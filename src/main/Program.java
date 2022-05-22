package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.ProgramFinishedException;

public class Program {
	private String name;
	private ArrayList<String> code;
	private PCB pcb;

	public Program(String fileName) {
		this.name = fileName;
		this.code = new ArrayList<>();
		this.pcb = new PCB();
		SystemCalls.loadToMemory(this);
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
		int nextInstructionCounter = pcb.getProgramCounter();
		if(nextInstructionCounter == code.size()) {
			throw new ProgramFinishedException();
		}
		
		return code.get(nextInstructionCounter);
	}
	
	public String getNextInstructionAndIncrement() {
		String instruction = this.getNextInstruction();
		pcb.incrementProgramCounter();
		return instruction;
	}
	
	public void block() {
		this.pcb.setCurrentState(State.BLOCKED);
	}
	
	public void ready() {
		this.pcb.setCurrentState(State.READY);
	}
	
	public void finish() {
		this.pcb.setCurrentState(State.FINISHED);
	}
	
	public int getID() {
		return this.pcb.getProgramId();
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getMemoryLowerBound(){
		return this.pcb.getLowerMemoryBound();
	}

	public int getMemoryUpperBound(){
		return this.pcb.getUpperMemoryBound();
	}

	public int getMemorySize(){
		return this.pcb.getMemorySize();
	}

	@Override
	public String toString() {
		return "ID: " + this.pcb.getProgramId() + " Program: " + this.name;
	}
}
