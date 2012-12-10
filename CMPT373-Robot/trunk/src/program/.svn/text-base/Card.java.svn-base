package program;
import utility.*;
import java.util.List;

public class Card {
	Instruction[][] instructionList;
	Location currentLocation; //Location object for current instruction
	int width, height; //number of instructions horizontally and vertically
	
	//TO DO: implement no-ops and fill card with no-ops initially(?)
	
		public Card(int init_width, int init_height, Location init_currentLocation){
			int X = init_currentLocation.getX();
			int Y = init_currentLocation.getY();
			
			instructionList = new Instruction[X][Y];
			currentLocation.setX(X);
			currentLocation.setY(Y);
		}
		
		//TO DO: throw errors for illegal instructions
		//add given instruction to given position
		public void putInstruction(Instruction inst, Location newLocation){
			int X = newLocation.getX();
			int Y = newLocation.getY();
			
			if(checkInstructionLegality(inst, newLocation))
				instructionList[X][Y] = inst;
		}		
		
		//create new Instruction at given position
		public void putInstruction(Verb v, Noun n, Conditional c0, Conditional c1, Conditional c2, Location newLocation){
			int X = newLocation.getX();
			int Y = newLocation.getY();
			
			Instruction inst = new Instruction(v, n, c0, c1, c2);
			if(checkInstructionLegality(inst, newLocation))
				instructionList[X][Y] = inst;
		}
		
		//create new Instruction with only 2 conditionals specified
		public void putInstruction(Verb v, Noun n, Conditional c0, Conditional c1, Location newLocation){
			Conditional c2 = null;
			int X = newLocation.getX();
			int Y = newLocation.getY();
			
			
			Instruction inst = new Instruction(v, n, c0, c1, c2);
			if(checkInstructionLegality(inst, newLocation))
				instructionList[X][Y] = inst;
		}
		
		//create new Instruction with only 1 conditionals specified
		public void putInstruction(Verb v, Noun n, Conditional c0, Location newLocation){
			Conditional c2 = null;
			Conditional c1 = null;
			int X = newLocation.getX();
			int Y = newLocation.getY();
			
			
			Instruction inst = new Instruction(v, n, c0, c1, c2);
			if(checkInstructionLegality(inst, newLocation))
				instructionList[X][Y] = inst;
		}
		
		//TO DO: error handling if nothing's there
		public Instruction getInstruction(Location newLocation){
			return instructionList[newLocation.getX()][newLocation.getY()];
		}
		
		public Instruction getCurrentInstruction(){
			return instructionList[currentLocation.getX()][currentLocation.getY()];
		}
		
		//sets the next Instruction when given the arrow coming from the current instruction
		//Pre-condition: must not be an illegal arrow (ie going off the board)
		public void setNextInstruction(Conditional cond){
			int X = currentLocation.getX();
			int Y = currentLocation.getY();
			
			switch(cond){
			case UP:
				Y--;
				currentLocation.setY(Y);
			case DOWN:
				Y++;
				currentLocation.setY(Y);
			case RIGHT:
				X++;
				currentLocation.setX(X);
			case LEFT:
				X--;
				currentLocation.setX(X);
			}
		}
		
		//TO DO: implement exceptions
		//check Instruction at X, Y, to see if it has a legal verb, noun, and conditionals
		private boolean checkInstructionLegality(Instruction inst, Location checkLocation){
			LegalInstructions legalInst = new LegalInstructions();
			//check that verb exists
			if (inst.getVerb() == null)
				return false;
			
			//check that noun exists
			if (inst.getNoun() == null)
				return false;
			
			//check to see if the noun matches the verb
			List<Noun> nounList = legalInst.getNouns(inst.getVerb());
			if (!(nounList.contains(inst.getNoun())))
				return false;
			
			//check to see if the verb has the correct number of conditionals
			if (!(legalInst.getNumConditionals(inst.getVerb()) == checkNumberCondition(inst)))
				return false;
			
			//TO DO: also check that each conditional points to a valid instruction, unless no-ops
			//check that the first conditional exists
			if (inst.getNext0() == null){
				return false;
			} else 
				//check that Conditional arrows aren't pointing off the card
				if(!(checkConditionalLegality(checkLocation,inst.getNext0())
					&& checkConditionalLegality(checkLocation,inst.getNext1())
					&& checkConditionalLegality(checkLocation,inst.getNext2())))
				return false; //if not every Conditional is legal, the instruction's illegal
							
			return true;
		}
		
		//count the number of legal conditionals that an instruction has
		private int checkNumberCondition(Instruction inst){
			int numberOfConditions = 0;
			
			if (!(inst.getNext0() == null))
				numberOfConditions++;
			
			if (!(inst.getNext1() == null))
				numberOfConditions++;
			
			if (!(inst.getNext2() == null))
				numberOfConditions++;
			
			return numberOfConditions;
		}
		//check that a Conditional arrow isn't pointing off the card
		//returns true for null Conditionals
		private boolean checkConditionalLegality(Location checkLocation, Conditional cond){
			int X = checkLocation.getX();
			int Y = checkLocation.getY();
			
			if (cond == null)
				return true; //a non-existent arrow can't be invalid!
			
			switch(cond){
			case UP:
				if(Y <= 0)
					return false;
			case DOWN:
				if(Y >= height-1)
					return false;
			case LEFT:
				if(X <= 0)
					return false;
			case RIGHT:
				if(X >= width-1)
					return false;
			}
			return true;
		}
}
