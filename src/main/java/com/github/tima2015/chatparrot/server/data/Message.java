package com.github.tima2015.chatparrot.server.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table
public class Message {
    @Id
    private Long id;
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

    public Message(Long id, String sourceId, String channelId, String author, String content, LocalDateTime timestamp) {
        this(sourceId, channelId, author, content, timestamp);
        this.id = id;
    }

    public Message() {
    }
}
