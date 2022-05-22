package main;

import java.util.ArrayList;

public class DiskData {
    private ArrayList<MemoryWord> programData;

    public DiskData(){
        programData = new ArrayList<>();
    }

    public void addToData(MemoryWord dataToAdd){
        programData.add(dataToAdd);
    }

    public ArrayList<MemoryWord> getData(){
        return programData;
    }
}
