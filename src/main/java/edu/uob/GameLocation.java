package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameLocation {
    private final HashSet<String> entityList;
    private final HashMap<String, HashMap<String, String>> locationInfo;
    private final HashMap<String, HashMap<String, String>> characters;
    private final HashMap<String, HashMap<String, String>> furniture;
    private final HashMap<String, HashMap<String, String>> artefacts;

    public GameLocation() {
        this.entityList = new HashSet<>();
        this.locationInfo = new HashMap<>();
        this.characters = new HashMap<>();
        this.furniture = new HashMap<>();
        this.artefacts = new HashMap<>();
    }

    public String getLocationName() {
        return this.locationInfo.keySet().iterator().next();
    }

    public HashSet<String> getEntityList() {
        return entityList;
    }

    public void addEntity(String entity) {

        this.entityList.add(entity);
    }

    public void removeEntity(String entity) {
        this.entityList.remove(entity);
    }

    public HashMap<String, HashMap<String, String>> getLocationInfo() {
        return this.locationInfo;
    }

    public void addLocationInfo(String location) {
        if(!this.locationInfo.containsKey(location)) {
            this.locationInfo.put(location, new HashMap<>());
        }
    }

    public void addLocationInfoAttr(String location, String attrName, String attrValue) {
        if(this.locationInfo.containsKey(location)) {
            if (!this.locationInfo.get(location).containsKey(attrName)) {
                this.locationInfo.get(location).put(attrName, attrValue);
            }
        }
    }

    public HashMap<String, HashMap<String, String>> getCharacters() {
        return this.characters;
    }

    public void addCharacter(String character) {
        if(!this.characters.containsKey(character)) {
            this.characters.put(character, new HashMap<>());
        }
    }

    public void addCharacterAttr(String character, String attrName, String attrValue) {
        if(this.characters.containsKey(character)) {
            if (!this.characters.get(character).containsKey(attrName)) {
                this.characters.get(character).put(attrName, attrValue);
            }
        }
    }

    public void removeCharacter(String character) {
        this.characters.remove(character);
    }

    public HashMap<String, HashMap<String, String>> getFurniture() {
        return this.furniture;
    }

    public void addFurniture(String furniture) {
        if(!this.furniture.containsKey(furniture)) {
            this.furniture.put(furniture, new HashMap<>());
        }
    }

    public void addFurnitureAttr(String furniture, String attrName, String attrValue) {
        if(this.furniture.containsKey(furniture)) {
            if (!this.furniture.get(furniture).containsKey(attrName)) {
                this.furniture.get(furniture).put(attrName, attrValue);
            }
        }
    }

    public void removeFurniture(String furniture) {
        this.furniture.remove(furniture);
    }

    public HashMap<String, HashMap<String, String>> getArtefacts() {
        return this.artefacts;
    }

    public void addArtefact(String artefact) {
        if(!this.artefacts.containsKey(artefact)) {
            this.artefacts.put(artefact, new HashMap<>());
        }
    }

    public void addArtefactAttr(String artefact, String attrName, String attrValue) {
        if(this.artefacts.containsKey(artefact)) {
            if (!this.artefacts.get(artefact).containsKey(attrName)) {
                this.artefacts.get(artefact).put(attrName, attrValue);
            }
        }
    }

    public void removeArtefact(String artefact) {
        this.artefacts.remove(artefact);
    }

    public String checkProperty(String entityName){

        for(String key : this.artefacts.keySet()) {
            if(key.equals(entityName)) {
                return "artefact";
            }
        }
        for(String key : this.characters.keySet()) {
            if(key.equals(entityName)) {
                return "character";
            }
        }
        for(String key : this.furniture.keySet()) {
            if(key.equals(entityName)) {
                return "furniture";
            }
        }
        return null;
    }

}
