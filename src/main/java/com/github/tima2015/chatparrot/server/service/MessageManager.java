package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
class MessageManager {

    private final MessageHistory messageHistory;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Autowired
    public MessageManager(MessageHistory messageHistory) {
        this.messageHistory = messageHistory;
    }

    public void receiveMessage(WebSocketSession session, String rawMsg) {
        Message message = null;
        try {
            message = objectMapper.readValue(rawMsg, Message.class);
        } catch (JacksonException e) {
            log.warn("Can't parse message from json. Ignore message and carry on.", e);
            return;
        }

        message.setTimestamp(LocalDateTime.now());

        messageHistory.receive(message);

        sink.tryEmitNext(objectMapper.writeValueAsString(message));
    }

    public Flux<String> getMessagesFlow() {
        return sink.asFlux();
    }
}
