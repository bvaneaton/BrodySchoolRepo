/**
 * 
 */
package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class LegalInstructions {
	private EnumMap<Verb, VerbData> Instructions;
	
	//Constructor that creates the collection of legal Verbs/Nouns
	public LegalInstructions(){
		Instructions = new EnumMap<Verb, VerbData>(Verb.class);
		constructEnumMap();
	}
	
	//returns a List<Verb> of ALL Verbs declared in Verb.java
	public List<Verb> getVerbs(){	
		List<Verb> result = Arrays.asList(Verb.values());
		return result;
	}
	
	//returns List<Noun> of all Nouns matching the given Verb
	public List<Noun> getNouns(Verb v){
		VerbData verbVal = Instructions.get(v);
		return verbVal.getNouns();
	}
	
	public int getNumConditionals(Verb v){
		VerbData verbVal = Instructions.get(v);
		return verbVal.getNumConditionals();
	}
	
	//this is where we put together the Verbs with their Nouns and their # of conditionals
	private void constructEnumMap(){
		//first, make the list of nouns
		List<Noun> nouns = Arrays.asList(Noun.FORWARD, Noun.CLOCKWISE, Noun.COUNTERCLOCKWISE);
		//then turn it into VerbData with the # of Conditionals
		VerbData data = new VerbData(nouns, 2);
		//then put it in the EnumMap with its Verb 
		Instructions.put(Verb.MOVE, data);
	}
	
	
		//private class containing a list of nouns and an int for the # of conditional statements
		private class VerbData{
			private final List<Noun> nouns;
			private final int numConditionals;
			
			public VerbData(List<Noun> n, int c){
				nouns = n;
				numConditionals = c;
			}
	
			//return a COPY of the list of nouns
			public List<Noun> getNouns() {
				return (List<Noun>) new ArrayList(nouns);
			}
	
			public int getNumConditionals() {
				return numConditionals;
			}		
		}
	
}
