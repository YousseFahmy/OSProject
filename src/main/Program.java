package main;

import exceptions.ProgramFinishedException;
import exceptions.VariableDoesNotExistException;

public class Program {
	private String name;
	private PCB pcb;

	public Program(String fileName, PCB programPCB) {
		this.name = fileName;
		this.pcb = programPCB;
	}
	
	public String getNextInstruction() {
		int nextInstructionCounter = pcb.getProgramCounter();
		String wordName = pcb.getProgramId() + "_code_" + nextInstructionCounter;
		String nextInstruction;
		try{
			nextInstruction = SystemCalls.getFromMemory(wordName);
		}catch(VariableDoesNotExistException e){
			throw new ProgramFinishedException();
		}

		return nextInstruction;
	}
	
	public String getNextInstructionAndIncrement() {
		String instruction = this.getNextInstruction();
		pcb.incrementProgramCounter();
		return instruction;
	}
	
	public void block() {
		if(pcb.getCurrentState() == State.READY_MEMORY){
			this.pcb.setCurrentState(State.BLOCKED_MEMORY);
		}else{
			this.pcb.setCurrentState(State.BLOCKED_DISK);
		}
	}
	
	public void ready() {
		if(pcb.getCurrentState() == State.BLOCKED_MEMORY){
			this.pcb.setCurrentState(State.READY_MEMORY);
		}else{
			this.pcb.setCurrentState(State.READY_DISK);
		}
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
		return this.pcb.getMemoryLowerBound();
	}

	public int getMemoryUpperBound(){
		return this.pcb.getMemoryUpperBound();
	}

	public int getSize(){
		return pcb.getMemoryUpperBound() - pcb.getMemoryLowerBound();
	}

	@Override
	public String toString() {
		return "ID: " + this.pcb.getProgramId() + " Program: " + this.name;
	}
}
