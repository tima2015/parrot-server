package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.api.ClientAuthenticator;
import com.github.tima2015.chatparrot.server.data.Client;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;

@Component
public class AnonymousClientAuthenticator implements ClientAuthenticator {
    @Override
    public Client authenticate(WebSocketSession session) {
        Client client = new Client();
        client.setSourceId(session.getId());
        return client;
    }
}
