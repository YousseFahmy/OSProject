package main;

public class PCB {
    private static int nextProcessId = 1;

    private int processId;
    private State currentState;
    private int programCounter;
    private int lowerMemoryBound;
    private int upperMemoryBound;

    public PCB(){
        this.processId = nextProcessId++;
        this.currentState = State.READY;
        this.programCounter = 0;
    }

    public int getProcessId(){
        return this.processId;
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
}
