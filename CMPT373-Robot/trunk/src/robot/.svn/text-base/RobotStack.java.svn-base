package robot;

import java.util.Stack;


/**
 * RobotStack Class
 * <P> Holds a set of objects for comparisons and other operations.
 */
public class RobotStack extends RobotPart {
	
	//---ATTRIBUTES----------------
	Stack<Object> robotStack;
	private int maxElement;
	//-----------------------------
	
	//-----------------------------------
	//DEFAULT CONSTRUCTOR
	//-----------------------------------
	public RobotStack(){
		super(1,50);
		this.robotStack = new Stack<Object>();
		this.maxElement = 3;
	}
	
	//---------------------------------
	//Set MaxElements Constructor
	//---------------------------------
	public RobotStack(int maxElement){
		super(1,50);
		this.robotStack = new Stack<Object>();
		this.maxElement = maxElement;
	}
	
	
	//---------------------------------
	//Push Elements
	//---------------------------------
	public void stackPush(Object object){
		if(robotStack.size() < maxElement){
			robotStack.push(object);
			System.out.println("Element has been pushed");
		}else{
			System.out.println("Cannot push anymore");
		}
		
	}
	
	//---------------------------------
	//Pop Elements
	//---------------------------------
	public Object stackPop(){
		if(robotStack.size() == 0){
			System.out.println("Stack is already Empty!");
			return 0;
		}else{
			return robotStack.pop();
		}
	}
	
	//---------------------------------
	//Peek Elements
	//---------------------------------
	public Object stackPeek(){
		return robotStack.peek();
	}
	
	//---------------------------------
	//Get the maximum number of Elements available
	//---------------------------------
	public int getMaxElement(){
		return maxElement;
	}
}//End of RobotStack