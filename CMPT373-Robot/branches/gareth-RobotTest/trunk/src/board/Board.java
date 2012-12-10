package board;

/**
 * Board Class
 * <P> Provides a structure for holding layer objects, which in turn hold tiles.
 */
class Board {
	
	/**
	 * Constructor.
	 * @param numberOfLayers The number of layers the board should hold. 
	 */
	Board(int numberOfLayers){
		this.layerArray = new Layer[numberOfLayers]; //holds the layer objects in an array
	}
	
	Layer layerArray[];
	
	//enums for level parameters will go here
	
	/**
	 * Returns a tile being held in one of this board's layers.
	 * @param layer The layer to look in
	 * @param x x-coordinate
	 * @param y y-coordiante
	 */
	Tile getTile(int layer, int x, int y){
		return layerArray[layer].getTile(x, y);
	}
	
	/**
	 * Sets the value of a tile being held in one of this board's layers.
	 * @param layer The layer to work on
	 * @param x x-coordinate
	 * @param y y-coordiante
	 * @param newTile The tile object to set the specified tile location to
	 */
	void setTile(int layer, int x, int y, Tile newTile){
		layerArray[layer].setTile(x, y, newTile);
	}
}
