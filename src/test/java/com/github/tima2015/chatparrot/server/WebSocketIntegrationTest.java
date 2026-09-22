package com.github.tima2015.chatparrot.server;

import com.github.tima2015.chatparrot.server.data.Message;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketClient client;
    URI uri = URI.create("ws://localhost:" + port + "/ws?token=super-secret-parrot-token");


    @BeforeEach
    void setUp() {
        client = new ReactorNettyWebSocketClient();
    }

    @Test
    void checkClientConnection() {
        Mono<Void> connectionMono = client.execute(uri, session -> session.receive().then());

        StepVerifier.create(connectionMono)
                .expectSubscription()
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }

    @Test
    void chatBetweenTwoPlayersShouldSucceed() {
        WebSocketClient another = new ReactorNettyWebSocketClient();

        String author0 = "Player 1";
        String content0 = "Hello World!";
        String author1 = "Player 2";
        String content1 = "Hello ParrotServer!";

        String jsonTemplate = """
            {
              "author": "%s",
              "content": "%s"
            }""";

        StepVerifier.create(
                client.execute(uri, session -> {
                    Mono<Void> send = session.send(Mono.just(session.textMessage(jsonTemplate.formatted(author0, content0))));

                    Mono<Void> receive = session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(this::asMessage) // парсим обратно в объект
                            .filter(m -> m.getAuthor().equals(author1 ))
                            .take(1)
                            .then();

                    return Mono.zip(send, receive).then();
                })
        ).expectComplete().verify(Duration.ofSeconds(5));

        StepVerifier.create(
                another.execute(uri, session -> {
                    Mono<Void> send = session.send(Mono.just(session.textMessage(jsonTemplate.formatted(author1, content1))));

                    Mono<Void> receive = session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(this::asMessage)
                            .filter(m -> m.getAuthor().equals(author0))
                            .take(1)
                            .then();
                    return Mono.zip(send, receive).then();
                })
        ).expectComplete().verify(Duration.ofSeconds(5));
    }

    private Message asMessage(String json) {
        try {
            return new ObjectMapper().readValue(json, Message.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
