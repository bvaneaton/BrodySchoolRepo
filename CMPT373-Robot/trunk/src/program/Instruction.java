
package program;

import utility.*;

//immutable class
public class Instruction {
	private Verb verb;
	private Noun noun;
	private Conditional nextInstruction_0;
	private Conditional nextInstruction_1;
	private Conditional nextInstruction_2;
	
	
	//Constructor
	public Instruction(){
		verb = null;
		noun = null;
		nextInstruction_0 = null;
		nextInstruction_1 = null;
		nextInstruction_2 = null;
	}
	
	//Constructor
	public Instruction(Verb init_verb, Noun init_noun, Conditional condition1, Conditional condition2, Conditional condition3){
		verb = init_verb;
		noun = init_noun;
		nextInstruction_0 = condition1;
		nextInstruction_1 = condition2;
		nextInstruction_2 = condition3;
	}
	
	//Getters
	public Verb getVerb(){
		return verb;
	}
	public Noun getNoun(){
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
