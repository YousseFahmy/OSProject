import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import exceptions.ProgramFinishedException;

public class Program {
	private int id;
	private ArrayList<String> code;
	private State state;
	private int nextInstruction;
	private Hashtable<String, String> vars;
	
	private static int nextId = 0;
	
	public Program(String fileName) {
		this.id = nextId++;
		this.state = State.READY;
		this.nextInstruction = 0;
		this.vars = new Hashtable<>();
		this.code = new ArrayList<>();
		readProgramCode(fileName);
	}

	private void readProgramCode(String fileName) {
		String filePath = "programs" + File.separator + fileName;
		try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
			String line;
			while((line = reader.readLine()) != null) {
				code.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String nextLine() {
		if(nextInstruction == code.size()) {
			throw new ProgramFinishedException();
		}
		
		return code.get(nextInstruction++);
	}
	
	public void block() {
		this.state = State.BLOCKED;
		decrementNextInstructionCounter();
	}
	
	public void ready() {
		this.state = State.READY;
	}
	
	public void finish() {
		this.state = State.FINISHED;
	}
	
	public String getVariable(String requestedVarIdentifier){
		return vars.get(requestedVarIdentifier);
	}
	
	public void addVariable(String identifier, String value) {
		vars.put(identifier, value);
	}
	
	private void decrementNextInstructionCounter() {
		nextInstruction--;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setState(State newState) {
		this.state = newState;
	}
	
	public State getState() {
		return this.state;
	}

	
}