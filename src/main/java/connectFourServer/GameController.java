package connectFourServer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This class represents the controller. It defines the REST API
 *
 * @author Richie Duggan
 */
@RestController 
public class GameController {	
	
	Board board = new Board();
	
    @Autowired
    private Environment env;

    Player[] players = new Player[2];
    
    @GetMapping("/isAcceptingClients")
    @ResponseBody
    public ServerAnswer isAcceptingClients() {
      return new ServerAnswer(board.isAcceptingPlayers());
    }
    
    @GetMapping("/addPlayer/{playerName}")
    @ResponseBody
    public Player addPlayer(@PathVariable("playerName") String playerName) {
    	if (board.isAcceptingPlayers()) {
    		if (board.getNumPlayers() == 0) {
    			players[0] = new Player(playerName);
    			board.increaseNumPlayers();
    			return players[0];
    		} else {
    			players[1] = new Player(playerName);
    			board.increaseNumPlayers();
    			
    			// if colour has already been chosen then give the player
    			// the other colour
    			if (board.isColourChosen()) {
    				if (board.getColourChosenInt() == Board.RED) {
    					players[1].setColour(Board.YELLOW);
    				} else {
    					players[1].setColour(Board.RED);
    				}
    			}
    			
    			return players[1];
    		}
    	} else {
    		// we have enough players already
    		// TODO : add error
    		return null;
    	}
    }
   
    @GetMapping("/colourChosen")
    @ResponseBody
    public ServerAnswer colourChosen() {
      return new ServerAnswer(board.isColourChosen());
    }
	
    @GetMapping("/chooseColour/{playerId}/{colour}")
    @ResponseBody
    public Player chooseColour(@PathVariable("playerId") int playerId, 
    		@PathVariable("colour") int colour) {
    	
    	int playerIndex;
		int otherPlayerIndex;
		
		if (players[0].getId() == playerId) {
			playerIndex = 0;
			otherPlayerIndex = 1;
		} else if (players[1].getId() == playerId) {
			playerIndex = 1;
			otherPlayerIndex = 0;
		} else {
			// we have have received an ID we know nothing about
			// TODO : return error
			return null;
		}
    	
    	if (!board.isColourChosen()) {

    		// TODO : Add validation of colour
    		
    		// TODO : add validation of player!!
    		
    		players[playerIndex].setColour(colour);
    		board.setColourChosen(true);
    		board.setColourChosenInt(colour);
    			
    		// if a second player has already been created then set his 
    		// colour to the other one available
    		if (board.getNumPlayers() == 2) {
    			if (colour == Board.RED) {
    				players[otherPlayerIndex].setColour(Board.YELLOW);	
    			} else {
    				players[otherPlayerIndex].setColour(Board.RED);
    			}
    		}
    			
    		return players[playerIndex];	   	
    	} else {  		
    		// assign the other colour to the user because a colour
    		// has already been chosen
    		int colourDefaut = -1;
    		if (board.getColourChosenInt() == Board.RED) {
    			colourDefaut = Board.YELLOW;
    		} else {
    			colourDefaut = Board.RED;
    		}
    		
    		
    		players[playerIndex].setColour(colourDefaut);
			return players[playerIndex];
    	}
    }
    
    @GetMapping("/gameState")
    @ResponseBody
    public BoardState gameState() {
      return new BoardState(
    		  board.getCurrentGo(),
    		  board.toString(),
    		  board.getNumPlayers(),
    		  board.getGameOver(),
    		  board.getWinningName(),
    		  board.getWinningColour());
    }
    
    @GetMapping("/takeTurn/{playerId}/{column}")
    @ResponseBody
    public BoardState takeTurn(@PathVariable("playerId") int playerId, 
    		@PathVariable("column") int column) {
    	
    	if (board.getGameOver() || (board.getNumPlayers() < 2)) {
    		return gameState();
    	}    	
    	
    	// check to see if too much time has passed since the last go
    	if (board.getLastRecordedMove() != 0) { 	
    		long currentTime = new Date().getTime();
		int allowed_go_mins = Integer.parseInt(env.getProperty("game.allowedGoMinutes"));

    		if ((currentTime - board.getLastRecordedMove())/1000 > (allowed_go_mins * 60)) {
    			board.setGameOver(true);
    			return gameState();
    		}
    	}
    	
    	int colour;
    	String name;
    	
    	if (players[0].getId() == playerId) {
			colour = players[0].getColour();
			name = players[0].getName();
		} else if (players[1].getId() == playerId) {
			colour = players[1].getColour();
			name = players[1].getName();
		} else {
			// we have have received an ID we know nothing about
			// TODO : consider returning an error - will require client changes
			return gameState();
		}
    	
    	Slot slot = board.recordMove(column, colour);
    	boolean gameOver = board.checkForWinner(slot.xPos, slot.yPos);
    	if (gameOver) {
    		board.setWinningName(name);
    		board.setWinningColour(colour);
    	}
    	return gameState();
    }
}
