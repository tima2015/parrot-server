package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.service.ClientManager;
import com.github.tima2015.chatparrot.server.service.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component
public class ParrotWebSocketHandler implements WebSocketHandler {

    private final ClientManager clientManager;
    private final MessageManager messageManager;
    private final ObjectMapper objectMapper;


    @Autowired
    public ParrotWebSocketHandler(ClientManager clientManager, MessageManager messageManager, ObjectMapper objectMapper) {
        this.clientManager = clientManager;
        this.messageManager = messageManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        clientManager.registerSession(session);
        Mono<Void> input = session.receive().concatMap(msg -> messageManager.receiveMessage(session, msg)).then();
        Mono<Void> output = session.send(messageManager.getMessagesFlow().map(msg -> session.textMessage(objectMapper.writeValueAsString(msg))));
        return input.and(output).doFinally(s -> clientManager.unregisterSession(session));
    }
}
