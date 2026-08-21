package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.api.MessageWriter;
import com.github.tima2015.chatparrot.server.data.Message;
import com.github.tima2015.chatparrot.server.data.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class DBMessageWriter implements MessageWriter {

    private final MessageRepository rep;

    public DBMessageWriter(MessageRepository rep) {
        this.rep = rep;
    }

    @Override
    public void write(List<Message> messages) throws IOException {
        try {
            rep.saveAll(messages).collectList().block();
        } catch (Exception e) {
            throw new IOException("Failed to save messages to DB", e);
        }
    }
}
