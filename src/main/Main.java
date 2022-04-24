package main;

public class Main {

	public static void main(String[] args) {
		Program prog1 = new Program("Program_1.txt");
		Program prog2 = new Program("Program_2.txt");
		Program prog3 = new Program("Program_3.txt");
		
		Scheduler scheduler = Scheduler.getSchedulerInstance();
		
		scheduler.addProgram(prog2, 1);
		scheduler.addProgram(prog1, 0);
		scheduler.addProgram(prog3, 4);
		
		scheduler.run();
		
	}

}
