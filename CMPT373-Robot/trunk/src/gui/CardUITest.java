package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

//this class's only purpose is to test if the Card UI works properly
public class CardUITest {

	  public static void main(String[] args) {
		  
		  JFrame f = new JFrame("card");
		  JPanel contentFrame = new JPanel();
		  InstructionLabel instruction = new InstructionLabel();
		  contentFrame.add(instruction);
		  
		    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    f.setContentPane(contentFrame);
		    f.pack();
		    f.setVisible(true);
		    System.out.println(" ");
	  } 
}