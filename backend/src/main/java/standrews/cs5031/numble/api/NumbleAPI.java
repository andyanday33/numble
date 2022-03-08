package standrews.cs5031.numble.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import standrews.cs5031.numble.GameCreation;
import standrews.cs5031.numble.Guess;
import standrews.cs5031.numble.MethodNotAvailableException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.Config;
import standrews.cs5031.numble.impl.NumbleModelEasyModeImpl;
import standrews.cs5031.numble.impl.NumbleModelHardModeImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@CrossOrigin
@RestController
public class NumbleAPI {

    private int gameId = 0;
    private final Map<Integer, NumbleModel> games = new HashMap<>();
    Logger logger = Logger.getLogger(NumbleAPI.class.getName());
    
    @PostMapping("/game")
    public Integer startGame(@RequestBody GameCreation gameCreation) {
        NumbleModel model = null;
        int numRows = gameCreation.getNumRows();
        int numCols = gameCreation.getNumCols();
        if (gameCreation.getMode() == NumbleModel.Mode.EASY) {
            if (numRows == 0) {
                numRows = Config.EASY_MODE_NUM_OF_ROWS;
            }
            if (numCols == 0) {
                numCols = Config.EASY_MODE_NUM_OF_COLS;
            }
            try {
                model = new NumbleModelEasyModeImpl(numRows, numCols);
            } catch (IllegalArgumentException e){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }

        } else if(gameCreation.getMode() == NumbleModel.Mode.HARD) {
            if (numRows == 0) {
                numRows = Config.HARD_MODE_NUM_OF_ROWS;
            }
            if (numCols == 0) {
                numCols = Config.HARD_MODE_NUM_OF_COLS;
            }
            try {
                model = new NumbleModelHardModeImpl(numRows, numCols);
            } catch (IllegalArgumentException e){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game mode doesnt exist");
        }
        games.put(++gameId, model);
        logger.info("Game with id " + gameId + " is created.");
        return gameId;
    }

    @GetMapping("/game/{id}")
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
