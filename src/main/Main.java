package main;

public class Main {

	public static void main(String[] args) {
		Scheduler scheduler = Scheduler.getSchedulerInstance();
		Memory.getMemoryInstance();
		Disk.getInstance();
		ProgramHandler.getInstance();

		scheduler.addProgram("Program_1.txt", 0); // PrintFromTo
		scheduler.addProgram("Program_2.txt", 1); // Write To File
		scheduler.addProgram("Program_3.txt", 4); // Read From File
		// scheduler.addProgram("Program_4.txt", 1); // Throw VariableDoesNotExistException
		// scheduler.addProgram("Program_5.txt", 20); // Simple IO

		scheduler.run();

	}

}
