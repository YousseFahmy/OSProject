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
	
	public boolean semWait(Program requester) {
		if(this.available) {
			this.holder = requester;
			this.available = false;
			return true;
		}
		blockedList.add(requester);
		return false;
	}
	
	public void semSignal(Program releaser) {
		if(!this.available && holder.equals(releaser)) {
			this.holder = null;
			this.available = true;
		}
		
		int listSize = blockedList.size();
		for(int i = 0; i < listSize; i++) {
			blockedList.remove().setState(State.READY);
		}
	}
	
	public String getResourceName() {
		return this.resourceName;
	}
}
