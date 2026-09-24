package com.github.tima2015.chatparrot.server;

import com.github.tima2015.chatparrot.server.data.Message;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketClient client;
    private URI uri;


    @BeforeEach
    void setUp() {
        client = new ReactorNettyWebSocketClient();
        uri = URI.create("ws://localhost:" + port + "/ws?token=super-secret-parrot-token");
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
        WebSocketClient client1 = new ReactorNettyWebSocketClient();
        WebSocketClient client2 = new ReactorNettyWebSocketClient();

        String author0 = "Player 1";
        String content0 = "Hello World!";
        String author1 = "Player 2";
        String content1 = "Hello ParrotServer!";

        String jsonTemplate = """
        {
          "author": "%s",
          "content": "%s"
        }""";

        Sinks.One<Void> client1Ready = Sinks.one();
        Sinks.One<Void> client2Ready = Sinks.one();

        Mono<Void> session1 = client1.execute(uri, session -> {
            client1Ready.tryEmitEmpty();

            return Mono.when(client1Ready.asMono(), client2Ready.asMono())
                    .then(session.send(Mono.just(session.textMessage(
                            jsonTemplate.formatted(author0, content0)))))
                    .and(session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(this::asMessage)
                            .filter(m -> m.getAuthor().equals(author1)
                                    && m.getContent().equals(content1))
                            .take(1)
                            .then());
        });

        Mono<Void> session2 = client2.execute(uri, session -> {
            client2Ready.tryEmitEmpty();

            return Mono.when(client1Ready.asMono(), client2Ready.asMono())
                    .then(session.send(Mono.just(session.textMessage(
                            jsonTemplate.formatted(author1, content1)))))
                    .and(session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
                            .map(this::asMessage)
                            .filter(m -> m.getAuthor().equals(author0)
                                    && m.getContent().equals(content0))
                            .take(1)
                            .then());
        });

        StepVerifier.create(Mono.when(session1, session2))
                .expectComplete()
                .verify(Duration.ofSeconds(5));
    }

    private Message asMessage(String json) {
        try {
            return new ObjectMapper().readValue(json, Message.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
