package com.github.tima2015.chatparrot.server.component;

import com.github.tima2015.chatparrot.server.data.Message;
import com.github.tima2015.chatparrot.server.service.MessageWriter;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class FileMessageWriter implements MessageWriter {

    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String MESSAGE_FORMAT = "[%s][%s][%s][%s][%s][%s]";

    private final File output;

    @Autowired
    public FileMessageWriter(@Value("${history-writer.file.path:./history/}") String historyPath) {
        try {
            File path = new File(historyPath);
            if (!path.exists() && !path.mkdirs()) {
                throw new IOException("Failed to create directory: " + historyPath);
            }
            String fileName = historyPath + LocalDateTime.now().format(FILE_NAME_FORMATTER) + ".txt";
            this.output = new File(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize FileMessageWriter", e);
        }
    }

    FileMessageWriter(File output) {
        this.output = output;
    }

    private String formatMessage(Message message) {
        return MESSAGE_FORMAT.formatted(
                message.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                message.getId(),
                message.getSourceId(),
                message.getChannelId(),
                message.getAuthor(),
                message.getContent()
        );
    }

    @Override
    public void write(List<Message> messages) throws IOException{
        List<String> lines = messages.stream().map(this::formatMessage).toList();
        Files.write(output.toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @PreDestroy
    @Override
    public void close() {
        // for future optimization
    }
}
