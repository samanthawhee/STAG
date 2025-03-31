package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class BuildInLookDoer extends BuildInActionHandler{

    public BuildInLookDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        String playerLocation = this.gameStateAccessor.getPlayerList().getPlayer(this.commandChecker.getPlayerName()).getLocation();
        GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(playerLocation);

        // Get location description
        String locDescription = this.getLocationInfo(location, playerLocation);

        // Get the path of the location
        HashSet<String> accessiblePath = this.gameStateAccessor.getPathList().getAccessiblePath(playerLocation);

        // Get entity description
        HashSet<String> entityInfo = new HashSet<>();
        this.getArtefactsInfo(location, entityInfo);
        this.getCharactersInfo(location, entityInfo);
        this.getFurnitureInfo(location, entityInfo);

        // Get the players in the location
        HashSet<String> allPlayers = this.gameStateAccessor.getPlayerList().getPlayers();
        this.getPlayers(playerLocation, allPlayers);

        return this.responseProducer(locDescription, entityInfo, accessiblePath, allPlayers);
    }

    private String getLocationInfo(GameLocation location, String playerLocation){
        // Get the location description
        HashMap<String, HashMap<String, String>> locationInfo = location.getLocationInfo();
        HashMap<String, String> locationInfoList = locationInfo.get(playerLocation);
        for(String attrName : locationInfoList.keySet()){
            if(attrName.equals("description")){}
            return locationInfoList.get(attrName);
        }
        return "";
    }

    private void getArtefactsInfo(GameLocation location, HashSet<String> entityInfo){
        for(Map.Entry<String, HashMap<String, String>> outEntry : location.getArtefacts().entrySet()){
            HashMap<String, String> artefactInfo = outEntry.getValue();
            for(Map.Entry<String, String> currentAttr : artefactInfo.entrySet()){
                if(currentAttr.getKey().equals("description")){
                    entityInfo.add(currentAttr.getValue());
                }
            }
        }
    }

    private void getCharactersInfo(GameLocation location, HashSet<String> entityInfo){
        for(Map.Entry<String, HashMap<String, String>> outEntry : location.getCharacters().entrySet()){
            HashMap<String, String> characterInfo = outEntry.getValue();
            for(Map.Entry<String, String> currentAttr : characterInfo.entrySet()){
                if(currentAttr.getKey().equals("description")){
                    entityInfo.add(currentAttr.getValue());
                }
            }
        }
    }

    private void getFurnitureInfo(GameLocation location, HashSet<String> entityInfo){
        for(Map.Entry<String, HashMap<String, String>> outEntry : location.getFurniture().entrySet()){
            HashMap<String, String> furnitureInfo = outEntry.getValue();
            for(Map.Entry<String, String> currentAttr : furnitureInfo.entrySet()){
                if(currentAttr.getKey().equals("description")){
                    entityInfo.add(currentAttr.getValue());
                }
            }
        }
    }

    private void getPlayers(String playerLocation, HashSet<String> allPlayers){
        Iterator<String> playrsIterator = allPlayers.iterator();
        while(playrsIterator.hasNext()){
            String currentPlayer = playrsIterator.next();
            GamePlayer playerInfo = this.gameStateAccessor.getPlayerList().getPlayer(currentPlayer);
            if(!playerInfo.getLocation().equals(playerLocation)){
                playrsIterator.remove();
            }
        }
    }

    private String responseProducer(String locDescription, HashSet<String> entityInfo, HashSet<String> accessPath, HashSet<String> players){
        String response = "You are in ";
        StringBuilder responseBuilder = new StringBuilder(response);

        responseBuilder.append(locDescription).append(". You can see:").append("\n");
        for(String entity : entityInfo){
            responseBuilder.append(entity).append("\n");
        }

        responseBuilder.append("\n").append("You can access from here:").append("\n");
        if(accessPath != null){
            for(String path : accessPath){
                responseBuilder.append(path).append("\n");
            }
        }

        responseBuilder.append("\n").append("Players who are in here:").append("\n");
        for(String player : players){
            responseBuilder.append(player).append("\n");
        }

        return responseBuilder.toString();
    }
}
