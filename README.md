# Project Code
The API is comprised of 4 methods. Their functions and repsonse bodies are outlined here. (Note: For HARD mode, the GET method '/game/{id}/rhs' will only return the 403 forbidden error.)

**1:** To initalise the game, the '/game' POST method is used. The request body follows the following schema for game creation.
{
  "numRows": integer (EASY mode: default = 5, HARD mode: default=8)
  "numCols": integer (Easy mode: default = 5, HARD mode: default=10)
  "mode": string  (EASY or HARD)
}
The server will respond with the game id as the response body.
(NOTE: In current build version, only the default values of numRows and numCols have front end support (unless playing via CURL, post the default values.))

**2:** The GET method '/game/{id}' will return the game state. The response body will be of the form 

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

**3:** If playing on mode=EASY, the get method '/game/{id}/rhs' will return the target value (an integer) of the current game.


**4:** To make a guess, the post method '/game/{id}/guess' is used. 'guess' is a string that must match in length the number of columns declared at
the initalisation of the game.  The method will return the updated game state.

(running 'NumbleApplication' in the src directory and going to http://localhost:8080/swagger-ui/index.html#/numble-api/
will provide an interactive page for using the API methods.)


**HOW TO PLAY**

To launch the game, first clone the repository. Navigate to the 'backend' directory in the terminal and run 'gradle bootRun'. Then go to the browser and go to the URL 'http://localhost:63342/project-code/Numble/frontend/' to find the web interface. To run the entire test suite, run 'gradle test'.

Easy mode: In easy mode, the playing grid will have 5 rows and 5 columns. For each guess you MUST use all 5 columns. The target integer will be shown on the right hand side of the screen. Note that for easy mode, the input does not have to be mathematically equal to the target value, but the expression shoudlnt represent a non integer value. Once you have submitted your valid guess by pressing enter, characters that were in the correct position in the solution will be coloured green, characters that appear in the solution, but not in the position of your guess, will appear yellow. Characters that are not included in the solution at all will be marked grey. Guess the solution before you run out of rows to win the game.

Hard mode: In hardmode, the playing grid will have 8 rows and 10 columns. You must provide a valid mathematical expression with an equality operator '='. The left hand and right hand side of your guess must be equal to eachother. Once you have submitted your first valid guess, any hints that you recieve in terms of characters marked green or yellow MUST be included in your subsequent guesses, or else they will be invalid. Guess the solution string before you run out of rows to win the game! 

Enjoy! 
