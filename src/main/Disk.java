package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;

public class Disk {
    private static final String diskPath = "src/disk.txt";

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
        writeDiskToFile();
    }

    private void writeDiskToFile(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(diskPath))){
            Set<Integer> keys = disk.keySet();
            for(Integer key : keys){
                DiskData processData = disk.get(key);
                for(MemoryWord word : processData.getData()){
                    writer.write(word.toString());
                    writer.newLine();
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public DiskData loadFromDisk(int programId){
        DiskData programData = new DiskData();

        try(BufferedReader reader = new BufferedReader(new FileReader(diskPath))){
            String line;
            while((line = reader.readLine()) != null){
                String wordName = line.split("||")[0];
                String wordData = line.split("||")[1];
                String wordProgramIdStr = wordName.split("_")[0];
                int wordProgramId = Integer.parseInt(wordProgramIdStr);
                if(wordProgramId == programId){
                    programData.addToData(new MemoryWord(wordName, wordData));
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return programData;

        // return disk.get(programId);
    }

    public void printDisk(){
        // TODO implement
    }
}
