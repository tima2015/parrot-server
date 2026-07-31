package com.github.tima2015.chatparrot.server.service;

import com.github.tima2015.chatparrot.server.data.Message;

import java.util.List;

public interface MessageWriter {
    void write(List<Message> messages);
}
