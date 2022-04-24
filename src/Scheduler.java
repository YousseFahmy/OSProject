import java.util.LinkedList;
import java.util.PriorityQueue;

public class Scheduler {

	private PriorityQueue readyQueue;
	private LinkedList<Program> blockedQueue;
	
	private final int TIME_SLICE = 2;
	
	public void addToBlockedQueue(Program program) {
		blockedQueue.add(program);
		program.block();
	}
	
}
