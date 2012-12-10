package utility;

/*
 * assuming top down structure, x will be the horizontal, and y will be the vertical.
 */
public class Location{
	private int x;
	private int y;

	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}


	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public void setX(int val){
		this.x = val;
	}

	public void setY(int val){
		this.y = val;
	}
}

/*
 * Might be a good idea to have tests to make sure that we don't go to rediculous locations
 * 
 */
