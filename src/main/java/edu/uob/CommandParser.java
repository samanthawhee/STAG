package edu.uob;

import java.util.HashSet;

public class CommandParser {
    private final String originalCommand;
    private String pureCommand;
    private final HashSet<String> commandSet;

    public CommandParser(String originalCommand) {
        this.originalCommand = originalCommand;
        this.commandSet = new HashSet<>();

    }

    public HashSet<String> parseCommand() {

        this.getCommand();
        this.getCommandSet();
        return this.commandSet;
    }

    private void getCommand() {
        StringBuilder db =  new StringBuilder(this.originalCommand);
        int colonIndex = db.indexOf(":");
        int stringLength = db.length();
        this.pureCommand = db.substring(colonIndex + 1, stringLength).toLowerCase().trim();
    }

    private void getCommandSet(){
        StringBuilder commandContent =  new StringBuilder(this.pureCommand);
        StringBuilder currentWord = new StringBuilder();

        for(int i = 0; i < commandContent.length(); i++){
            char currentChar = commandContent.charAt(i);

            if(currentChar != ' '){
                currentWord.append(currentChar);
            }

            if(currentChar == ' ' || currentChar == '\n'){

                if(!currentWord.isEmpty()){
                    this.commandSet.add(currentWord.toString());

                    currentWord.setLength(0);
                }
            }

        }
        if(!currentWord.isEmpty()){
            this.commandSet.add(currentWord.toString());
        }
    }
}
