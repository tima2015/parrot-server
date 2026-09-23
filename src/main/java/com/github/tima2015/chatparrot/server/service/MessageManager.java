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
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MessageManager {


    private final MessageHistory messageHistory;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final ObjectMapper objectMapper;
    private final Sinks.Many<Message> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @Autowired
    public MessageManager(MessageHistory messageHistory) {
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
            return Mono.error(e);
        }

        message.setTimestamp(LocalDateTime.now());
        message.setSourceId(clientManager.getClient(session).getSourceId());

        messageHistory.receive(message);

        sink.tryEmitNext(objectMapper.writeValueAsString(message));
        return Mono.empty();
    }

    public Flux<Message> getMessagesFlow() {
        return sink.asFlux();
    }
}
