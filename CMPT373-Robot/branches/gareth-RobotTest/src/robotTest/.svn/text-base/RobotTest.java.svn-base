package robotTest;

public class RobotTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		System.out.println("Hello Eclipse!");
		
		/*
		MovableObject obj = new MovableObject(0,0);		
		System.out.println("New MovableObject 'obj' created at " + obj.getX() + "," + obj.getY());		
		obj.setPosition(1,0);		
		System.out.println("obj moved to " + obj.getX() + "," + obj.getY());		
		obj.incrementPosition(0, 12);		
		System.out.println("obj moved to " + obj.getX() + "," + obj.getY());
		*/
		
		Robot spot = new Robot(0,0,0);		
		System.out.println("New Robot 'spot' created at " + spot.getX() + "," + spot.getY());
		
		spot.addPart(new RobotMotor());
		System.out.println("Motor added to robot.");
		spot.executeInstruction("move");
		
		spot.addPart(new RobotCamera());
		System.out.println("Camera added to robot.");
		spot.executeInstruction("look");
		
		spot.addPart(new RobotCompass());
		System.out.println("Compass added to robot.");
		spot.executeInstruction("compass");
		
		spot.addPart(new RobotStack());
		System.out.println("Robotstack added to robot");
		spot.executeInstruction("stack");
	}

}
