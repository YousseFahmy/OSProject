package main;

import java.util.Hashtable;

public class Disk {
    private static Disk instance;

    private Hashtable<Integer, DiskData> disk;

    private Disk(){
        disk = new Hashtable<>();
    }

    public static Disk getInstance(){
        if(instance == null) instance = new Disk();
        return instance;
    }

    public void saveToDisk(int programId, DiskData programData){
        disk.put(programId, programData);
    }

    public DiskData loadFromDisk(int programId){
        return disk.get(programId);
    }

    public void printDisk(){
        // TODO implement
    }
}
