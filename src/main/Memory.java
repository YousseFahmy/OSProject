package main;

import java.util.Arrays;
import java.util.List;

import exceptions.VariableDoesNotExistException;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    private static final int VARS_PER_PROCESS = 4;
    
    private static Memory instance;
    
    private List<MemoryWord> memory;
    private Disk disk;

    private Memory(){
        this.memory = Arrays.asList(new MemoryWord[MEMORY_SIZE]);
        this.disk = Disk.getInstance();
    }

    public static Memory getMemoryInstance(){
        if(instance == null) instance = new Memory();
        return instance;
    }

    public void loadToMemory(Program program){
        // find space for program
        // if no space found
        //  find program to unload
        //  unload program
        // check if program data is already in disk
        // if in disk
        //  load from disk
        // if not in disk
        //  setup memory words for program
        //      set pcb words
        //      set code words
        //  save in memory
    }

    // TODO implement
    private boolean spaceAvailable(){
        return false;
    }

    // TODO implement
    private int findProgramToUnload(){
        return 0;
    }

    private void unloadFromMemory(Program program){
        int programMemoryLowerBound = program.getMemoryLowerBound();
        int programMemoryUpperBound = program.getMemoryUpperBound();

        DiskData toUnload = new DiskData();

        for(int memLocation = programMemoryLowerBound; memLocation < programMemoryUpperBound; memLocation++){
            toUnload.addToData(memory.get(memLocation));
            removeWordAt(memLocation);
        }

        disk.saveToDisk(program.getID(), toUnload);
    }

    private void removeWordAt(int memLocation){
        memory.set(memLocation, null);
    }

    // TODO implement
    private void reserveMemory(Program program){

    }

    // TODO implement
    private void setupPCBChunk(Program program){

    }

    // TODO implement
    private void setupCodeChunk(Program program){

    }

    public void setVariable(int programId, String variableIdentifier, String variableValue){
        String memoryWordName = programId + "_vars_" + variableIdentifier;

        String processMemoryLowerBoundName = programId + "_pcb_memLowerBound";       
        int programMemoryLowerBound = Integer.parseInt(findInMemory(processMemoryLowerBoundName));
        int programVariablesStart = programMemoryLowerBound + PCB.SIZE_IN_MEMORY;
        int programVariablesEnd = programVariablesStart + VARS_PER_PROCESS;

        for(int memLocation = programVariablesStart; memLocation < programVariablesEnd; memLocation++){
            MemoryWord wordAtLocation = memory.get(memLocation);
            if(wordAtLocation == null){
                setInMemory(memLocation, memoryWordName, variableValue);
                return;
            }

            if(wordAtLocation.getName().equals(memoryWordName)){
                setInMemory(memLocation, memoryWordName, variableIdentifier);
                return;
            }
        }

        throw new VariableDoesNotExistException("Not a process variable.");
    }

    private void setInMemory(int location, String memoryWordName, String memoryWordValue){
        MemoryWord newWord = new MemoryWord(memoryWordName, memoryWordValue);
        memory.set(location, newWord);
    }

    public String getVariable(int programId, String variableIdentifier){
        String memoryWordName = programId + "_vars_" + variableIdentifier;
        return findInMemory(memoryWordName);
    }

    private String findInMemory(String memoryWordName){
        for(MemoryWord word : memory){
            if(word == null) continue;
            if(word.getName().equals(memoryWordName)) return word.getValue();
        }
        throw new VariableDoesNotExistException();
    }

}
