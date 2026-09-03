package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.data.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileMessageWriterTest {


    private FileMessageWriter writer;
    private final File testOutputFile = new File("FileMessageWriterTest.tmp");

    @BeforeEach
    void setUp() {
        writer = new FileMessageWriter(testOutputFile);
    }

    @AfterEach
    void tearDown() {
        if (testOutputFile.exists()) {
            assertTrue(testOutputFile.delete());
        }
    }

    @Test
    @DisplayName("Check history file creates")
    void checkHistoryFileCreate() throws IOException {
        writer.write(List.of());
        assertTrue(testOutputFile.exists());
    }

    @Test
    @DisplayName("Check that empty lists not append empty lines to file")
    void checkEmptyListNotAppendLines() throws IOException {
        writer.write(List.of());
        writer.write(List.of());
        List<String> lines = Files.readAllLines(testOutputFile.toPath());
        assertEquals(0, lines.size());
    }

    @Test
    @DisplayName("Single message formatted correctly")
    void singleMessageFormattedCorrectly() throws IOException {
        Message msg = new Message("src", "ch1", "author", "Hello!", LocalDateTime.MIN);
        writer.write(List.of(msg));
        String text = Files.readString(testOutputFile.toPath());
        assertTrue(text.contains("src"));
        assertTrue(text.contains("ch1"));
        assertTrue(text.contains("author"));
        assertTrue(text.contains("Hello!"));
    }

    @Test
    @DisplayName("Check writing after reopened file is correct")
    void dataContinueWriteCorrect() throws IOException {
        Message msg0 = new Message("src1", "ch1", "author1", "Hello!", LocalDateTime.MIN);
        Message msg1 = new Message("src2","ch2" ,"author2", "🤓", LocalDateTime.MIN);

        writer.write(List.of(msg0));
        writer = new FileMessageWriter(testOutputFile);
        writer.write(List.of(msg1));
        String text = Files.readString(testOutputFile.toPath());
        assertTrue(text.contains("src1"));
        assertTrue(text.contains("ch1"));
        assertTrue(text.contains("author1"));
        assertTrue(text.contains("Hello!"));
        assertTrue(text.contains("src2"));
        assertTrue(text.contains("ch2"));
        assertTrue(text.contains("author2"));
        assertTrue(text.contains("🤓"));
    }

}