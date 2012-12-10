package symbolTable;

import asmCodeGenerator.RunTime;

public enum BaseAddress {
	GLOBAL_POINTER,
	FRAME_POINTER,
	CURRENT_OFFSET,
	NULL_BASE_ADDRESS;
	
	public String getASMVariableBlock() {
		return RunTime.FRAME_POINTER;
	}
}
