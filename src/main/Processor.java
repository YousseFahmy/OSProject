package main;
import java.util.Hashtable;
import java.util.Set;

import exceptions.InvalidArgumentException;
import exceptions.ProgramBlockedException;

public class Processor {

	private Hashtable<String, Mutex> mutexes;
	private Program runningProgram;
	private int runningProgramId;
	
	public Processor() {
		initialiseSystemMutexes();
	}

	private void initialiseSystemMutexes() {
		mutexes = new Hashtable<>();
		mutexes.put("userInput", new Mutex("userInput"));
		mutexes.put("userOutput", new Mutex("userOutput"));
	}
	
	public void releaseHeldMutexes(Program programToReleaseMutexes) {
		Set<String> mutexNameSet = mutexes.keySet();
		for(String mutexName : mutexNameSet) {
			Mutex mutex = mutexes.get(mutexName);
			if(mutex.isHolder(programToReleaseMutexes)) mutex.semSignal(programToReleaseMutexes);
		}
		
	}

	public void run(Program program) {
		if(program == null) return;
		this.runningProgram = program;
		this.runningProgramId = program.getID();
		String lineToRun = program.getNextInstructionAndIncrement();
		execute(lineToRun);
		this.runningProgram = null;
	}
	
	public void execute(String lineToRun) {
		String[] commandLine = lineToRun.split(" ");
		switch(commandLine[0]) {
		case "print":
			printToScreenCommand(commandLine);
			break;
		case "assign":
			assignVariableCommand(commandLine);			
			break;
		case "writeFile":
			writeFileCommand(commandLine);
			break;
		case "readFile":
			readFileCommand(commandLine);
			break;
		case "printFromTo":
			printFromToCommand(commandLine);
			break;
		case "semWait":
			semWaitCommand(commandLine);			
			break;
		case "semSignal":
			semSignalCommand(commandLine);
			break;
		}
	}

	private void printToScreenCommand(String[] commandLine) {
		String varToPrint = commandLine[1];
		String varContent = SystemCalls.getProcessVariable(runningProgramId, varToPrint);
		SystemCalls.outputToScreen(varContent);
	}
	
	private void assignVariableCommand(String[] commandLine) {
		String varIdentifier = commandLine[1];
		String inputMethod = commandLine[2];
		String varValue;
		
		if(inputMethod.equals("input")) {
			varValue = inputFromUser();
		}else if(inputMethod.equals("readFile")) {
			String newCommand = "readFile " + commandLine[3];
			varValue = readFileCommand(newCommand.split(" "));
		}else {
			varValue = SystemCalls.getProcessVariable(runningProgramId, "temp");
		}

		SystemCalls.setProcessVariable(runningProgramId, varIdentifier, varValue);
		
	}

	private String inputFromUser() {
		SystemCalls.outputToScreen("Please enter a value > ");
		return SystemCalls.getInputFromUser();
	}

	private void writeFileCommand(String[] commandLine) {
		String fileNameIdentifier = commandLine[1];
		String fileName = SystemCalls.getProcessVariable(runningProgramId, fileNameIdentifier);
		String contentVarIdentifier = commandLine[2];
		String content = SystemCalls.getProcessVariable(runningProgramId, contentVarIdentifier);
		SystemCalls.writeFile(fileName, content);
	}
	
	private String readFileCommand(String[] commandLine) {
		String fileNameIdentifier = commandLine[1];
		String fileName = SystemCalls.getProcessVariable(runningProgramId, fileNameIdentifier);
		return SystemCalls.readFile(fileName);
	}
	
	private void printFromToCommand(String[] commandLine) {
		String firstNumberIdentifier = commandLine[1];		
		String secondNumberIdentifier = commandLine[2];
		
		int firstNumber, secondNumber;
		
		try {
			firstNumber = Integer.parseInt(SystemCalls.getProcessVariable(runningProgramId, firstNumberIdentifier));
			secondNumber = Integer.parseInt(SystemCalls.getProcessVariable(runningProgramId, secondNumberIdentifier));
		} catch (NumberFormatException e){
			throw new InvalidArgumentException();
		}
		
		for(int i = firstNumber; i <= secondNumber; i++) {
			SystemCalls.outputToScreen(i);
		}
	}
	
	private void semWaitCommand(String[] commandLine) {
		String mutexName = commandLine[1];
		Mutex mutex = mutexes.get(mutexName);
		
		if(mutex == null) {
			mutex = createAndReturnNewMutex(mutexName);
		}
		
		if(mutex.isAvailable()) {
			mutex.semWait(runningProgram);
		}else {
			mutex.addToBlockedList(runningProgram);
			throw new ProgramBlockedException();
		}
	}

	private Mutex createAndReturnNewMutex(String mutexName) {
		Mutex mutex = new Mutex(mutexName);
		mutexes.put(mutexName, mutex);
		return mutex;
	}
	
	private void semSignalCommand(String[] commandLine) {
		String mutexName = commandLine[1];
		Mutex mutex = mutexes.get(mutexName);
		mutex.semSignal(runningProgram);
	}
	
}
