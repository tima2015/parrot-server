package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Message;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface MessageWriter extends Closeable {
    void write(List<Message> messages) throws IOException;
}
