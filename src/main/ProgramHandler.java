package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class ProgramHandler {
    public static final int VARS_PER_PROGRAM = 4;

    private static ProgramHandler instance;

    private Hashtable<Integer, Program> idProgramLink;
    private Memory memory;
    private Scheduler scheduler;
    private PCB programPCB;
    private int programSize;
    private int memoryLowerBound;

    private ProgramHandler(){
        idProgramLink = new Hashtable<>();
        memory = Memory.getMemoryInstance();
        scheduler = Scheduler.getSchedulerInstance();
    }

    public static ProgramHandler getInstance(){
        if(instance == null) instance = new ProgramHandler();
        return instance;
    }

    public Program getProgramById(int programId){
        return idProgramLink.get(programId);
    }

    public void createNewProgram(String programName){
        ArrayList<String> programCode = readProgramCode(programName);
        programSize = programCode.size() + PCB.SIZE_IN_MEMORY + VARS_PER_PROGRAM;
        memory.ensureSpaceAvailable(programSize);
        this.memoryLowerBound = memory.findSpaceStartIdx(programSize);
        this.programPCB = new PCB();
        MemoryWord[] programMemoryChunk = setupMemoryChunk(programName, programCode);
        memory.put(memoryLowerBound, programMemoryChunk);
        Program programObject = new Program(programName, programPCB);
        scheduler.addProgram(programObject);
        idProgramLink.put(this.programPCB.getProgramId(), programObject);
    }

    private MemoryWord[] setupMemoryChunk(String programName, ArrayList<String> programCode){
        MemoryWord[] memoryChunk = new MemoryWord[this.programSize];

        setupPCBChunk(memoryChunk);
        setupVariablesChunk(memoryChunk);
        setupCodeChunk(memoryChunk, programCode);

        return memoryChunk;
    }

    private void setupPCBChunk(MemoryWord[] memoryChunk){
		String varPrefix = programPCB.getProgramId() + "_pcb_";

		int programId = programPCB.getProgramId();
		memoryChunk[0] = new MemoryWord(varPrefix + "programId", programId + "");

		State currentState = programPCB.getCurrentState();
		memoryChunk[1] =  new MemoryWord(varPrefix + "state", currentState.toString());

		int programCounter = programPCB.getProgramCounter();
		memoryChunk[2] = new MemoryWord(varPrefix + "pc", programCounter + "");

        programPCB.setMemoryLowerBound(memoryLowerBound);
		memoryChunk[3] = new MemoryWord(varPrefix + "memLowerBound", memoryLowerBound + "");

		int memoryUpperBound = memoryLowerBound + this.programSize;
        programPCB.setMemoryUpperBound(memoryUpperBound);
		memoryChunk[4] = new MemoryWord(varPrefix + "memUpperBound", memoryUpperBound + "");
    }

    private void setupVariablesChunk(MemoryWord[] memoryChunk){
        int variableChunkStartIdx = PCB.SIZE_IN_MEMORY;
        String varName = programPCB.getProgramId() + "_vars_.freeVariable";
        for(int var = 0; var < VARS_PER_PROGRAM; var++){
            memoryChunk[variableChunkStartIdx + var] = new MemoryWord(varName, "");
        }
    }

    private void setupCodeChunk(MemoryWord[] memoryChunk, ArrayList<String> code){
        String varPrefix = programPCB.getProgramId() + "_code_";
        for(int codeLineNumber = 0; codeLineNumber < code.size(); codeLineNumber++){
            int wordNumber = codeLineNumber + PCB.SIZE_IN_MEMORY + VARS_PER_PROGRAM;
            String wordName = varPrefix + codeLineNumber;
            MemoryWord word = new MemoryWord(wordName, code.get(codeLineNumber));
            memoryChunk[wordNumber] = word;
        }
    }

    private ArrayList<String> readProgramCode(String programName){
        ArrayList<String> code = new ArrayList<>();
        String programFilePath = "programs" + File.separator + programName;
        try(BufferedReader reader = new BufferedReader(new FileReader(programFilePath))){
            String line;
			while((line = reader.readLine()) != null) {
				parseAndAdd(code, line);
			}
        }catch(IOException e){
            e.printStackTrace();
        }
        return code;
    }

    private void parseAndAdd(ArrayList<String> code, String line) {
		String[] splitLine = line.split(" ");
		if(!splitLine[0].equals("assign")) {
			code.add(line); return;
		}
		
		String inputMethod = splitLine[2];
		String firstInstruction;

		if(inputMethod.equals("input")){
			firstInstruction = "assign temp " + splitLine[2];
		}else{
			firstInstruction = "assign temp readFile " + splitLine[3];
		}

		String secondInstruction = "assign " + splitLine[1] + " temp";
		code.add(firstInstruction);
		code.add(secondInstruction);
	}
}
