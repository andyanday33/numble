package standrews.cs5031.numble.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.data.Config;
import standrews.cs5031.numble.impl.NumbleModelEasyModeImpl;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
public class NumbleAPI {

    private int gameId = 0;
    private final Map<Integer, NumbleModel> games = new HashMap<>();
    
    @PostMapping("/game")
    public Integer startGame(
            @RequestParam(name = "mode", defaultValue = "EASY") String mode) {
        NumbleModel model = null;
        if (mode.equals("EASY")) {
            //randomly select an equation from data source
            //parse this equation to get rhs and lhs
            int rhs = 6;
            String lhs = "1+2+3";
            model = new NumbleModelEasyModeImpl(Config.EASY_MODE_NUM_OF_ROWS, Config.EASY_MODE_NUM_OF_COLS, rhs, lhs);
        } else if(mode.equals("HARD")) {

            //TODO create a hard mode game instance
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game mode doesnt exist");
        }
        games.put(++gameId, model);
        return gameId;
    }

    @GetMapping("/game/{id}")
    public NumbleModel getGame(@PathVariable int id) {
        NumbleModel model = games.get(id);
        if (model != null) {
            return model;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
    }

    @GetMapping("/game/{id}/rhs")
    public int getRhs(@PathVariable int id) {
        NumbleModel model = games.get(id);
        if (model != null) {
            if (model instanceof NumbleModelEasyModeImpl) {
                return ((NumbleModelEasyModeImpl) model).getRhs();
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not allow to get right hand side value");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
    }

    @PostMapping("/game/{id}/guess")
    public NumbleModel makeGuess(@PathVariable int id, @RequestParam(name = "guess") String guess) {
        NumbleModel model = games.get(id);
        if (model != null) {
            try {
                model.guess(guess);
                return model;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
    }
}
