package connectFourServer;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// TODO : I would like to simplify this class by just having it call appropriate
// methods in a different class instead of having the business logic built into it.
// I would also like to add more stringent checking around what is accepted as input
// and explicitly define the type of data that is being sent back.

/**
 * This class represents the controller. It defines the REST API
 *
 * @author Richie Duggan
 */
@RestController 
public class GameController {	
	
	Board board = new Board();
	
	// TODO : Would it be better to store the players in a data structure, 
	// for example an array and can reference players via the array index,
	// it might simplify the code below where we have various checks on which
	// player we are dealing with
	Player player1;
	Player player2;
    
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
    			player1 = new Player(playerName);
    			board.increaseNumPlayers();
    			return player1;
    		} else {
    			player2 = new Player(playerName);
    			board.increaseNumPlayers();
    			
    			// if colour has already been chosen then give the player
    			// the other colour
    			if (board.isColourChosen()) {
    				if (board.getColourChosenInt() == Board.RED) {
    					player2.setColour(Board.YELLOW);
    				} else {
    					player2.setColour(Board.RED);
    				}
    			}
    			
    			return player2;
    		}
    	} else {
    		// we have enough players already
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
    	if (!board.isColourChosen()) {

    		// TODO : Add validation of colour
    		
    		// TODO : There appears to be a lot of code replication below.
    		// Have a look to see if this can be improved
    		
    		if (player1.getId() == playerId) {
    			player1.setColour(colour);
    			board.setColourChosen(true);
    			board.setColourChosenInt(colour);
    			
    			// if a second player has already been created then set his 
    			// colour to the other one available
    			if (board.getNumPlayers() == 2) {
    				if (colour == Board.RED) {
    					player2.setColour(Board.YELLOW);	
    				} else {
    					player2.setColour(Board.RED);
    				}
    			}
    			
    			return player1;
    		} else if (player2.getId() == playerId){
    			player2.setColour(colour);
    			board.setColourChosen(true);
    			board.setColourChosenInt(colour);
    			
    			// if a second player has already been created then set his 
    			// colour to the other one available
    			if (board.getNumPlayers() == 2) {
    				if (colour == Board.RED) {
    					player1.setColour(Board.YELLOW);	
    				} else {
    					player1.setColour(Board.RED);
    				}
    			}
    			
    			return player2;
    		} else {
    			// we have have received an ID we know nothing about
    			return null;
    		}
    	} else {
    		
    		int colourDefaut = -1;
    		if (board.getColourChosenInt() == Board.RED) {
    			colourDefaut = Board.YELLOW;
    		} else {
    			colourDefaut = Board.RED;
    		}
    		
    		// assign the other colour
    		if (player1.getId() == playerId) {
    			player1.setColour(colourDefaut);
    			return player1;
    		} else if (player2.getId() == playerId){
    			player2.setColour(colourDefaut);	
    			return player2;
    		} else {
    			return null;
    		}
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
    	
    	if (board.getGameOver()) {
    		return gameState();
    	}
    	
    	// check to see if too much time has passed since the last go
    	if (board.getLastRecordedMove() != 0) { 	
    		long currentTime = new Date().getTime();
    		if ((currentTime - board.getLastRecordedMove())/1000 > (Board.ALLOWED_GO_MINUTES * 60)) {
    			board.setGameOver(true);
    			return gameState();
    		}
    	}
    	
    	int colour;
    	String name;
    	
    	if (player1.getId() == playerId) {
			colour = player1.getColour();
			name = player1.getName();
		} else if (player2.getId() == playerId) {
			colour = player2.getColour();
			name = player2.getName();
		} else {
			// we have have received an ID we know nothing about
			return null;
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
