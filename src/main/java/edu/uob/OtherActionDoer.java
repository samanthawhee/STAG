package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class OtherActionDoer{
    private final GameStateAccessor gameStateAccessor;
    protected final CommandChecker commandChecker;
    protected final String keyPhrase;
    protected final HashSet<String> entityList;

    public OtherActionDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor){
        this.gameStateAccessor = gameStateAccessor;
        this.commandChecker = commandChecker;
        this.keyPhrase = commandChecker.getKeyPhrase();
        this.entityList = this.commandChecker.getEntityList();
    }


    public String executeAction(){

        if(!this.checkSubjectsAvailable()){ return "The entities not all available"; }

        this.doConsume();
        this.doProduce();
        return this.checkPlayerAlive(this.getNarration());
    }

    private void doConsume(){
        for(String currentConsume : this.getConsumeList()){
            if(currentConsume.equals("health")){
                this.decrementHealth();
            }else if(this.checkIsLocation(currentConsume)){
                this.removePath(currentConsume);
            }else{
                this.removeEntity(currentConsume);
            }
        }
    }

    private void doProduce(){
        for(String currentProduce : this.getProduceList()){
            if(currentProduce.equals("health")){
                this.incrementHealth();
            }else if(this.checkIsLocation(currentProduce)){
                this.addPath(currentProduce);
            }else{
                this.produceEntity(currentProduce);
                this.getNarration();
            }
        }
    }

    private String getNarration(){
        return this.gameStateAccessor.getActionList().getGameAction(this.keyPhrase).getNarration();
    }

    private String checkPlayerAlive(String respond){
        GamePlayer player = this.commandChecker.getPlayerDetail();
        if(player.getHealth() <= 0){
            PlayerResetDoer playerResetDoer = new PlayerResetDoer(this.commandChecker, this.gameStateAccessor);
            StringBuilder respondBuilder = new StringBuilder(respond);
            respondBuilder.append("\n").append(playerResetDoer.playerReset());
            return respondBuilder.toString();
        }else{
            return respond;
        }
    }

    private boolean checkSubjectsAvailable(){
        // Check the subjects are in the location or player's inv
        String keyPhrase = this.commandChecker.getKeyPhrase();
        HashSet<String> subjectsList = this.gameStateAccessor.getActionList().getSubjects(keyPhrase);

        String playerLocation = this.commandChecker.getPlayerLocation();
        HashSet<String> entityList = this.gameStateAccessor.getLocationList().getGameLocation(playerLocation).getEntityList();
        GamePlayer player = this.commandChecker.getPlayerDetail();
        HashMap<String, HashMap<String, String>> invList = player.getInventory();

        for (String subject : subjectsList) {
            if (!entityList.contains(subject)) {
                if (!invList.containsKey(subject)) {
                    return false;
                }
            }
        }
        return true;
    }

    private HashSet<String> getConsumeList(){
        HashSet<String> consumeList = new HashSet<>();
        HashSet<GameAction> gameActionList = this.gameStateAccessor.getActionList().getGameActions();
        Iterator<GameAction> iterator = gameActionList.iterator();
        while(iterator.hasNext()) {
            GameAction gameAction = iterator.next();
            if (gameAction.getTriggers().contains(this.keyPhrase)) {
                consumeList.addAll(gameAction.getConsumed());
            }
        }
        return consumeList;
    }

    private HashSet<String> getProduceList(){
        HashSet<String> produceList = new HashSet<>();
        HashSet<GameAction> gameActionList = this.gameStateAccessor.getActionList().getGameActions();
        Iterator<GameAction> iterator = gameActionList.iterator();
        while(iterator.hasNext()) {
            GameAction gameAction = iterator.next();
            if (gameAction.getTriggers().contains(this.keyPhrase)) {
                produceList.addAll(gameAction.getProduced());
            }
        }
        return produceList;
    }

    private void decrementHealth(){
        String playerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(playerName);
        if(player.getHealth() > 0){
            player.setHealth(player.getHealth() - 1);
        }
    }
    private void incrementHealth(){
        // Increment health by 1 if health is smaller than 3
        String playerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(playerName);
        if(player.getHealth() > 0 && player.getHealth() < 3){
            player.setHealth(player.getHealth() + 1);
        }
    }

    private void removePath(String consumeLocation){
        String currentLocation = this.commandChecker.getPlayerLocation();
        this.gameStateAccessor.getPathList().removePath(currentLocation, consumeLocation);
    }

    private void addPath(String produceLocation){
        // Add the path from current location to consumed location
        String currentLocation = this.commandChecker.getPlayerLocation();
        this.gameStateAccessor.getPathList().addPath(currentLocation, produceLocation);
    }

    private HashMap<String, String> getEntityAttributes(String entityName){
        String PlayerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(PlayerName);

        if(player.hasInventory(entityName)){
            return player.getInventory().get(entityName);
        }else{
            String currentLocation = this.commandChecker.getPlayerLocation();
            GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(currentLocation);
            String entityProperty = location.checkProperty(entityName);

            if(entityProperty != null){
                switch(entityProperty){
                    case "artefact": return location.getArtefacts().get(entityName);
                    case "character": return location.getCharacters().get(entityName);
                    case "furniture": return location.getFurniture().get(entityName);
                    default: return null;
                }
            }
            return null;
        }
    }

    private void removeEntity(String entityName){
        String PlayerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(PlayerName);
        HashMap<String, String> attributesList = this.getEntityAttributes(entityName);
        GameLocation storeroom = this.gameStateAccessor.getLocationList().getGameLocation("storeroom");
        storeroom.addEntity(entityName);

        if(player.hasInventory(entityName)){
            this.removeFromInventory(entityName, storeroom, attributesList);
        }else{
            this.removeFromLocation(entityName, storeroom, attributesList);
        }
    }

    private void removeFromLocation(String entityName, GameLocation storeroom, HashMap<String, String> attributesList){
        String currentLocation = this.commandChecker.getPlayerLocation();
        GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(currentLocation);
        String entityProperty = location.checkProperty(entityName);
        if(entityProperty != null){
            switch(entityProperty){
                case "artefact":
                    storeroom.addArtefact(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addArtefactAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    location.removeArtefact(entityName);
                    break;

                case "character":
                    storeroom.addCharacter(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addCharacterAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    location.removeCharacter(entityName);
                    break;

                case "furniture":
                    storeroom.addFurniture(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addFurnitureAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    location.removeFurniture(entityName);
                    break;

                default: break;
            }
        }
    }

    private void removeFromInventory(String entityName, GameLocation storeroom, HashMap<String, String> attributesList){
        String playerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(playerName);
        String entityProperty = player.checkEntityProperty(entityName);
        if(entityProperty != null){
            switch(entityProperty){
                case "artefact":
                    storeroom.addArtefact(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addArtefactAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    player.getInventory().remove(entityName);
                    break;
                case "character":
                    storeroom.addCharacter(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addCharacterAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    player.getInventory().remove(entityName);
                    break;
                case "furniture":
                    storeroom.addFurniture(entityName);
                    for(Map.Entry<String, String> currentAttr : attributesList.entrySet()){
                        storeroom.addFurnitureAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                    }
                    player.getInventory().remove(entityName);
                    break;
                default: break;

            }
        }
    }


    private void produceEntity(String entityName){
        String locationName = this.gameStateAccessor.getLocationList().checkEntityLocation(entityName);
        this.addEntityToLocation(entityName, locationName);
        this.removeEntityFromLocation(entityName, locationName);
    }

    private void addEntityToLocation(String entityName, String entityLocationName){
        // Add the entity in the location which the action was triggered
        String currentLocation = this.commandChecker.getPlayerLocation();
        GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(currentLocation);
        HashMap<String, String> entityAttributes = this.getEntityAttributeList(entityName,entityLocationName);
        GameLocation entityLocation = this.gameStateAccessor.getLocationList().getGameLocation(entityLocationName);
        String entityProperty = entityLocation.checkProperty(entityName);

        if(entityProperty != null){
            location.addEntity(entityName);
            for(Map.Entry<String, String> currentAttr : entityAttributes.entrySet()){
                switch(entityProperty){
                    case "artefact":
                        location.addArtefact(entityName);
                        location.addArtefactAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        break;
                    case "character":
                        location.addCharacter(entityName);
                        location.addCharacterAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        break;
                    case "furniture":
                        location.addFurniture(entityName);
                        location.addFurnitureAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        break;
                    default: break;
                }
            }
        }
    }

    private void removeEntityFromLocation(String entityName, String entityLocationName){
        // Remove the entity from the original location
        GameLocation entityLocation = this.gameStateAccessor.getLocationList().getGameLocation(entityLocationName);
        String entityProperty = entityLocation.checkProperty(entityLocationName);
        if(entityProperty != null){
            switch(entityProperty){
                case "artefact":
                    entityLocation.removeArtefact(entityName);
                    break;
                case "character":
                    entityLocation.removeCharacter(entityName);
                    break;
                case "furniture":
                    entityLocation.removeFurniture(entityName);
                    break;
                default: break;
            }
        }
    }

    private HashMap<String, String> getEntityAttributeList(String entityName, String entityLocationName){
        GameLocation entityLocation = this.gameStateAccessor.getLocationList().getGameLocation(entityLocationName);
        String entityProperty = entityLocation.checkProperty(entityName);
        if(entityProperty != null){
            switch(entityProperty){
                case "artefact": return entityLocation.getArtefacts().get(entityName);
                case "character": return entityLocation.getCharacters().get(entityName);
                case "furniture": return entityLocation.getFurniture().get(entityName);
                default: break;
            }
        }
        return null;
    }

    private boolean checkIsLocation(String currentProduce){
        HashSet<String> locationList = this.gameStateAccessor.getLocationList().getLocationsList();
        return locationList.contains(currentProduce);
    }
}
