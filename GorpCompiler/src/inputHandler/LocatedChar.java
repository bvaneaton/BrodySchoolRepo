package inputHandler;


public class LocatedChar {
	Character character;
	TextLocation location;
	
	public LocatedChar(Character character, TextLocation location) {
		super();
		this.character = character;
		this.location = location;
	}
	public LocatedChar(Character character, String filename, int lineNumber, int position) {
		super();
		this.character = character;
		this.location = new TextLocation(filename, lineNumber, position);
	}
	public Character getCharacter() {
		return character;
	}
	public TextLocation getLocation() {
		return location;
	}
	public String toString() {
		return "(" + charString() + ", " + location + ")";
	}
	public String charString() {
		if(Character.isWhitespace(character)) {
			int i = character;
			return String.format("'\\%d'", i);
		}
		else {
			return character.toString();
		}
	}
}
