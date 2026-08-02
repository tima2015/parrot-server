package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Message;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MessageHistory {
    private final ConcurrentLinkedQueue<Message> history = new ConcurrentLinkedQueue<>();
    private final Set<MessageWriter> writers;
    @Getter
    private volatile int flushThreshold;
    private final AtomicInteger count = new AtomicInteger(0);

    @Autowired
    public MessageHistory(Set<MessageWriter> writers,  @Value("${history-writer.flush-threshold:100}") int flushThreshold) {
        this.writers = writers;
        this.flushThreshold = flushThreshold;
    }

    public void receive(Message message) {
        log.debug("message receive");
        history.add(message);
        if (count.incrementAndGet() >= flushThreshold) {
            flush();
        }
    }

    @PreDestroy
    @Scheduled(fixedDelayString = "${history-writer.flush-schedule:10000}")
    public synchronized void flush() {
        log.debug("flush() start");
        List<Message> tmp = new ArrayList<>();
        Message m;
        while ((m = history.poll()) != null) {
            tmp.add(m);
        }
        if (tmp.isEmpty()) {
            return;
        }
        tmp = Collections.unmodifiableList(tmp);
        for (MessageWriter writer : writers) {
            try {
                writer.write(tmp);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        count.addAndGet(-tmp.size());
        log.debug("flush() done");
    }

    public void setFlushThreshold(int flushThreshold) {
        if (flushThreshold < 0) {
            throw new IllegalArgumentException("flushThreshold value must be greater than or equal to 0!");
        }
        this.flushThreshold = flushThreshold;
    }
}
