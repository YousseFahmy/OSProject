import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

import exceptions.ProgramBlockedException;

public class Processor {

	private Hashtable<String, Mutex> mutexes;
	private Program runningProgram;
	private Scanner scanner;
	
	public Processor() {
		this.scanner = new Scanner(System.in);
		initialiseSystemMutexes();
	}

	private void initialiseSystemMutexes() {
		mutexes = new Hashtable<>();
		mutexes.put("userInput", new Mutex("userInput"));
		mutexes.put("userOutput", new Mutex("userOutput"));
	}
	
	public void run(Program program) {
		this.runningProgram = program;
		String lineToRun = program.nextLine();
		execute(lineToRun);
		this.runningProgram = null;
	}
	
	public void execute(String lineToRun) {
		String[] commandLine = lineToRun.split(" ");
		switch(commandLine[0]) {
		case "print":
			String varToPrint = commandLine[1];
			print(varToPrint);
			break;
		case "assign":
			assignVariable(commandLine);			
			break;
		case "writeFile":
			writeFileCommand(commandLine);
			break;
		case "readFile":
			readFileCommand(commandLine);
			break;
		case "printFromTo": 
			printFromTo(commandLine);
			break;
		case "semWait":
			commandSemWait(commandLine);			
			break;
		case "semSignal": 
			commandSemSignal(commandLine);
			break;
		}
	}

	private void print(String varToPrint) {
		String varContent = runningProgram.getVariable(varToPrint);
		System.out.println(varContent);
	}
	
	private void assignVariable(String[] commandLine) {
		String varIdentifier = commandLine[1];
		String inputMethod = commandLine[2];
		String varValue;
		
		if(inputMethod.equals("input")) {
			varValue = inputFromUser();
		}else if(inputMethod.equals("readFile")) {
			String newCommand = "readFile " + commandLine[3];
			varValue = readFileCommand(newCommand.split(" "));
		}else {
			varValue = inputMethod;
		}
		
		runningProgram.addVariable(varIdentifier, varValue);
		
	}

	private String inputFromUser() {
		System.out.println(runningProgram.getID());
		System.out.print("Please enter a value > ");
		return scanner.nextLine();
	}

	private void writeFileCommand(String[] commandLine) {
		String fileNameIdentifier = commandLine[1];
		String fileName = runningProgram.getVariable(fileNameIdentifier);
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			String contentVarIdentifier = commandLine[2];
			String content = runningProgram.getVariable(contentVarIdentifier);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readFileCommand(String[] commandLine) {
		String fileNameIdentifier = commandLine[1];
		String fileName = runningProgram.getVariable(fileNameIdentifier);
		try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
			StringBuilder fileContent = new StringBuilder();
			String line;
			
			while((line = reader.readLine()) != null) {
				fileContent.append(line);
			}
			
			return fileContent.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private void printFromTo(String[] commandLine) {
		String firstNumberIdentifier = commandLine[1];
		int firstNumber = Integer.parseInt(runningProgram.getVariable(firstNumberIdentifier));
		
		String secondNumberIdentifier = commandLine[2];
		int secondNumber = Integer.parseInt(runningProgram.getVariable(secondNumberIdentifier));
		
		for(int i = firstNumber; i <= secondNumber; i++) {
			System.out.println(i);
		}
	}
	
	private void commandSemWait(String[] commandLine) {
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
	
	private void commandSemSignal(String[] commandLine) {
		String mutexName = commandLine[1];
		Mutex mutex = mutexes.get(mutexName);
		mutex.semSignal(runningProgram);
	}
	
}
