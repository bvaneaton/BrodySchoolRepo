package asmCodeGenerator;
import static asmCodeGenerator.ASMOpcode.*;
import static asmCodeGenerator.ASMCodeFragment.CodeType.*;

public class RunTime {
	public static final String EAT_LOCATION_ZERO = "$eat-location-zero";		// helps us distinguish null pointers from real ones.
	public static final String MAIN_PROGRAM_LABEL = "$main";

	private static final String SYSTEM_RETURN_ADDRESS = "-system-return-address";
	public  static final String RUNTIME_ERROR  =        "-system-error-runtime";
	private static final String RUNTIME_ERROR_MSG =     "$runtime-error-msg";
	public  static final String FRAME_POINTER         = "$system-frame-pointer";
	public  static final String THIS_OBJECT_POINTER         = "$system-object-pointer";
	
	public static final String BOOLEAN_PRINT_FORMAT  = "$print-format-boolean";
	public static final String INTEGER_PRINT_FORMAT  = "$print-format-integer";
	public static final String FLOATING_PRINT_FORMAT = "$print-format-floating";
	public static final String STRING_PRINT_FORMAT   = "$print-format-string";
	public static final String NEWLINE_PRINT_FORMAT  = "$print-format-newline";
	
	
	private ASMCodeFragment environment() {
		ASMCodeFragment result = new ASMCodeFragment(GENERATES_VOID);
		
		result.append(MemoryManager.codeForInitialization());
		result.append(systemVariables());
		result.append(jumpToMain());
		result.append(stringsForPrintf());
		result.append(runtimeErrorCode());
		
		result.add(Label, MAIN_PROGRAM_LABEL);
		
		return result;
	}
	
	
	
	private ASMCodeFragment systemVariables() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(DLabel, EAT_LOCATION_ZERO);
		frag.add(DataZ, 8);
		frag.add(DLabel, SYSTEM_RETURN_ADDRESS);
		frag.add(DataZ, 4);
		frag.add(DLabel, FRAME_POINTER);
		frag.add(DataZ, 4);
		frag.add(DLabel, THIS_OBJECT_POINTER);
		frag.add(DataZ, 4);
		
		frag.add(Memtop);
		//frag.add(PushI, 1);
		//frag.add(Subtract);
		frag.add(PushD, FRAME_POINTER);
		frag.add(Exchange);
		frag.add(StoreI);
		
		return frag;
	}



	private ASMCodeFragment jumpToMain() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(Jump, MAIN_PROGRAM_LABEL);
		return frag;
	}

	private ASMCodeFragment stringsForPrintf() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);
		frag.add(DLabel, BOOLEAN_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, STRING_PRINT_FORMAT);
		frag.add(DataS, "%s");
		frag.add(DLabel, INTEGER_PRINT_FORMAT);
		frag.add(DataS, "%d");
		frag.add(DLabel, FLOATING_PRINT_FORMAT);
		frag.add(DataS, "%g");
		frag.add(DLabel, NEWLINE_PRINT_FORMAT);
		frag.add(DataS, "\n");
		
		return frag;
	}
	

	// invoke by putting printf arguments on the stack, and appending the
	// code returned by this method.  This code prepends a "runtime error"
	// message and appends a newline.
	private ASMCodeFragment runtimeErrorCode() {
		ASMCodeFragment frag = new ASMCodeFragment(GENERATES_VOID);

		frag.add(DLabel, RUNTIME_ERROR_MSG);
		frag.add(DataS, "Runtime error!  ");

		// runtime error - general
		frag.add(Label, RUNTIME_ERROR);
		frag.add(PushD, RUNTIME_ERROR_MSG);
		frag.add(PushD, STRING_PRINT_FORMAT);
		frag.add(Printf);
		frag.add(Printf);						// this was the printf set on the stack
		frag.add(PushD, NEWLINE_PRINT_FORMAT);
		frag.add(Printf);
		frag.add(Halt);
		return frag;
	}
	
	public static ASMCodeFragment getEnvironment() {
		RunTime rt = new RunTime();
		return rt.environment();
	}

}
