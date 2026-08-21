package com.github.tima2015.chatparrot.server.api;

import com.github.tima2015.chatparrot.server.data.Message;

import java.io.IOException;
import java.util.List;

public interface MessageWriter {
    void write(List<Message> messages) throws IOException;
}
