package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.concurrent.locks.LockSupport;

@Service
@Slf4j
public class MessageManager {


    /**
     * Sinks.Many does not serialize emission; under concurrent writes
     * one thread gets FAIL_NON_SERIALIZED. A short retry is used instead
     * of busyLooping() so the event loop is not blocked.
     */
    private static final Sinks.EmitFailureHandler EMIT_HANDLER = (signalType, emitResult) -> {
        if (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
            log.warn("Failed to emit message: {}. Try again in 10ns...", emitResult);
            LockSupport.parkNanos(10);
            return true;
        }
        log.warn("Failed to emit message: {}", emitResult);
        return Sinks.EmitFailureHandler.FAIL_FAST.onEmitFailure(signalType, emitResult);
    };

    private final ClientManager clientManager;
    private final MessageHistory messageHistory;
    private final ObjectMapper objectMapper;
    private final Sinks.Many<Message> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Autowired
    public MessageManager(ClientManager clientManager, MessageHistory messageHistory, ObjectMapper objectMapper) {
        this.clientManager = clientManager;
        this.messageHistory = messageHistory;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> receiveMessage(WebSocketSession session, WebSocketMessage msg) {
        Message message = null;
        try {
            message = objectMapper.readValue(msg.getPayloadAsText(), Message.class);
        } catch (JacksonException e) {
            log.warn("Can't parse message from json. Ignore message and carry on.", e);
            return Mono.empty();
        }

        message.setTimestamp(LocalDateTime.now());
        message.setSourceId(clientManager.getClient(session).getSourceId());

        messageHistory.receive(message);

        sink.emitNext(message, EMIT_HANDLER);
        return Mono.empty();
    }

    public Flux<Message> getMessagesFlow() {
        return sink.asFlux();
    }
}
