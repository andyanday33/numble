package standrews.cs5031.numble.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import standrews.cs5031.numble.GameCreation;
import standrews.cs5031.numble.Guess;
import standrews.cs5031.numble.NumbleModel;

public class NumbleAPITests {
    WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToController(new NumbleAPI()).build();

    }

    @Test
    void createGame() {
        GameCreation gameCreation = new GameCreation(2, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");
    }

    @Test
    void checkCorrectGameMode() {
        GameCreation gameCreation = new GameCreation(2, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange();
        client.get().uri("/game/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mode").isEqualTo(NumbleModel.Mode.EASY.toString());
    }

    @Test
    void madeValidGuess() {
        GameCreation gameCreation = new GameCreation(2, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange();

        Guess validGuess = new Guess("3+2");
        client.post().uri("/game/1/guess").accept(MediaType.APPLICATION_JSON)
                .bodyValue(validGuess)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.numberOfGuessMade").isEqualTo(1);
    }

}