package main;

public class Main {

	public static void main(String[] args) {
		Program prog1 = new Program("Program_1.txt"); // PrintFromTo
		Program prog2 = new Program("Program_2.txt"); // Read From File
		Program prog3 = new Program("Program_3.txt"); // Write To File
		Program prog4 = new Program("Program_4.txt"); // Throw VariableDoesNotExistException
		Program prog5 = new Program("Program_5.txt"); // Simple IO
		
		Scheduler scheduler = Scheduler.getSchedulerInstance();
		
		//scheduler.addProgram(prog1, 2);
		//scheduler.addProgram(prog2, 1);
		//scheduler.addProgram(prog3, 4);
		//scheduler.addProgram(prog4, 1);
		scheduler.addProgram(prog5, 20);
		
		scheduler.run();
		
	}

}
