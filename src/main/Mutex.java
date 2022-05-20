package main;

import java.util.LinkedList;

public class Mutex {
	private String resourceName;
	private boolean available;
	private Program holder;
	private LinkedList<Program> blockedList;
	private Scheduler scheduler;

	public Mutex(String resourceName) {
		this.resourceName = resourceName;
		this.available = true;
		this.blockedList = new LinkedList<>();
		this.scheduler = Scheduler.getSchedulerInstance();
	}

	public void semWait(Program requester) {
		lockMutex(requester);
	}

	private void lockMutex(Program requester) {
		this.holder = requester;
		this.available = false;
	}

	public void semSignal(Program releaser) {
		if (validSignal(releaser)) {
			releaseMutex();
			if (!this.blockedList.isEmpty()) releaseNextWaiting();
		}
	}

	private boolean validSignal(Program releaser) {
		return !this.available && this.holder.equals(releaser);
	}

	private void releaseMutex() {
		this.holder = null;
		this.available = true;
	}

	private void releaseNextWaiting() {
		Program readiedProgram = this.blockedList.poll();
		scheduler.readyProgram(readiedProgram);
	}

	public void addToBlockedList(Program programToBlock) {
		this.blockedList.add(programToBlock);
	}

	public String getResourceName() {
		return this.resourceName;
	}

	public boolean isAvailable() {
		return this.available;
	}

	public boolean isHolder(Program program) {
		return program.equals(holder);
	}
}
