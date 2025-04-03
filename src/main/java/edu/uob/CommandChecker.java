package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CommandChecker {
    private final GameStateAccessor gameStateAccessor;
    private final HashSet<String> commandSet;
    private final HashSet<String> triggerList;
    private final HashSet<String> entityList;
    private final String playerName;
    private GamePlayer playerDetail;
    private String playerLocation;


    public CommandChecker(String command, GamePlayerCreator playerCreator, GameStateAccessor stateAccessor) {
        this.gameStateAccessor = stateAccessor;
        CommandParser commandParser = new CommandParser(command);
        this.commandSet = commandParser.parseCommand();
        this.playerName = playerCreator.getPlayerName();
        this.triggerList = new HashSet<>();
        this.entityList = new HashSet<>();

    }

    public String checkCommand (){
        if(this.commandSet == null || this.commandSet.isEmpty()){
            return "You can only have 1 built-in command";
        }
        this.retrieveData();

        if(!this.getDataList()) {
            if (this.triggerList.size() > 1) {
                return "More than one action found";
            }else if(this.triggerList.isEmpty()) {
                return "No action found";
            }
        }

        if(this.triggerList.size() != 1){
            return "More than one action found";
        }else{
            if(this.isBuiltInAction()){
                return this.checkBuiltInEntityValid(this.getKeyPhrase());
            }else if(!this.entityList.isEmpty()){
                return "Execute";
            }else{
                return "Entity not found";
            }
        }
    }

    // Get the data to check action validity later on
    private boolean getDataList(){
        this.getTriggerPhrase();
        if(this.triggerList.size() != 1){ return false; }

        if(isBuiltInAction()){
            this.addBuiltInEntity(this.getKeyPhrase());
        }else{
            this.addActionEntity(this.getKeyPhrase());
        }
        return true;
    }

    // Retrieve the game state data
    private void retrieveData(){
        this.playerDetail = this.gameStateAccessor.getPlayerList().getPlayer(this.playerName);
        this.playerLocation = this.playerDetail.getLocation();

    }

    // Check if the built-in action valid
    private String checkBuiltInEntityValid(String keyPhrase){
        if(keyPhrase == null){ return "Can not recognize the action"; }

        switch(keyPhrase){
            case "get": return this.checkGetEntityValid();
            case "drop": return this.checkDropEntityValid();
            case "goto":return this.checkGotoEntityValid();
            case "look", "inv", "inventory", "health": return this.checkOthersEntityValid();
            default: return "Action not found";
        }
    }

    private String checkGetEntityValid(){
        if(this.entityList.isEmpty()){
            return "Artefact not found";
        }else if(this.entityList.size() != 1){
            return "Get one artefact at once";
        }else{
            return "Execute";
        }
    }

    private String checkDropEntityValid(){
        if(this.entityList.isEmpty()) {
            return "Artefact not found";
        }else if(this.entityList.size() != 1){
            return "Drop one artefact at once";
        }else{
            return "Execute";
        }
    }

    private String checkGotoEntityValid(){
        String gotoLocation = this.getEntityPhrase();
        if(this.entityList.isEmpty()) {
            return "Location not found";
        }else if(this.entityList.size() != 1){
            return "Goto one location at once";
        }else{
            return this.checkGotoValid(gotoLocation);
        }
    }

    private String checkOthersEntityValid(){
        if(this.entityList.isEmpty()){
            return "Execute";
        }else{
            return "Execute action with only action phrase";
        }
    }

    // Check the path exists
    private String checkGotoValid(String gotoLocation){
        if(gotoLocation.equalsIgnoreCase(this.getPlayerLocation())){
            return "You are already in the location";
        }
        HashMap<String, HashSet<String>> pathList = this.gameStateAccessor.getPathList().getPath();
        for(String currentLocation : pathList.keySet()){
            if(currentLocation.equalsIgnoreCase(this.playerLocation)){
                HashSet<String> paths = pathList.get(currentLocation);
                for(String path : paths){
                    if(path.equalsIgnoreCase(gotoLocation)){
                        return "Execute";
                    }
                }
            }
        }
        return "Path not found";
    }

    // Add entities into entityList
    private void addBuiltInEntity(String keyPhrase){
        switch(keyPhrase){
            case "get": this.addGetEntity(); break;
            case "drop": this.addDropEntity(); break;
            case "goto": this.addGotoEntity(); break;
            case "inv", "inventory", "look", "health": this.addOthersEntity(keyPhrase); break;
            default: break;
        }
    }

    void addGetEntity(){
        // Add the entity into entityList if the entity is in the location
        GameLocation locationDetail = this.gameStateAccessor.getLocationList().getGameLocation(this.playerLocation);
        HashMap<String, HashMap<String, String>> locationArtefactsList = locationDetail.getArtefacts();
        for(String currentWord : this.commandSet){
            for(String artifactPhrase : locationArtefactsList.keySet()){
                if(artifactPhrase.equalsIgnoreCase(currentWord)){
                    this.entityList.add(currentWord.toLowerCase());
                }
            }
        }
    }

    void addDropEntity(){
        // Check the entity is one of all entities in the entity file
        // Add the entity into the entityList if the entity is in the player's inv
        HashSet<String> allArtefactsSet = this.gameStateAccessor.getLocationList().getArtefactsList();
        for(String currentWord : this.commandSet){
            for (String artifactPhrase : allArtefactsSet) {
                if (artifactPhrase.equalsIgnoreCase(currentWord)) {
                    if(this.playerDetail.hasInventory(currentWord)){
                        this.entityList.add(currentWord.toLowerCase());
                    }
                }
            }
        }
    }

    void addGotoEntity(){
        // Add the location into locationList if the location exists
        HashSet<String> locationsList = this.gameStateAccessor.getLocationList().getLocationsList();
        for(String currentWord : this.commandSet){
            for(String currentLocation : locationsList){
                if(currentLocation.equalsIgnoreCase(currentWord)){
                    this.entityList.add(currentWord.toLowerCase());
                }
            }
        }
    }

    void addOthersEntity(String keyPhrase){
        keyPhrase = keyPhrase.toLowerCase();
        for(String entityPhrase : this.commandSet){
            if(!entityPhrase.equalsIgnoreCase(keyPhrase)){
                this.entityList.add(entityPhrase.toLowerCase());
            }
        }
    }

    // Add entities into entityList if it appears in the subjectList
    private void addActionEntity(String triggerPhrase){
        HashSet<String> subjectList = this.gameStateAccessor.getActionList().getSubjects(triggerPhrase);
        for (String currentWord : this.commandSet) {
            for(String currentSubject : subjectList){

                if (currentWord.equalsIgnoreCase(currentSubject)) {
                    this.entityList.add(currentWord.toLowerCase());
                }
            }
        }
    }

    // Check if the trigger key phrase is built-in phrase
    public boolean isBuiltInAction(){
        String keyPhrase = this.getKeyPhrase();
        if(keyPhrase == null){
            return false;
        }else{
            return keyPhrase.equalsIgnoreCase("inv")
                    || keyPhrase.equalsIgnoreCase("inventory")
                    || keyPhrase.equalsIgnoreCase("get")
                    || keyPhrase.equalsIgnoreCase("drop")
                    || keyPhrase.equalsIgnoreCase("goto")
                    || keyPhrase.equalsIgnoreCase("look")
                    || keyPhrase.equalsIgnoreCase("health");
        }
    }

    // Add the trigger phrase if it is a built-in or appearing in the actionList
    private void getTriggerPhrase() {
        this.addBuiltInAction("inventory");
        this.addBuiltInAction("inv");
        this.addBuiltInAction("get");
        this.addBuiltInAction("drop");
        this.addBuiltInAction("goto");
        this.addBuiltInAction("look");
        this.addBuiltInAction("health");
        this.addAction();
    }

    // Add built-in action from commandSet
    private void addBuiltInAction(String action) {
        for (String currentWord : this.commandSet) {
            if (currentWord.equalsIgnoreCase(action)) {
                this.triggerList.add(currentWord);
            }
        }
    }

    // Add action from commandSet
    private void addAction() {
        HashSet<String> triggerPhrases = this.gameStateAccessor.getActionList().getActionTriggerPhrases();
        for (String currentWord : this.commandSet) {
            for(String currentTrigger : triggerPhrases){

                if (currentWord.equalsIgnoreCase(currentTrigger)) {
                    this.triggerList.add(currentWord);
                }
            }
        }
    }

    public String getKeyPhrase(){
        Iterator<String> triggerIterator = this.triggerList.iterator();
        if(triggerIterator.hasNext()){
            return triggerIterator.next().toLowerCase();

        }
        return null;
    }

    public String getEntityPhrase(){
        Iterator<String> entityIterator = this.entityList.iterator();
        if(entityIterator.hasNext()){
            return entityIterator.next();
        }
        return null;
    }

    public HashSet<String> getEntityList(){
        return this.entityList;
    }

    public GamePlayer getPlayerDetail(){
        return this.playerDetail;
    }

    public String getPlayerLocation(){
        return this.playerLocation;
    }

    public String getPlayerName(){
        return this.playerName;
    }
}
