package symbolTable;

public class MemoryLocation {
	
	private BaseAddress baseAddress;
	private int offset;
	
	public MemoryLocation(BaseAddress baseAddress, int offset) {
		super();
		this.offset = offset;
		this.baseAddress = baseAddress;
	}

	public int getOffset() {
		return offset;
	}

	public BaseAddress getBaseAddress() {
		return baseAddress;
	}
	public String toString() {
		return "L-" + baseAddress + "+" + offset + "  ";
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////
// Null MemoryLocation object
////////////////////////////////////////////////////////////////////////////////////
	
	public static MemoryLocation nullInstance() {
		return NullMemoryLocation.getInstance();
	}
	private static class NullMemoryLocation extends MemoryLocation {
		private static final int NULL_OFFSET = 0;
		private static NullMemoryLocation instance=null;
		
		private NullMemoryLocation() {
			super(BaseAddress.NULL_BASE_ADDRESS, NULL_OFFSET);
		}
		public static NullMemoryLocation getInstance() {
			if(instance==null)
				instance = new NullMemoryLocation();
			return instance;
		}
	}
}
