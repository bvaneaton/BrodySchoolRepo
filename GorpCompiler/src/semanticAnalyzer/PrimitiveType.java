package semanticAnalyzer;

public enum PrimitiveType implements Type {
	UNDEFINED(0),
	ERROR(0),
	INTEGER(4),
	FLOAT(8),
	BOOLEAN(8),
	NULL(0),
	STRING(0),
	AUTOTYPE(0),
	FUNCTION(0),
	VOID(0);
	
	private int sizeInBytes;
	
	private PrimitiveType(int size) {
		this.sizeInBytes = size;
	}
	public int getSize() {
		return sizeInBytes;
	}
}
