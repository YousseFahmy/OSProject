import java.util.LinkedList;

public class Mutex {
	private String resourceName;
	private boolean available;
	private Program holder;
	private LinkedList<Program> blockedList;
	
	public Mutex(String resourceName) {
		this.resourceName = resourceName;
		this.available = true;
		this.blockedList = new LinkedList<>();
	}
	
	public void semWait(Program requester) {
		lockMutex(requester);
	}

	private void lockMutex(Program requester) {
		this.holder = requester;
		this.available = false;
	}
	
	public void semSignal(Program releaser) {
		if(validSignal(releaser)) {
			releaseMutex();
			emptyBlockedList();
		}
	}

	private boolean validSignal(Program releaser) {
		return !this.available && this.holder.equals(releaser);
	}

	private void releaseMutex() {
		this.holder = null;
		this.available = true;
	}
	
	public void addToBlockedList(Program programToBlock) {
		this.blockedList.add(programToBlock);
	}

	private void emptyBlockedList() {
		int listSize = this.blockedList.size();
		for(int i = 0; i < listSize; i++) {
			this.blockedList.remove().setState(State.READY);
		}
	}
	
	public String getResourceName() {
		return this.resourceName;
	}
	
	public boolean isAvailable() {
		return this.available;
	}
}
