package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class Disk {
    private static final String diskPrefix = "disk/";

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
        writeDiskToFile(programId);
    }

    private void writeDiskToFile(int programId){
        String filePath = diskPrefix + programId + ".txt";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            DiskData processData = disk.get(programId);
            for(MemoryWord word : processData.getData()){
                writer.write(word.toString());
                writer.newLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public DiskData loadFromDisk(int programId){
        DiskData programData = new DiskData();
        String filePath = diskPrefix + programId + ".txt";
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = reader.readLine()) != null){
                String wordName = line.split(MemoryWord.NAME_DATA_DELIMITER)[0];
                String wordData = line.split(MemoryWord.NAME_DATA_DELIMITER)[1];
                String wordProgramIdStr = wordName.split("_")[0];
                int wordProgramId = Integer.parseInt(wordProgramIdStr);
                if(wordProgramId == programId){
                    programData.addToData(new MemoryWord(wordName, wordData));
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        try{
            File diskFile = new File(filePath);
            if(diskFile.exists()) diskFile.delete();
            diskFile.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }

        return programData;
    }

    public void printDisk(){
        // TODO implement
    }
}
