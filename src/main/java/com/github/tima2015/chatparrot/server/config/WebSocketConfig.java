package com.github.tima2015.chatparrot.server.config;

import com.github.tima2015.chatparrot.server.component.ParrotWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
class WebSocketConfig {

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", new ParrotWebSocketHandler());
        int order = -1;

        return new SimpleUrlHandlerMapping(map, order);
    }
}
