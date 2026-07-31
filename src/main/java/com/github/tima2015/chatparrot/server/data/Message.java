package com.github.tima2015.chatparrot.server.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private final String id;
    private final String sourceId;
    private final String channel;
    private final String content;
    private final LocalDateTime timestamp;
}
