package board;

/**
 * Board Control Class
 * <P> The only communication into, and out of the board. 
 * 
 */

public class BoardControl {
	public BoardControl(){
		gameBoard = new Board(NUMBER_OF_LAYERS); 
	}
	private Board gameBoard;
	
	private final int NUMBER_OF_LAYERS = 30; //see Layer.java for a discussion of the number of layers
	
	/**
	 * From Justin: I'm still not sure about how this will work, is the robot entity in the game
	 * going to be a full robot object? The board is going to hold the location
	 */
	public void moveRobot(){
		
		
	}
	//should just be looking for object, doesn't need to deal with layer.
	public Tile getTile(int layer, int x, int y){
		return gameBoard.getTile(layer, x, y);
	}
}
