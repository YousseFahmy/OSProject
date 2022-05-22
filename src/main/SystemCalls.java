package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SystemCalls {

    private static Scanner scanner = new Scanner(System.in);
    private static Memory memory = Memory.getMemoryInstance();

    public static void loadToMemory(Program program){
        memory.loadToMemory(program);
    }

    public static String readFile(String fileName){
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            StringBuilder fileContent = new StringBuilder();
            String line;
            
            while((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            
            return fileContent.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void writeFile(String fileName, String content){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static String getProcessVariable(int processId, String varIdentifier){
        return memory.getVariable(processId, varIdentifier);
    }

    public static void setProcessVariable(int processId, String varIdentifier, String varValue){
        memory.setVariable(processId, varIdentifier, varValue);
    }

    public static String getInputFromUser(){
        return scanner.nextLine();
    }

    public static void outputToScreen(Object content){
        System.out.println(content);
    }

}
