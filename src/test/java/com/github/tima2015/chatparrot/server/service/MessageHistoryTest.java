package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.api.MessageWriter;
import com.github.tima2015.chatparrot.server.data.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageHistoryTest {

    @Mock
    private MessageWriter mockWriter1;

    @Mock
    private MessageWriter mockWriter2;

    private MessageHistory messageHistory;

    @BeforeEach
    void setUp() {
        Map<String, MessageWriter> writers = Map.of("mockWriter1", mockWriter1, "mockWriter2", mockWriter2);
        MessageWriterManager manager = new MessageWriterManager(writers, "mockWriter1, mockWriter2");
        int testThreshold = 3;
        messageHistory = new MessageHistory(manager, testThreshold);
    }

    @Test
    @DisplayName("Checking validation on setting flushThreshold value via constructor and setter")
    void checkFlushThreshold() {
        MessageWriterManager manager = new MessageWriterManager(new HashMap<>(), "");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MessageHistory(manager, -1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageHistory.setFlushThreshold(-1));
        messageHistory.setFlushThreshold(0);
    }

    @Test
    @DisplayName("Collect messages counted less that flushThreshold. flush() must not call")
    void shouldAccumulateMessagesWithoutFlushing() {
        for (int i = 1; i < messageHistory.getFlushThreshold(); i++) {
            messageHistory.receive(new Message(null, null, null, null, null));
        }
        verifyNoInteractions(mockWriter1, mockWriter2);
    }

    @Test
    @DisplayName("Collect messages counted equal flushThreshold. flush() must call")
    void shouldFlushAutomaticallyWhenThresholdReached() throws IOException {
        List<Message> expectedList = new ArrayList<>();
        for (int i = 1; i <= messageHistory.getFlushThreshold(); i++) {
            Message msg = new Message(null, null, null, null, null);
            expectedList.add(msg);
            messageHistory.receive(msg);
        }

        verify(mockWriter1, times(1)).write(expectedList);
        verify(mockWriter2, times(1)).write(expectedList);
    }

    @Test
    @DisplayName("Flush immediately when threshold is 0")
    void shouldFlushImmediatelyWhenThresholdZero() throws IOException {
        messageHistory.setFlushThreshold(0);
        List<Message> expectedList = new ArrayList<>();
        Message msg = new Message(null, null, null, null, null);
        expectedList.add(msg);
        messageHistory.receive(msg);
        verify(mockWriter1, times(1)).write(expectedList);
        verify(mockWriter2, times(1)).write(expectedList);
    }

    @Test
    @DisplayName("Manual flush message")
    void shouldFlushManually() throws IOException {
        Message msg = new Message(null, null, null, null, null);
        messageHistory.receive(msg);
        messageHistory.flush();
        verify(mockWriter1, times(1)).write(List.of(msg));
        verify(mockWriter2, times(1)).write(List.of(msg));
    }

    @Test
    @DisplayName("Flush with empty queue don't trigger writers")
    void shouldDoNothingOnFlushIfHistoryIsEmpty() {
        messageHistory.flush();
        verifyNoInteractions(mockWriter1, mockWriter2);
    }

    @Test
    @DisplayName("Exception in one of writers don't break process")
    void shouldContinueFlushingIfOneWriterFails() throws IOException {
        Message msg = new Message(null, null, null, null, null);
        messageHistory.receive(msg);

        doThrow(new RuntimeException("Test error")).when(mockWriter1).write(anyList());
        messageHistory.flush();

        verify(mockWriter1, times(1)).write(anyList());
        verify(mockWriter2, times(1)).write(anyList());
    }
}