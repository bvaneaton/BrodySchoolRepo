package tokens;

import inputHandler.TextLocation;

public class FloatingConstantToken extends TokenImp {
	protected float value;
	
	protected FloatingConstantToken(TextLocation location, String lexeme) {
		super(location, lexeme);
	}
	protected void setValue(float value) {
		this.value = value;
	}
	public float getValue() {
		return value;
	}
	
	public static FloatingConstantToken make(TextLocation location, String lexeme) {
		FloatingConstantToken result = new FloatingConstantToken(location, lexeme);
		result.setValue(Float.parseFloat(lexeme));
		return result;
	}
	
	@Override
	protected String rawString() {
		return "floatConst, " + value;
	}
}
