package board;
import robot.*;

@SuppressWarnings("unused")
public class BoardLevelGenerator {
	
	private Board board;
	private Robot robot;
	private StationaryTile water;
	private MovableObject ball;
	private MovableObject flag;
	
	public BoardLevelGenerator()
	{
		board = new Board(30);
		//robot = new Robot();
		water = new StationaryTile();
		ball = new MovableObject();
		flag = new MovableObject();
		
		setWalls();
		setWater();
		setEmptyTiles();
		setBall();
		setFlag();
		//setRobot();
	}
	
	// Set walls around the outer dimensions of the board (2 tile rows/columns)
	private void setWalls()
	{
		// "i" represents row and "j" column
		
		for (int i=0; i <= 9; i++) {
			for (int j=0; j <= 1; j++){ 
				board.setTile(10, i, j, new Wall());
			}
			
		}
		
		for (int i=0; i <= 1; i++) {
			for (int j=2; j<=7; j++) {
				board.setTile(10, i, j, new Wall());
			}
		}
		
		for (int i=0; i <= 9; i++) {
			for (int j=8; j <= 9; j++){ 
				board.setTile(10, i, j, new Wall());
			}
		}
		
		for (int i=8; i <= 9; i++) {
			for (int j=2; j<=7; j++) {
				board.setTile(10, i, j, new Wall());
			}
		}
	}
	
	// set water at an arbitrary location on the board
	private void setWater()
	{
		for (int i=4; i <= 5; i++) {
			for (int j=5; j <= 6; j++) {
				board.setTile(10, i, j, water);
			}
		}
	}
	
	// set empty tiles where there is no water
	private void setEmptyTiles() 
	{
		for (int i=2; i <= 7; i++) {
			for (int j=2; j <= 7; j++) {
				if (board.getTile(10, i, j) != water) {
					board.setTile(10, i, j, new EmptyTile());
				}
			}
		}
	}

	// set ball at an arbitrary location on the board
	private void setBall()
	{
		board.setTile(20, 5, 6, ball);
	}
	
	private void setFlag()
	{
		board.setTile(20, 2, 2, flag);
	}
	
	// this will depend on the implementation of the BoardControl class
	private void setRobot()
	{
		
	}
}
