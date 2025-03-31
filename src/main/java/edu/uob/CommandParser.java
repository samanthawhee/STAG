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
        if(this.getCommandSet()){
            return this.commandSet;
        }else{
            return null;
        }
    }

    private void getCommand() {
        StringBuilder db =  new StringBuilder(this.originalCommand);
        int colonIndex = db.indexOf(":");
        int stringLength = db.length();
        this.pureCommand = db.substring(colonIndex + 1, stringLength).toLowerCase().trim();
    }

    private boolean getCommandSet(){
        int builtInActionAmount = 0;
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

                    if(currentWord.toString().equals("get")
                            ||currentWord.toString().equals("drop")
                            ||currentWord.toString().equals("look")
                            ||currentWord.toString().equals("inv")
                            ||currentWord.toString().equals("inventory")){
                        builtInActionAmount++;
                    }

                    currentWord.setLength(0);
                }
            }

            if(builtInActionAmount > 1){
                return false;
            }
        }

        if(!currentWord.isEmpty()){
            this.commandSet.add(currentWord.toString());
        }
        return true;
    }
}
