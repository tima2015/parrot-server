package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ClientManager {

    private final Map<WebSocketSession, Client> clientMap = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, Client> historyMap = new ConcurrentHashMap<>();

    public Client getClient(WebSocketSession session) {
        return clientMap.getOrDefault(session, historyMap.get(session));
    }
    public Client registerSession(WebSocketSession session) {
        Client client = new Client();//todo authorization
        clientMap.put(session, client);
        return client;
    }

    public void unregisterSession(WebSocketSession session) {
        if (!clientMap.containsKey(session)) {
            log.warn("Session {} alredy unregister", session);
            return;
        }
        historyMap.put(session, clientMap.remove(session));
    }
}
