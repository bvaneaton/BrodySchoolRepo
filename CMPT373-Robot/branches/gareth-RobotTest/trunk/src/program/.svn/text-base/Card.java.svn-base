import java.util.ArrayList;

public class Card {
	Instruction[][] instructionList;
	int current; //index of current instruction
	int width, height; //number of instructions horizontally and vertically
	
		public Card(int init_width, int init_height, int init_currentX, int init_currentY){
			instructionList = new Instruction[init_width][init_height];
			currentX = init_currentX;
			currentY = init_currentY;
		}
		
		public void putInstruction(Instruction inst, int X, int Y){
			instructionList[X][Y] = inst;
			//If it were inserted, everything after would be shoved down and all their conditionals would change
		}
		
		public Instruction getInstruction(int X, int Y){
			return instructionList.get(index);
		}
		
		public Instruction getCurrentInstruction(){
			return instructionList.get(current);
		}
		
		public void setNextInstruction(int next){
			current = next;
		}
}
