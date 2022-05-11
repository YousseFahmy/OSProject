package main;

import java.util.Hashtable;

import exceptions.VariableDoesNotExistException;

public class Memory {
    
    private static Memory instance;
    private Hashtable<Integer, Hashtable<String, String>> memory;

    private Memory(){
        this.memory = new Hashtable<>();
    }

    public static Memory getMemoryInstance(){
        if(instance == null) instance = new Memory();
        return instance;
    }

    public void reserveMemory(Program program){
        int programId = program.getID();
        Hashtable<String, String> programMemory = new Hashtable<>();
        memory.put(programId, programMemory);
    }

    public String getVariable(int processId, String variableIdentifier){
        Hashtable<String, String> processVariables = memory.get(processId);
        String variableValue = processVariables.get(variableIdentifier);
        if (variableValue == null) throw new VariableDoesNotExistException();
        return variableValue;
    }

    public void setVariable(int processId, String variableIdentifier, String variableValue){
        Hashtable<String, String> processVariables = memory.get(processId);
        processVariables.put(variableIdentifier, variableValue);
    }

}
