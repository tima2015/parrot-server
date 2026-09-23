package com.github.tima2015.chatparrot.server.component;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ParrotWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<Void> input = session.receive().then();
        Flux<String> source = Flux.empty();
        Mono<Void> output = session.send(source.map(session::textMessage));
        return input.and(output);
    }
}
