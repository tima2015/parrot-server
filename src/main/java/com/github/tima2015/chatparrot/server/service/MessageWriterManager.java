package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.api.MessageWriter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class MessageWriterManager {

    @Getter
    private final Set<MessageWriter> activeWriters;

    @Autowired
    public MessageWriterManager(Map<String, MessageWriter> writers, @Value("${history-writer.enabled:}") String enabled) {
        Set<MessageWriter> activeWriters = new HashSet<>();
        enabled = enabled.strip();
        String[] writersName = enabled.isEmpty() ? new String[0] : enabled.split(",");
        for (String writerName : writersName) {
            writerName = writerName.strip();
            if (!writers.containsKey(writerName)) {
                log.warn("Unknown writer ({}) ignored!", writerName);
                continue;
            }
            activeWriters.add(writers.get(writerName));
        }
        this.activeWriters = Collections.unmodifiableSet(activeWriters);
        if (activeWriters.isEmpty()) {
            log.warn("There are no active writers!");
        } else {
            log.debug("Active writers: {}", this.activeWriters);
        }
    }

}
