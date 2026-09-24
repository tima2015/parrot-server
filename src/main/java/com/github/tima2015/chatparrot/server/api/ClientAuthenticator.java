package com.github.tima2015.chatparrot.server.api;

import com.github.tima2015.chatparrot.server.data.Client;
import org.springframework.web.reactive.socket.WebSocketSession;

public interface ClientAuthenticator {
    Client authenticate(WebSocketSession session);
}
