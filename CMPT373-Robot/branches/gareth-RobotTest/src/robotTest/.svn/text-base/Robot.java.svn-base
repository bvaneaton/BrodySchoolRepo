package robotTest;
import java.util.*;

public class Robot extends MovableObject {
	private ArrayList <RobotPart> inventory = new ArrayList <RobotPart>();	//inventory, where robot parts are held
	
	//constructor: set the position and facing direction of the robot. later we will have more parameters.
	public Robot(int x, int y, int d){
		super(x,y,d);
	}
	
	//add a part to the inventory and reinitialize the robot.
	public boolean addPart(RobotPart part){
		return inventory.add(part);
	}
	
	public int executeInstruction(String verb){
		//this is a really poor way of doing this, have to revisit later
		
		if (verb.equalsIgnoreCase("move")){
			for (int i=0; i<inventory.size(); i++){
				if (inventory.get(i) instanceof RobotMotor){
					((RobotMotor) inventory.get(i)).move(1);
					return 1;
				}
			}
		}
		
		if (verb.equalsIgnoreCase("look")){
			for (int i=0; i<inventory.size(); i++){
				if (inventory.get(i) instanceof RobotCamera){
					((RobotCamera) inventory.get(i)).look();
					return 1;
				}
			}
		}
		
		if (verb.equalsIgnoreCase("compass")){
			for (int i=0; i<inventory.size(); i++){
				if (inventory.get(i) instanceof RobotCompass){
					((RobotCompass) inventory.get(i)).getCompassDirection();
					return 1;
				}
			}
		}
		
		if (verb.equalsIgnoreCase("stack")){
			for(int i =0; i< inventory.size(); i++){
				if (inventory.get(i) instanceof RobotStack){
					((RobotStack) inventory.get(i)).stackPush(new Integer(1));
					
					//---Testing Restriction------------------------------------
					//((RobotStack) inventory.get(i)).stackPush(new Integer(2));
					//((RobotStack) inventory.get(i)).stackPush(new Integer(3));
					//((RobotStack) inventory.get(i)).stackPush(new Integer(4));
					return 1;
				}
			}
		}
		
		return 0;
	}
}
