package main;

public class PCB {
    public static final int SIZE_IN_MEMORY = 5;

    private static int nextProcessId = 1;

    private int programId;
    private State currentState;
    private int programCounter;
    private int lowerMemoryBound;
    private int upperMemoryBound;

    public PCB(){
        this.programId = nextProcessId++;
        this.currentState = State.READY;
        this.programCounter = 0;
    }

    public int getMemorySize(){
        return upperMemoryBound - lowerMemoryBound;
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

    public int getLowerMemoryBound() {
        return lowerMemoryBound;
    }

    public int getUpperMemoryBound() {
        return upperMemoryBound;
    }

    
}
