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
        System.out.println("LOADING PROGRAM " + program.getID() + " TO MEMORY");
        int programSize = program.getSize();
        program.setInMemory();
        ensureSpaceAvailable(programSize);
        int memorySpaceIdx = findSpaceStartIdx(programSize);
        DiskData programMemoryChunk = disk.loadFromDisk(program.getID());
        for(int wordOffset = 0; wordOffset < programMemoryChunk.getData().size(); wordOffset++){
            MemoryWord memoryWord = programMemoryChunk.getData().get(wordOffset);
            String programLowerMemory = program.getID() + "_pcb_memLowerBound";
            String programUpperMemory = program.getID() + "_pcb_memUpperBound";
            if(memoryWord.getName().equals(programLowerMemory)){
                memory.set(memorySpaceIdx + wordOffset++, new MemoryWord(programLowerMemory, memorySpaceIdx + ""));
                memory.set(memorySpaceIdx + wordOffset, new MemoryWord(programUpperMemory, memorySpaceIdx + programSize + ""));
            }else{
                memory.set(memorySpaceIdx + wordOffset, memoryWord);
            }
        }
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
            if(programSizes.get(key) > biggestSize){
                biggestProgram = key;
                biggestSize = programSizes.get(key);
            } 
        }

        return biggestProgram;
    }

    private void unloadFromMemory(int programId){
        System.out.println("UNLOADING PROGRAM " + programId + " FROM MEMORY");
        DiskData dataToUnload = new DiskData();
        Program programtoUnload = ProgramHandler.getInstance().getProgramById(programId);
        programtoUnload.setOnDisk();

        for(int memLocation = 0; memLocation < MEMORY_SIZE; memLocation++){
            MemoryWord word = memory.get(memLocation);
            if(word == null) continue;
            String[] wordName = word.getName().split("_");
            int wordProgramId = Integer.parseInt(wordName[0]);    
            if(programId == wordProgramId){
                dataToUnload.addToData(word);
                removeWordAt(memLocation);
            }
        }

        if(ProgramHandler.getInstance().getProgramById(programId).getState() == State.FINISHED) return;

        disk.saveToDisk(programId, dataToUnload);
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
            String[] wordName = wordAtLocation.getName().split("_");
            if(wordName[2].equals(".freeVariable")){
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

    public static void printMemory(){
        System.out.println("#################");
        for(int wordIdx = 0; wordIdx < MEMORY_SIZE; wordIdx++){
            MemoryWord wordAtIdx = instance.memory.get(wordIdx);
            if(wordAtIdx != null)
                System.out.println(wordIdx + ": " + wordAtIdx.getName() + " = " + wordAtIdx.getValue());
                else
                System.out.println(wordIdx + ": EMPTY");

        }
        System.out.println("#################");
    }

}
