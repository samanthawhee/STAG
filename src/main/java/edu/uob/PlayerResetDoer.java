package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class PlayerResetDoer {
    private final GameStateAccessor gameStateAccessor;
    private final GamePlayer player;

    public PlayerResetDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        this.gameStateAccessor = gameStateAccessor;
        this.player = this.gameStateAccessor.getPlayerList().getPlayer(commandChecker.getPlayerName());
    }

    public String playerReset() {
        this.dropEntity();
        this.resetPlayerLocation();
        this.resetPlayerHealth();
        return "you died and lost all of your items, you must return to the start of the game";
    }

    private void dropEntity(){
        // Drop all entities in the current location
        // Get artefact attributes from the player's inventory

        HashMap<String, HashMap<String, String>> playerInvList = this.player.getInventory();

        // Add the artefact into the location

        GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(this.player.getLocation());
        for(Map.Entry<String, HashMap<String, String>> entry : playerInvList.entrySet()){
            String entityName = entry.getKey();
            HashMap<String, String> entityInfo = entry.getValue();
            String entityProperty = entityInfo.get("property");

            location.addEntity(entityName);
            if(entityProperty != null){
                switch(entityProperty){
                    case "artefact":
                        location.addArtefact(entityName);
                        for(Map.Entry<String, String> currentAttr : entityInfo.entrySet()){
                            location.addArtefact(entityName);
                            location.addArtefactAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        }
                        break;
                    case "character":
                        location.addCharacter(entityName);
                        for(Map.Entry<String, String> currentAttr : entityInfo.entrySet()){
                            location.addCharacter(entityName);
                            location.addCharacterAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        }
                        break;
                    case "furniture":
                        location.addFurniture(entityName);
                        for(Map.Entry<String, String> currentAttr : entityInfo.entrySet()){
                            location.addFurniture(entityName);
                            location.addFurnitureAttr(entityName, currentAttr.getKey(), currentAttr.getValue());
                        }
                        break;
                    default: break;
                }
            }
        }

        // Remove the artefact from the player's inventory
        this.player.getInventory().clear();

    }

    private void resetPlayerLocation(){
        // Player's location change to the start location
        String startLocation = this.gameStateAccessor.getLocationList().getStartLocation();
        this.player.setLocation(startLocation);
    }

    private void resetPlayerHealth(){
        // Reset player's health to 3
        this.player.setHealth(3);
    }
}
