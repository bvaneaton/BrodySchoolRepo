package robot;
import utility.*;
import board.*;
import java.util.*;

/**
 * Robot Class
 * <P> Contains an inventory of RobotParts and provides an interface to utilize them.
 */
public class Robot extends MovableObject {
	private ArrayList <RobotPart> inventory;	
	private Direction robotDirection;
	
	/**
	 * Constructor.
	 */
	public Robot(){
		inventory = new ArrayList <RobotPart> ();
		robotDirection = new Direction();
		robotDirection.setDir(Direction.compassPoint.NORTH);
	}
	
	public Direction.compassPoint getRobotDirection(){
		return robotDirection.getDir();
	}
	
	public void setRobotDirection(utility.Direction.compassPoint newDir){
		robotDirection.setDir(newDir);
	}
	
	/**
	 * Adds a RobotPart to the robot's inventory.
	 * @param part The RobotPart to add to the inventory.
	 */
	public boolean addPart(RobotPart part){
		return inventory.add(part);
	}
	
	/**
	 * Takes an instruction, interprets and executes it.
	 * @param verb The instruction to execute.
	 */
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
