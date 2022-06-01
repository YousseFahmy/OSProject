package main;

public class PCB {
    public static final int SIZE_IN_MEMORY = 5;

    private static int nextProcessId = 1;

    private int programId;
    private State currentState;
    private int programCounter;
    private int memoryLowerBound;
    private int memoryUpperBound;

    public PCB(){
        this.programId = nextProcessId++;
        this.currentState = State.READY_MEMORY;
        this.programCounter = 0;
    }

    public int getMemorySize(){
        return memoryUpperBound - memoryLowerBound;
    }

    public int getProgramId(){
        return this.programId;
    }

    public State getCurrentState(){
        return this.currentState;
    }

    public void setCurrentState(State newState){
        this.currentState = newState;
    }

    public int getProgramCounter(){
        return programCounter;
    }

    public void incrementProgramCounter(){
        programCounter++;
    }

    public int getMemoryLowerBound() {
        return memoryLowerBound;
    }

    public void setMemoryLowerBound(int memoryLowerBound){
        this.memoryLowerBound = memoryLowerBound;
    }

    public int getMemoryUpperBound() {
        return memoryUpperBound;
    }

    
    public void setMemoryUpperBound(int memoryUpperBound){
        this.memoryUpperBound = memoryUpperBound;
    }

    
}
