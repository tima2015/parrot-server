package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Client;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
class ClientManager {

    private Map<WebSocketSession, Client> clientMap = new ConcurrentHashMap<>();

    public Map<WebSocketSession, Client> getClientMap() {
        return Collections.unmodifiableMap(clientMap);
    }
}
