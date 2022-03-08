# Project Code
The API is comprised of 4 methods. Their functions and repsonse bodies are outlined here. (Note: For HARD mode, the GET method '/game/{id}/rhs' will only return the 403 forbidden error.)

1: To initalise the game, the '/game' POST method is used. The request body follows the following schema for game creation.
{
  "numRows": integer (EASY mode: default = 6, HARD mode: default=8)
  "numCols": integer (Easy mode: default = 7, HARD mode: default=6)
  "mode": string  (EASY or HARD)
}
The server will respond with the game id as the response body.
(NOTE: In current build version, only the default values of numRows and numCols have front end support (unless playing via CURL, post the default values.))

2: The GET method '/game/{id}' will return the game state. The response body will be of the form 

NumbleModel{
numCols	integer
mode	string
numRows	integer
cells	[...]
numberOfGuessMade integer
lost	boolean
won	boolean
} 

Where each cell represents the intersection of a row and column. The cell data is in the form:

Cell{
col	integer
row	integer
guessChar string
state	string
Enum:
Array [ 4 ]
}

The 4 possible states of a cell are either INIT (initialised) CORRECT (the provided guessed character in this cell matches the solution)
WRONG_POSITION (the guessed character is in the final solution, but in a different row, and NOT_EXIST, where the guessed character is not
found in the final solution.

3: If playing on mode=EASY, the get method '/game/{id}/rhs' will return the target value (an integer) of the current game.


4: To make a guess, the post method '/game/{id}/guess' is used. 'guess' is a string that must match in length the number of columns declared at
the initalisation of the game.  The method will return the updated game state.

(running 'NumbleApplication' in the src directory and going to http://localhost:8080/swagger-ui/index.html#/numble-api/
will provide an interactive page for using the API methods.)
