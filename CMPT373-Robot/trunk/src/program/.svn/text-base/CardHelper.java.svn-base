package program;
import utility.*;

public class CardHelper {
	private Card mainCard;
	
		//constructor
		public CardHelper(int init_width, int init_height, Location init_currentLocation){
			mainCard = new Card(init_width, init_width, init_currentLocation);
		}
		
		//add given instruction to given position
		public void putInstruction(Instruction inst, Location newLocation){
			mainCard.putInstruction(inst, newLocation);
		}
		
		//add given instruction to given position
		public void putInstruction(Verb v, Noun n, Conditional c0, Conditional c1, Conditional c2, Location newLocation){
			mainCard.putInstruction(v, n, c0, c1, c2, newLocation);
		}
		
		//create new Instruction with only 2 conditionals specified
		public void putInstruction(Verb v, Noun n, Conditional c0, Conditional c1, Location newLocation){
			mainCard.putInstruction(v, n, c0, c1, newLocation);
		}
		
		//create new Instruction with only 1 conditionals specified
		public void putInstruction(Verb v, Noun n, Conditional c0, Location newLocation){
			mainCard.putInstruction(v, n, c0, newLocation);
		}
		
		//gets the instruction at location
		public Instruction getInstruction(Location newLocation){
			return mainCard.getInstruction(newLocation);
		}
		
		//gets the current instruction
		public Instruction getCurrentInstruction(){
			return mainCard.getCurrentInstruction();
		}
		
		//sets the next Instruction when given the arrow coming from the current instruction
		//Pre-condition: must not be an illegal arrow (ie going off the board)
		public void setNextInstruction(Conditional cond){
			mainCard.setNextInstruction(cond);
		}
}
