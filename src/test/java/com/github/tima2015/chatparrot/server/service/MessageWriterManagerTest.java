package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.api.MessageWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageWriterManagerTest {
    @Mock
    private MessageWriter fileWriter;

    @Mock
    private MessageWriter dbWriter;

    @Mock
    private MessageWriter otherWriter;

    private Map<String, MessageWriter> writers;

    @BeforeEach
    void setUp() {
        writers = Map.of("fileWriter", fileWriter, "dbWriter", dbWriter, "otherWriter", otherWriter);
    }

    @Test
    @DisplayName("Should not activate any writers when enabled property is empty")
    void shouldActivateAllWritersWhenEnabledIsEmpty() {
        MessageWriterManager manager = new MessageWriterManager(writers, "");

        assertTrue(manager.getActiveWriters().isEmpty());
    }

    @Test
    @DisplayName("Should activate only specified writers")
    void shouldActivateOnlySpecifiedWriters() {
        MessageWriterManager manager = new MessageWriterManager(writers, "fileWriter,dbWriter");

        assertEquals(2, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
        assertTrue(manager.getActiveWriters().contains(dbWriter));
        assertFalse(manager.getActiveWriters().contains(otherWriter));
    }

    @Test
    @DisplayName("Should ignore unknown writer names and log warning")
    void shouldIgnoreUnknownWriterNames() {
        MessageWriterManager manager = new MessageWriterManager(writers, "fileWriter,unknownWriter,dbWriter");

        assertEquals(2, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
        assertTrue(manager.getActiveWriters().contains(dbWriter));
        assertFalse(manager.getActiveWriters().contains(otherWriter));
    }

    @Test
    @DisplayName("Should handle whitespace in enabled property")
    void shouldHandleWhitespace() {
        MessageWriterManager manager = new MessageWriterManager(writers, " fileWriter , dbWriter , ");

        assertEquals(2, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
        assertTrue(manager.getActiveWriters().contains(dbWriter));
    }

    @Test
    @DisplayName("Should return empty set when no writers match")
    void shouldReturnEmptySetWhenNoWritersMatch() {
        MessageWriterManager manager = new MessageWriterManager(writers, "unknown1,unknown2");

        assertTrue(manager.getActiveWriters().isEmpty());
    }

    @Test
    @DisplayName("Should return unmodifiable set")
    void shouldReturnUnmodifiableSet() {
        MessageWriterManager manager = new MessageWriterManager(writers, "fileWriter,dbWriter");

        assertThrows(UnsupportedOperationException.class, () -> manager.getActiveWriters().add(otherWriter));
    }

    @Test
    @DisplayName("Should handle null writers map gracefully")
    void shouldHandleNullWritersMap() {
        assertThrows(NullPointerException.class, () -> new MessageWriterManager(null, "fileWriter"));
    }

    @Test
    @DisplayName("Should work with single writer")
    void shouldWorkWithSingleWriter() {
        Map<String, MessageWriter> singleWriter = Map.of("fileWriter", fileWriter);

        MessageWriterManager manager = new MessageWriterManager(singleWriter, "fileWriter");

        assertEquals(1, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
    }

    @Test
    @DisplayName("Should ignore duplicate writer names in enabled property")
    void shouldIgnoreDuplicateWriterNames() {
        MessageWriterManager manager = new MessageWriterManager(writers, "fileWriter,fileWriter,dbWriter");

        assertEquals(2, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
        assertTrue(manager.getActiveWriters().contains(dbWriter));
    }

    @Test
    @DisplayName("Should not activate writers with unknown names even if partially valid")
    void shouldNotActivateUnknownWritersEvenIfPartiallyValid() {
        MessageWriterManager manager = new MessageWriterManager(writers, "fileWriter,nonExistent,otherWriter");

        assertEquals(2, manager.getActiveWriters().size());
        assertTrue(manager.getActiveWriters().contains(fileWriter));
        assertTrue(manager.getActiveWriters().contains(otherWriter));
        assertFalse(manager.getActiveWriters().contains(dbWriter));
    }
}