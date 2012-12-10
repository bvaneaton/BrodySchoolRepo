package board;

public class BoardControl {
	public BoardControl(){
		gameBoard = new Board(NUMBER_OF_LAYERS); 
	}
	private Board gameBoard;
	
	private final int NUMBER_OF_LAYERS = 30; //see Layer.java for a discussion of the number of layers
	
	public void moveRobot(){}
	
	public Tile getTile(int layer, int x, int y){
		return gameBoard.getTile(layer, x, y);
	}
}
