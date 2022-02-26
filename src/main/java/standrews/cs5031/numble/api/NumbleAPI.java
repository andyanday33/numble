package standrews.cs5031.numble.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import standrews.cs5031.numble.NumbleModel;
import standrews.cs5031.numble.impl.NumbleModelImpl;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NumbleAPI {

    private int gameId = 0;
    private final Map<Integer, NumbleModel> games = new HashMap<>();

    @GetMapping("/api")
    public String apiDescription() {
        return "return a description of the API in the future";
    }

    @PostMapping("/game")
    public Integer startGame(
            @RequestParam(name = "cols", defaultValue = "5") int cols,
            @RequestParam(name = "rows", defaultValue = "5") int rows,
            @RequestParam(name = "rhs", defaultValue = "16") int rhs,
            @RequestParam(name = "lhs", defaultValue = "4+4*2") String lhs
    ) {
        NumbleModel model = new NumbleModelImpl(cols, rows, rhs, lhs);
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

    @PostMapping("/game/{id}/guess")
    public NumbleModel makeGuess(@PathVariable int id, @RequestParam(name = "guess") String guess) {
        NumbleModel model = games.get(id);
        if (model != null) {
            model.guess(guess);
            return model;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
    }
}
