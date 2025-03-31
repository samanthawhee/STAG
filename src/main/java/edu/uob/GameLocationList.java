package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class GameLocationList {
    static String startLocation;
    private final HashSet<String> artefactsList;
    private final HashSet<String> locationsList;
    private final HashMap<String, GameLocation> gameLocations;

    public GameLocationList() {
        startLocation = null;
        this.gameLocations = new HashMap<>();
        this.artefactsList = new HashSet<>();
        this.locationsList = new HashSet<>();
    }

    public HashMap<String, GameLocation> getGameLocations() {
        return gameLocations;
    }

    public GameLocation getGameLocation(String locationName) {
        Iterator<String> gameLocationIterator = gameLocations.keySet().iterator();
        while (gameLocationIterator.hasNext()) {
            String currentLocation = gameLocationIterator.next();
            if (currentLocation.equals(locationName)) {
                return gameLocations.get(currentLocation);
            }
        }
        return null;
    }

    public String checkEntityLocation(String entityName){
        // Find the entity location
        for(Map.Entry<String, GameLocation> location : this.getGameLocations().entrySet()){
            GameLocation currentLoc = location.getValue();
            String entityProperty = currentLoc.checkProperty(entityName);
            if(entityProperty != null){
                switch(entityProperty){
                    case "artefact":
                        if(currentLoc.getArtefacts().containsKey(entityName)){ return currentLoc.getLocationName();}
                        break;
                    case "character":
                        if(currentLoc.getCharacters().containsKey(entityName)){ return currentLoc.getLocationName();}
                        break;
                    case "furniture":
                        if(currentLoc.getFurniture().containsKey(entityName)){ return currentLoc.getLocationName();}
                        break;
                    default: break;
                }
            }
        }
        return null;
    }

    public void addGameLocation(String locationName, GameLocation locationObject) {
        this.gameLocations.put(locationName, locationObject);
    }

    public boolean hasGameLocation(String locationName) {
        return this.gameLocations.containsKey(locationName);
    }

    public void addArtefact(String artefactName) {
        this.artefactsList.add(artefactName);
    }

    public HashSet<String> getArtefactsList() {
        return this.artefactsList;
    }

    public void addLocation(String locationName) {
        this.locationsList.add(locationName);
    }

    public HashSet<String> getLocationsList() {
        return this.locationsList;
    }

    public void setStartLocation(String startLoc) {
        startLocation = startLoc;
    }

    public String getStartLocation() {
        return startLocation;
    }
}
