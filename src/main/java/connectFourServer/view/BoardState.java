package connectFourServer.view;

/**
 * This class represents the state of the board at a state in time. It is used
 * to send a response back to the client for most requests so the client has
 * all the information needed in order to display content how it so wishes
 *
 * @author Richie Duggan
 */
public class BoardState {

	private int playerGo;
	private String board;
	private int numPlayers;
	private boolean gameOver;
	private String winningName;
	private int winningColour;
	
	public BoardState(int playerGo, String board, int numPlayers,
			boolean gameOver,
			String winningName,
			int winningColour) {
		this.playerGo = playerGo;
		this.board = board;
		this.numPlayers = numPlayers;
		this.gameOver = gameOver;
		this.winningName = winningName;
		this.winningColour = winningColour;
	}
	
	public int getPlayerGo() {
		return playerGo;
	}
	
	public String getBoardState() {
		return board; 
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public boolean getGameOver() {
		return gameOver;
	}
	
	public int getWinningColour() {
		return winningColour;
	}
	
	public String getWinningName() {
		return winningName;
	}
 }
