package board;

class Layer {
	//move to the board class.
	private final int GRID_X = 10;
	private final int GRID_Y = 10;
	private Tile grid[][] = new Tile[GRID_X][GRID_Y]; //holds the tile objects in a 2-d array
	
	
	/* 
	 * Suggested convention for position assignment:
	 * "main" layer (water, walls, empty tiles, etc.) = layer 10
	 * "objects" layer (flag, ball, etc) = layer 20
	 * "robot layer" (a separate layer with only the robot, to simplify collision detection) = layer 30
	 * I'm playing with the idea of making the flag its own layer too. I haven't decided whether the flag should be a tile or an object yet.
	 * If we add more layers before or after these three, let them be n +- 10
	 * If we add more layers between these, let them be floor((previous layer + next layer)/2)
	 * I don't see us running into many layer numbering conflicts this way.
	 */
	
	void setTile(int x, int y, Tile newTile){
		grid[x][y] = newTile;
	}
	
	Tile getTile(int x, int y){
		return grid[x][y];
	}
	
		
}
