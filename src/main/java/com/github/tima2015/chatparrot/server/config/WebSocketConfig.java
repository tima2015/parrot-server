package com.github.tima2015.chatparrot.server.config;

import com.github.tima2015.chatparrot.server.component.ParrotWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping handlerMapping(ParrotWebSocketHandler handler) {
        int order = -1;
        return new SimpleUrlHandlerMapping(Map.of("/ws", handler), order);
    }
}
