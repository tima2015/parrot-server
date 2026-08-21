package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.data.Message;
import com.github.tima2015.chatparrot.server.data.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DBMessageWriterTest {
    @Mock
    private MessageRepository messageRepository;

    private DBMessageWriter dbMessageWriter;

    @BeforeEach
    void setUp() {
        dbMessageWriter = new DBMessageWriter(messageRepository);
    }

    @Test
    @DisplayName("Should save messages successfully")
    void shouldSaveMessagesSuccessfully() throws IOException {
        Message msg1 = new Message("src1", "ch1", "author1", "Hello!", LocalDateTime.now());
        Message msg2 = new Message("src2", "ch2", "author2", "World!", LocalDateTime.now());
        List<Message> messages = List.of(msg1, msg2);

        when(messageRepository.saveAll(messages)).thenReturn(Flux.just(msg1, msg2));

        dbMessageWriter.write(messages);

        verify(messageRepository, times(1)).saveAll(messages);
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    @DisplayName("Should throw IOException when repository fails")
    void shouldThrowIOExceptionWhenRepositoryFails() {
        Message msg = new Message("src1", "ch1", "author1", "Hello!", LocalDateTime.now());
        List<Message> messages = List.of(msg);

        when(messageRepository.saveAll(messages)).thenReturn(Flux.error(new RuntimeException("Database connection failed")));

        IOException exception = assertThrows(IOException.class, () -> dbMessageWriter.write(messages));

        assertTrue(exception.getMessage().contains("Failed to save messages to DB"));
        assertNotNull(exception.getCause());
        assertEquals("Database connection failed", exception.getCause().getMessage());

        verify(messageRepository, times(1)).saveAll(messages);
    }

    @Test
    @DisplayName("Should handle empty list without errors")
    void shouldHandleEmptyList() throws IOException {
        List<Message> emptyList = List.of();

        when(messageRepository.saveAll(emptyList)).thenReturn(Flux.empty());

        dbMessageWriter.write(emptyList);

        verify(messageRepository, times(1)).saveAll(emptyList);
    }
}