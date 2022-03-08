package standrews.cs5031.numble.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import standrews.cs5031.numble.GameCreation;
import standrews.cs5031.numble.Guess;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.Config;

public class NumbleAPITests {
    WebTestClient client;

    @BeforeEach
    void setup() {
        client = WebTestClient.bindToController(new NumbleAPI()).build();
    }

    @Test
    void createGameWithCustomisedNumRowsAndCols() {
        GameCreation gameCreation = new GameCreation(2, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");
    }

    @Test
    void createGameFailsWhenNoEquationDataWithDesiredLength() {
        GameCreation gameCreation = new GameCreation(2, 10, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isNotFound();
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
    void createEasyGameWithDefaultConfigData() {
        GameCreation gameCreation = new GameCreation(0, 0, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");
        client.get().uri("/game/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mode").isEqualTo(NumbleModel.Mode.EASY.toString())
                .jsonPath("$.numRows").isEqualTo(Config.EASY_MODE_NUM_OF_ROWS)
                .jsonPath("$.numCols").isEqualTo(Config.EASY_MODE_NUM_OF_COLS);
    }

    @Test
    void createHardGameWithDefaultConfigData() {
        GameCreation gameCreation = new GameCreation(0, 0, NumbleModel.Mode.HARD);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");
        client.get().uri("/game/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.mode").isEqualTo(NumbleModel.Mode.HARD.toString())
                .jsonPath("$.numRows").isEqualTo(Config.HARD_MODE_NUM_OF_ROWS)
                .jsonPath("$.numCols").isEqualTo(Config.HARD_MODE_NUM_OF_COLS);
    }

    @Test
    public void requestGameFailWhenNoSuchGameID() {
        client.get().uri("/game/1").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void requestRhsForEasyModeGame() {
        GameCreation gameCreation = new GameCreation(0, 0, NumbleModel.Mode.EASY);

        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");

        client.get().uri("/game/1/rhs").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void requestRhsForbiddenForHardModeGame() {
        GameCreation gameCreation = new GameCreation(0, 0, NumbleModel.Mode.HARD);

        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("1");

        client.get().uri("/game/1/rhs").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void requestRhsNotFoundWhenGameIDNotExist() {
        client.get().uri("/game/1/rhs").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
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

    @Test
    void madeInValidGuess() {
        GameCreation gameCreation = new GameCreation(2, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange();

        Guess validGuess = new Guess("3++");
        client.post().uri("/game/1/guess").accept(MediaType.APPLICATION_JSON)
                .bodyValue(validGuess)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void guessNotAllowedAfterGameOver() {
        GameCreation gameCreation = new GameCreation(1, 3, NumbleModel.Mode.EASY);
        client.post().uri("/game")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(gameCreation)
                .exchange();

        //Make a wrong guess to lose the game.
        //"3+1" is not in equation data source.
        Guess validGuess = new Guess("3+1");
        client.post().uri("/game/1/guess").accept(MediaType.APPLICATION_JSON)
                .bodyValue(validGuess)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.lost").isEqualTo(true);

        //Make another guess
        client.post().uri("/game/1/guess").accept(MediaType.APPLICATION_JSON)
                .bodyValue(validGuess)
                .exchange()
                .expectStatus().isForbidden();
    }

}