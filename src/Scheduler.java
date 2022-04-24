import java.util.Hashtable;
import java.util.LinkedList;

import exceptions.ProgramBlockedException;
import exceptions.ProgramFinishedException;

public class Scheduler {

	private static Scheduler instance;
	private LinkedList<Program> readyQueue;
	private LinkedList<Program> blockedQueue;
	private int currentTimeTick;
	private Processor processor;
	private Hashtable<Integer, LinkedList<Program>> toAddTable;
	
	private final int TIME_SLICE_AMOUNT = 2;
	
	private Scheduler(){
		this.readyQueue = new LinkedList<>();
		this.blockedQueue = new LinkedList<>();
		this.currentTimeTick = 0;
		this.toAddTable = new Hashtable<>();
	}
	
	public void run() {
		do {
			checkProgramsToAdd();
			runSlice();
			currentTimeTick++;
		} while(!this.finishedExecuting());
	}
	
	private void checkProgramsToAdd() {
		LinkedList<Program> toAddNowList = toAddTable.get(currentTimeTick);
		if(toAddNowList != null) {
			for(Program programToAdd : toAddNowList) addProgram(programToAdd);
		}
	}
	
	private void addProgram(Program program) {
		readyQueue.add(program);
	}

	private void runSlice() {
		Program programToRun = readyQueue.removeFirst();
		try {
			for(int tick = 0; tick < TIME_SLICE_AMOUNT; tick++) {
				processor.run(programToRun);
			}
			readyQueue.addLast(programToRun);
		}catch (ProgramBlockedException e) {
			blockProgram(programToRun);
		}catch (ProgramFinishedException e) {
			finishProgram(programToRun);
		}
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
	
	private void blockProgram(Program program) {
		blockedQueue.add(program);
		program.block();
	}
	
	private void finishProgram(Program program) {
		readyQueue.remove(program);
		program.finish();
	}
	
	public boolean finishedExecuting() {
		return this.readyQueue.isEmpty() && this.blockedQueue.isEmpty();
	}
	
	public int getAndIncrementTimeTick() {
		return currentTimeTick++;
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
	
}
