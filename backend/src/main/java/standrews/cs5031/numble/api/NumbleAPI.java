package standrews.cs5031.numble.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import standrews.cs5031.numble.model.GameCreation;
import standrews.cs5031.numble.model.Guess;
import standrews.cs5031.numble.exception.MethodNotAvailableException;
import standrews.cs5031.numble.model.NumbleModel;
import standrews.cs5031.numble.data.Config;
import standrews.cs5031.numble.impl.NumbleModelEasyModeImpl;
import standrews.cs5031.numble.impl.NumbleModelHardModeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@CrossOrigin
@RestController
@OpenAPIDefinition(info = @Info(title = "Numble API",
        description = "This documents Restful APIs for Numble game",
        contact = @Contact(name = "CS5031 Group 7",
                url = "https://gitlab.cs.st-andrews.ac.uk/cs5031group07/project-code")
))
public class NumbleAPI {

    private int gameId = 0;
    private final Map<Integer, NumbleModel> games = new HashMap<>();
    Logger logger = Logger.getLogger(NumbleAPI.class.getName());

    @PostMapping("/game")
    @Operation(summary = "Creat a new game",
            description = "Create and store a new game with number of rows and cells and game mode")
    public Integer startGame(@RequestBody GameCreation gameCreation) {
        NumbleModel model = null;
        int numRows = gameCreation.getNumRows();
        int numCols = gameCreation.getNumCols();
        System.out.println("Rows: " + numRows + "Cols: " + numCols);
        if (gameCreation.getMode() == NumbleModel.Mode.EASY) {
            if (numRows == 0) {
                numRows = Config.EASY_MODE_NUM_OF_ROWS;
            }
            if (numCols == 0) {
                numCols = Config.EASY_MODE_NUM_OF_COLS;
            }
            try {
                model = new NumbleModelEasyModeImpl(numRows, numCols);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }

        } else if (gameCreation.getMode() == NumbleModel.Mode.HARD) {
            if (numRows == 0) {
                numRows = Config.HARD_MODE_NUM_OF_ROWS;
            }
            if (numCols == 0) {
                numCols = Config.HARD_MODE_NUM_OF_COLS;
            }
            try {
                model = new NumbleModelHardModeImpl(numRows, numCols);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game mode doesnt exist");
        }
        games.put(++gameId, model);
        logger.info("Game with id " + gameId + " is created.");
        return gameId;
    }

    @GetMapping("/game/{id}")
    @Operation(summary = "Get game model",
            description = "Request game model with given id, the model represents the state of this game")
    public NumbleModel getGame(@PathVariable int id) {
        NumbleModel model = games.get(id);
        if (model != null) {
            logger.info("Get game with id " + id + " : " + model);
            return model;
        }
        logger.severe("Game with id " + id + " is not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
    }

    @GetMapping("/game/{id}/rhs")
    @Operation(summary = "Get right hand side value",
            description = "Request right hand side value of the equation in easy mode")
    public int getRhs(@PathVariable int id) {
        NumbleModel model = games.get(id);
        if (model != null) {
            if (model instanceof NumbleModelEasyModeImpl) {
                int rhs = ((NumbleModelEasyModeImpl) model).getRhs();
                logger.info("Get rhs of game " + id + " : " + rhs);
                return rhs;
            }
            logger.severe("Game in this mode is not allowed to get rhs");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allow to get right hand side value");
        }
        logger.severe("Game with id " + id + " is not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
    }

    @PostMapping("/game/{id}/guess")
    @Operation(summary = "Check if the input guess is right solution",
            description = "The input guess could be invalid, and no more guess can be made when game is over")
    public NumbleModel makeGuess(@PathVariable int id, @RequestBody Guess guess) {
        NumbleModel model = games.get(id);
        if (model != null) {
            try {
                model.guess(guess.getExpression());
                logger.info("Made a valid guess: " + guess.getExpression());
                return model;
            } catch (IllegalArgumentException e) {
                logger.severe(e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            } catch (MethodNotAvailableException e) {
                logger.severe(e.getMessage());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
            }
        }
        logger.severe("Game with id " + id + " is not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
    }
}
