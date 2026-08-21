package com.github.tima2015.chatparrot.server.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private long id;
    private String sourceId;
    private String channelId;
    private String author;
    private String content;
    private LocalDateTime timestamp;

    public Message(String sourceId, String channelId, String author, String content, LocalDateTime timestamp) {
        this.sourceId = sourceId;
        this.channelId = channelId;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(long id, String sourceId, String channelId, String author, String content, LocalDateTime timestamp) {
        this(sourceId, channelId, author, content, timestamp);
        this.id = id;
    }
}
