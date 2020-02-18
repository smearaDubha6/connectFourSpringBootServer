package connectFourServer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the board of the Connect 4 game. It handles recording
 * user choices and checking for collisions and has the logic to check for the
 * game winner. It also returns the various board related messages to be sent
 * back to the client 
 *
 * @author Richie Duggan
 */
public class Board {
	
    public static final int NUM_ROWS = 6;
    public static final int NUM_COLS = 9;
    public static final int WINNING_SCORE = 5;
    
    private static int numPlayers = 0;
    
    // create 2D array that records state of game
    private  char[][] board = new char[NUM_ROWS][NUM_COLS];
    // record how many spaces have been filled in each column
    private  int[] boardColIndex = new int[NUM_COLS];
    
    private static boolean colourChosen = false; 
    public static int colourChosenInt = 0;
    
    public static final int YELLOW = 1;
    public static final int RED = 2;
    
    private final static Map < Integer, String > colourTextMapping = new HashMap < > () {
        {
            put(YELLOW, "yellow");
            put(RED, "red");
        }
    };

    private final static Map < Integer, Character > numberMapping = new HashMap < > () {
        {
            put(YELLOW, 'x');
            put(RED, 'o');
        }
    };
    
    // record who has the current go
    private static int currentGo = 1;
    
    // record if the game is over
    private boolean gameOver = false;
    private int winningColour = -1;
    private String winningName = "";
    
    private long lastRecordedMove;
    
    // TODO : instead of hardcoding this, instead read it from 
    // application.properties via ConfigProperties class
    public static final int ALLOWED_GO_MINUTES = 10;
    
    public Board() {}
    
    /* The following are various getters and setters required
     * for operating the board
     */
    
    public void setColourChosen(boolean colourChosenState) {
    	colourChosen = colourChosenState;
    }
    
    public boolean isColourChosen() {
    	return colourChosen;
    }
    
    public void setColourChosenInt(int colour) {
    	colourChosenInt = colour;
    }
    
    public int getColourChosenInt() {
    	return colourChosenInt;
    }
    
    public boolean isAcceptingPlayers() {
    	return (numPlayers < 2);
    }
    
    public int getNumPlayers() {
    	return numPlayers;
    }
    
    public void increaseNumPlayers() {
    	numPlayers = numPlayers + 1;
    }
    
    public int getCurrentGo() {
    	return currentGo;
    }
    
    public boolean getGameOver() {
    	return gameOver;
    }
    
    public void setGameOver(boolean over) {
    	gameOver = over;
    }
    
    public void setWinningColour(int colour) {
    	winningColour = colour;
    }
    
    public int getWinningColour() {
    	return winningColour;
    }
    
    public void setWinningName(String name) {
    	winningName = name;
    }
    
    public String getWinningName() {
    	return winningName;
    }
    
    public long getLastRecordedMove() {
    	return lastRecordedMove;
    }
    
    public void setLastRecordedMove(long moveTime) {
    	lastRecordedMove = moveTime;
    }
    
    /**
     * 
     * @return a String representation of the board
     */
    @Override
    public String toString() {
  	
        StringBuilder currentBoard = new StringBuilder();

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if (board[i][j] == 0) {
                    currentBoard.append("[ ]");
                } else {
                    currentBoard.append("[" + board[i][j] + "]");
                }
            }
            currentBoard.append("\n");
        }
        return currentBoard.toString();
    }

    /**
     * clears the board.
     * currently only used for testing purposes
     */
    public void clearBoard() {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
            	board[i][j] = 0;
            	boardColIndex[j] = 0;
            }
    	}
    	
    }
    
    /**
     * 
     * @param x
     * @param y
     * @return boolean outlining if we have a winner
     */
    public boolean checkForWinner(int x, int y) {
    	
    	if (gameOver) return true;
    	
        int numConsecutive = 0;

        // check verticals
        // only need to check the column that changed
        numConsecutive = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            if (board[i][y] == board[x][y]) {
                 numConsecutive++;
            } else {
                numConsecutive = 0;
            }
            
            if (numConsecutive == WINNING_SCORE) {
            	gameOver = true;
            	return true;
            }
        }

        // check horizontals
        // only need to check the row that changed
        numConsecutive = 0;

        for (int i = 0; i < NUM_COLS; i++) {
        	if (board[x][i] == board[x][y]) {
                 numConsecutive++;
            } else {
                 numConsecutive = 0;
            }
        	
            if (numConsecutive == WINNING_SCORE) {
            	gameOver = true;
                return true;
            }
        }

        // check diagonals. For these focus on where the update has been
        // made and check relevant diagonals in this area

        numConsecutive = 1;

        // left to right diagonal. 
        // Check to the left first and then the right.
        // make sure it is a valid position to check too
        // left
        int i = x - 1;
        int j = y - 1;

        while (i >= 0 && j >= 0) {
        	if (board[i][j] != 0 &&
        			board[i][j] == board[x][y]) {
        		numConsecutive++;
        		if (numConsecutive == WINNING_SCORE) {
        			gameOver = true;
        			return true;
        		} 
        	} else {
        		break;
        	}
            i--;
            j--;
        }

        // right
        i = x + 1;
        j = y + 1;

        while (i < NUM_ROWS && j < NUM_COLS) {
        	if (board[i][j] != 0 &&
        			board[i][j] == board[x][y]) {
                numConsecutive++;
                if (numConsecutive == WINNING_SCORE) {
                	gameOver = true;
                    return true;
                }
            } else {
                break;
            }
            i++;
            j++;
        }

        numConsecutive = 1;

        // right to left diagonal.
        // Check to the right first and then the left.
        // make sure it is a valid position to check too.
        // right
        i = x - 1;
        j = y + 1;

        while (i >= 0 && j < NUM_COLS) {
        	if (board[i][j] != 0 &&
        			board[i][j] == board[x][y]) {
                numConsecutive++;
                if (numConsecutive == WINNING_SCORE) {
                	gameOver = true;
                    return true;
                }
            } else {
                break;
            }
            i--;
            j++;
        }

        // left
        i = x + 1;
        j = y - 1;

        while (i < NUM_ROWS && j >= 0) {
        	if (board[i][j] != 0 &&
        			board[i][j] == board[x][y]) {
                numConsecutive++;
                if (numConsecutive == WINNING_SCORE) {
                	gameOver = true;
                    return true;
                }
            } else {
                break;
            }
            i++;
            j--;
        }
        
        return false;
    }
    
    /**
     * 
     * @param colChoice
     * @return boolean indicating if slot is free
     */
    public boolean isValidChoice(int colChoice) {
    	if (colChoice >= 1 && colChoice <= NUM_COLS && boardColIndex[colChoice - 1] < NUM_ROWS) {
            return true;
        }
    	return false;
    }

    /**
     * record the player's choice of column
     * 
     * @param colChoice
     * @param marker
     * @return the slot that the piece was given
     */
    public Slot recordMove(int colChoice,int colour) {
    	
    	if (isValidChoice(colChoice)) {	
    		char symbol = numberMapping.get(colour);
    		int x = (NUM_ROWS - 1) - boardColIndex[colChoice - 1];
    		int y = colChoice - 1;
       
    		board[x][y] = symbol;
        	boardColIndex[y]++;
        
        	if (currentGo == 1) {
        		currentGo = 2;
        	} else {
        		currentGo = 1;
        	}
        	
        	lastRecordedMove = new Date().getTime();
        	return new Slot(x,y);
    	} else {
    		return null;
    	}
    }
}
