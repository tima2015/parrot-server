package com.github.tima2015.chatparrot.server.data.repository;

import com.github.tima2015.chatparrot.server.data.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {
}
