package board;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Stack;


class Robot extends MovableObject {
	
	//-----------Attributes----------------------
	private ArrayList<Object> inventory;
	private Stack<Object> robotStack;
    private double weight;
    
    
	//-----------Constructor of Robot-----------------------
	public Robot(ArrayList<Object> inventory, Stack<Object> robotStack){
		this.inventory = inventory;
		this.robotStack = robotStack;
	}
    
  //--------------------------THIS PART DEALS WITH INVENTORY------------------------
    
	//---------insert the part into the Robot-----------------
	public void insertPart(Object robotPart){
		inventory.add(robotPart);
	}
	
	//---------remove the part from the Robot-----------------
	public void removePart(Object robotPart){
		inventory.remove(robotPart);
	}
	
	//checks if it has the robot parts
	//.contains() returns a boolean value
	public boolean has(Object robotPart){
		if(inventory.contains(robotPart) == true){
			return true;
		}else{
			return false;
		}
	}
	//------------------END OF THIS PART DEALS WITH INVENTORY------------------------
	
	//----THESE PARTS ARE FOR THE STACK!!!!---
	public Object stackPop(){
		return robotStack.pop();
	}
	public void stackPush(Object object){
		robotStack.push(object);
	}
	public Object peek(){
		return robotStack.peek();
	}
	//--------------------------------------
}
