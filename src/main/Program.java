package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import exceptions.ProgramFinishedException;
import exceptions.VariableDoesNotExistException;

public class Program {
	private int id;
	private String name;
	private ArrayList<String> code;
	private State state;
	private int nextInstruction;
	private Hashtable<String, String> vars;
	
	private static int nextId = 1;
	
	public Program(String fileName) {
		this.id = nextId++;
		this.name = fileName;
		this.state = State.READY;
		this.nextInstruction = 0;
		this.vars = new Hashtable<>();
		this.code = new ArrayList<>();
		SystemCalls.reserveProgramMemory(this);
		parseProgramCode(fileName);
	}

	private void parseProgramCode(String fileName) {
		String filePath = "programs" + File.separator + fileName;
		try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
			String line;
			while((line = reader.readLine()) != null) {
				parseAndAdd(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseAndAdd(String line) {
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
	
	public String getNextInstruction() {
		if(nextInstruction == code.size()) {
			throw new ProgramFinishedException();
		}
		
		return code.get(nextInstruction);
	}
	
	public String getNextInstructionAndIncrement() {
		String instruction = this.getNextInstruction();
		nextInstruction++;
		return instruction;
	}
	
	public void block() {
		this.state = State.BLOCKED;
	}
	
	public void ready() {
		this.state = State.READY;
	}
	
	public void finish() {
		this.state = State.FINISHED;
	}
	
	public String getVariable(String requestedVarIdentifier){
		String varValue = vars.get(requestedVarIdentifier);
		if (varValue == null) throw new VariableDoesNotExistException();
		return varValue;
	}
	
	public void addVariable(String identifier, String value) {
		vars.put(identifier, value);
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setState(State newState) {
		this.state = newState;
	}
	
	public State getState() {
		return this.state;
	}
	
	@Override
	public String toString() {
		return "ID: " + this.id + " Program: " + this.name;
	}

	
}
