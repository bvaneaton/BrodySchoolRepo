package utility; 

public class Direction{
	public enum compassPoint {NORTH, SOUTH, EAST, WEST};
	
	private compassPoint dir;
	
	public compassPoint getDir(){
		return dir;
	}
	
	public void setDir(compassPoint newDir){
		dir = newDir;
	}
	
	public void counterclockwise(){
		switch (dir) {
		case NORTH:
			dir = compassPoint.WEST;
			break;
		case WEST:
			dir = compassPoint.SOUTH;
			break;
		case SOUTH:
			dir = compassPoint.EAST;
			break;
		case EAST:
			dir = compassPoint.NORTH;
			break;		
		}
	}
	
	public void clockwise(){
		switch (dir) {
		case NORTH:
			dir = compassPoint.EAST;
			break;
		case WEST:
			dir = compassPoint.NORTH;
			break;
		case SOUTH:
			dir = compassPoint.WEST;
			break;
		case EAST:
			dir = compassPoint.SOUTH;
			break;		
		}
	}
	
	public void aboutFace(){
		switch (dir) {
		case NORTH:
			dir = compassPoint.SOUTH;
			break;
		case WEST:
			dir = compassPoint.EAST;
			break;
		case SOUTH:
			dir = compassPoint.NORTH;
			break;
		case EAST:
			dir = compassPoint.WEST;
			break;		
		}
	}
	
}