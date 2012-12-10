
public class Instruction {
	private String noun;
	private String verb;
	private Conditional nextInstruction_0;
	private Conditional nextInstruction_1;
	private Conditional nextInstruction_2;
	
	public enum Conditional {UP, DOWN, LEFT, RIGHT} //possible future addition: loop back to self?
	
	//Constructor
	public Instruction(){
		noun = "";
		verb = "";
		nextInstruction_0 = null;
		nextInstruction_1 = null;
		nextInstruction_2 = null;
	}
	
	//Constructor
	public Instruction(String init_noun, String init_verb, Conditional condition1, Conditional condition2, Conditional condition3){
		noun = init_noun;
		verb = init_verb;
		nextInstruction_0 = condition1;
		nextInstruction_1 = condition2;
		nextInstruction_2 = condition3;
	}
	
	//Setters	
	public void setNoun(String newNoun){
		noun = newNoun;
	}
	public void setVerb(String newVerb){
		verb = newVerb;
	}
	public void setNext0(Conditional condition1){
		nextInstruction_0 = condition1;
	}
	public void setNext1(Conditional condition2){
		nextInstruction_1 = condition2;
	}
	public void setNext2(Conditional condition3){
		nextInstruction_2 = condition3;
	}
	
	//Getters
	public String getNoun(){
		return noun;
	}
	public String getVerb(){
		return noun;
	}
	public Conditional getNext0(){
		return nextInstruction_0;
	}
	public Conditional getNext1(){
		return nextInstruction_1;
	}
	public Conditional getNext2(){
		return nextInstruction_2;
	}
}
