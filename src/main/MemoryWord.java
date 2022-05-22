package main;

public class MemoryWord {
    private String wordName;
    private String wordValue;

    public MemoryWord(){
        this.wordName = null;
        this.wordValue = null;
    }

    public MemoryWord(String variableName, String variableValue){
        this.wordName = variableName;
        this.wordValue = variableValue;
    }

    public String getName(){
        return wordName;
    }

    public void setValue(String newValue){
        this.wordValue = newValue;
    }

    public String getValue(){
        return this.wordValue;
    }
}
