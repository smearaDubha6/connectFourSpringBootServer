package connectFourServer;

/**
 * This class represents a slot on the game board, recorded as a set of 
 * x and y coordinates
 *
 * @author Richie Duggan
 */
public class Slot {
	public int xPos;
    public int yPos;
    
    public Slot(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }
}
