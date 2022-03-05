package standrews.cs5031.numble.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import standrews.cs5031.numble.api.NumbleAPI;

public class NumbleAPITests {
    WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToController(new NumbleAPI()).build();
    }
    @Test
    void createGame() {
        client.post().uri("/game?mode=EASY")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");
    }
    @Test
    void checkFailedGame() {
        client.post().uri("/game?mode=FAIL")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
        //.expectBody().json("1");
    }
    @Test
    void checkCorrectGameMode() {
        client.post().uri("/game?mode=EASY").exchange();
        client.get().uri("/game/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mode").isEqualTo("EASY");
        //.expectBody().json("1");
    }
}