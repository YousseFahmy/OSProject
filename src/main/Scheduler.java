package main;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

import exceptions.*;

public class Scheduler {

	private static final int TIME_SLICE_AMOUNT = 2;
	private static final boolean PAUSE_ANALYSIS_PRINTING = true;
	
	private static Scheduler instance;
	
	private LinkedList<Program> readyQueue;
	private LinkedList<Program> blockedQueue;
	private int currentTimeTick;
	private Processor processor;
	private Hashtable<Integer, LinkedList<Program>> toAddTable;
	private Scanner scanner;
	
	private Scheduler(){
		this.readyQueue = new LinkedList<>();
		this.blockedQueue = new LinkedList<>();
		this.currentTimeTick = 0;
		this.toAddTable = new Hashtable<>();
		this.scanner = new Scanner(System.in);
	}
	
	public static Scheduler getSchedulerInstance() {
		if(instance == null) {
			instance = new Scheduler();
			instance.initialiseProcessor();
		}
		return instance;
	}

	private void initialiseProcessor() {
		this.processor = new Processor();
	}

	public void addProgram(Program program, int time) {
		LinkedList<Program> exisitingList = toAddTable.get(time);
		if(exisitingList != null) {
			exisitingList.add(program);
		}else {
			exisitingList = new LinkedList<>();
			exisitingList.add(program);
			toAddTable.put(time, exisitingList);
		}
		
	}

	public void readyProgram(Program program) {
		blockedQueue.remove(program);
		program.ready();
		readyQueue.add(program);
	}

	public void run() {
		checkProgramsToAdd();
		while(!this.finishedExecuting()) {
			runSlice();
		}
		printTerminationMessage();
	}

	private void checkProgramsToAdd() {
		LinkedList<Program> toAddNowList = toAddTable.get(currentTimeTick);
		if(toAddNowList != null) {
			for(Program programToAdd : toAddNowList) addProgram(programToAdd);
			toAddNowList.clear();
		}
	}

	private void addProgram(Program program) {
		readyQueue.add(program);
	}

	private boolean finishedExecuting() {
		return this.readyQueue.isEmpty() && this.blockedQueue.isEmpty() && toAddTableIsEmpty();
	}

	private boolean toAddTableIsEmpty() {
		Set<Integer> keySet = toAddTable.keySet();
		for(int key : keySet) {
			if(!toAddTable.get(key).isEmpty()) return false;
		}
		return true;
	}

	private void runSlice() {
		Program programToRun = retrieveProgramToRun();
		
		try {
			for(int tick = 0; tick < TIME_SLICE_AMOUNT; tick++) {
				checkProgramsToAdd();
				printSliceAnalysis(programToRun);
				processor.run(programToRun);
				currentTimeTick++;
				if(programToRun == null) break;
			}
			if(programToRun != null) readyQueue.addLast(programToRun);
		}catch (ProgramBlockedException e) {
			blockProgram(programToRun);
			currentTimeTick++;
		}catch (ProgramFinishedException e) {
			finishProgram(programToRun);
		}catch (OSException e) {
			finishProgram(programToRun);
			releaseHeldMutexes(programToRun);
			printErrorMessage(programToRun, e);
			currentTimeTick++;
		}
	}

	private Program retrieveProgramToRun() {
		return readyQueue.pollFirst();
	}

	private void printSliceAnalysis(Program runningProgram) {
		String programName = runningProgram == null ? "None" : runningProgram.getName();
		String currentlyRunningInstruction = runningProgram == null ? "None" : runningProgram.getNextInstruction();
		System.out.println("#################");
		System.out.println("Current Tick: " + currentTimeTick);
		System.out.println("Current Program: " + programName);
		System.out.println("Current Instruction: " + currentlyRunningInstruction);
		System.out.println("Ready Queue:");
		System.out.println(readyQueue);
		System.out.println("Blocked Queue:");
		System.out.println(blockedQueue);
		System.out.println("#################");
		if(PAUSE_ANALYSIS_PRINTING) scanner.nextLine();
	}

	private void blockProgram(Program program) {
		blockedQueue.add(program);
		program.block();
	}

	private void finishProgram(Program program) {
		readyQueue.remove(program);
		program.finish();
	}

	private void releaseHeldMutexes(Program programToReleaseMutexes) {
		processor.releaseHeldMutexes(programToReleaseMutexes);
	}
	
	private void printErrorMessage(Program errorProgram, OSException thrownException) {
		String programName = errorProgram.getName();
		System.out.println("#################");
		System.out.println("Error Occured: " + thrownException.getClass().getSimpleName());
		System.out.println("Current Tick: " + currentTimeTick);
		System.out.println("Current Program: " + programName);
		System.out.println("#################");
		if(PAUSE_ANALYSIS_PRINTING) scanner.nextLine();
		
	}

	private void printTerminationMessage() {
		System.out.println("!!!!!!!!!!!!!!!!!!");
		System.out.println("No More Jobs. Terminating OS.");
		System.out.println("!!!!!!!!!!!!!!!!!!");
	}
	
}
