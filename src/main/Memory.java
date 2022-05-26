package main;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import exceptions.NotEnoughSpaceException;
import exceptions.VariableDoesNotExistException;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    
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
        // int programSize = program.getSize();
        // find space for program
        // if no space found
        //  find program to unload
        //  unload program
        // check if program data is already in disk
        // if in disk
        //  load from disk
        //  save in memory
    }

    public void put(int idx, MemoryWord obj){
        memory.set(idx, obj);
    }

    public void put(int startIdx, MemoryWord[] memoryChunk){
        int currentIdx = startIdx;
        for(MemoryWord word : memoryChunk){
            this.put(currentIdx++, word);
        }
    }

    public void ensureSpaceAvailable(int neededCapacity){
        while(!spaceAvailable(neededCapacity)){
            unloadFromMemory(findProgramToUnload());
        }
    }

    private boolean spaceAvailable(int neededCapacity){
        int freeSpaceCounter = 0;
        for(MemoryWord word : memory){
            if(word == null){
                freeSpaceCounter++;
            }else{
                freeSpaceCounter = 0;
            }
        }
        return freeSpaceCounter >= neededCapacity;
    }

    public int findSpaceStartIdx(int neededCapacity){
        int overallBiggestSpace = 0;
        int overallBiggestSpaceIdx = 0;
        int currentBiggestSpace = 0;
        int currentBiggestSpaceIdx = 0;
        for(int wordIdx = 0; wordIdx < MEMORY_SIZE; wordIdx++){
            MemoryWord word = memory.get(wordIdx);
            if(word == null) {
                currentBiggestSpace++;
            }else{
                if(currentBiggestSpace > overallBiggestSpace){
                    overallBiggestSpace = currentBiggestSpace;
                    overallBiggestSpaceIdx = currentBiggestSpaceIdx;
                }
                currentBiggestSpace = 0;
                currentBiggestSpaceIdx = wordIdx + 1;
            }
        }
        if(currentBiggestSpace > overallBiggestSpace){
            overallBiggestSpace = currentBiggestSpace;
            overallBiggestSpaceIdx = currentBiggestSpaceIdx;
        }

        if(overallBiggestSpace < neededCapacity) throw new NotEnoughSpaceException();
        return overallBiggestSpaceIdx;
    }

    private int findProgramToUnload(){
        Hashtable<Integer, Integer> programSizes = new Hashtable<>();
        
        for(MemoryWord word : memory){
            if(word == null) continue;
            String[] wordName = word.getName().split("_");
            int programId = Integer.parseInt(wordName[0]);
            
            if(programSizes.get(programId) == null){
                programSizes.put(programId, 1);
            }else{
                programSizes.put(programId, programSizes.get(programId)+1);
            }
        }

        Set<Integer> programsInMemory = programSizes.keySet();
        int biggestSize = 0;
        int biggestProgram = 0;

        for(int key : programsInMemory){
            if(programSizes.get(key) > biggestSize) biggestProgram = key;
        }

        return biggestProgram;
    }

    private void unloadFromMemory(int programId){
        DiskData toUnload = new DiskData();

        for(int memLocation = 0; memLocation < MEMORY_SIZE; memLocation++){
            MemoryWord word = memory.get(memLocation);
            String[] wordName = word.getName().split("_");
            int wordProgramId = Integer.parseInt(wordName[0]);    
            if(programId == wordProgramId){
                toUnload.addToData(word);
                removeWordAt(memLocation);
            }
        }
        disk.saveToDisk(programId, toUnload);
    }

    private void removeWordAt(int memLocation){
        memory.set(memLocation, null);
    }

    public void setVariable(int programId, String variableIdentifier, String variableValue){
        String memoryWordName = programId + "_vars_" + variableIdentifier;

        String processMemoryLowerBoundName = programId + "_pcb_memLowerBound";       
        int programMemoryLowerBound = Integer.parseInt(findInMemory(processMemoryLowerBoundName));
        int programVariablesStart = programMemoryLowerBound + PCB.SIZE_IN_MEMORY;
        int programVariablesEnd = programVariablesStart + ProgramHandler.VARS_PER_PROGRAM;

        for(int memLocation = programVariablesStart; memLocation < programVariablesEnd; memLocation++){
            MemoryWord wordAtLocation = memory.get(memLocation);
            if(wordAtLocation == null){
                setInMemory(memLocation, memoryWordName, variableValue);
                return;
            }

            if(wordAtLocation.getName().equals(memoryWordName)){
                setInMemory(memLocation, memoryWordName, variableValue);
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

    public String findInMemory(String memoryWordName){
        for(MemoryWord word : memory){
            if(word == null) continue;
            if(word.getName().equals(memoryWordName)) return word.getValue();
        }
        throw new VariableDoesNotExistException();
    }

}
