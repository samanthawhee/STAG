package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GamePlayer {
    private final String name;
    private int health;
    private String location;
    private final HashMap<String, HashMap<String, String>> inventory;

    public GamePlayer(String name, String location) {
        this.name = name;
        this.health = 3;
        this.location = location;
        this.inventory = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, HashMap<String, String>> getInventory() {
        return this.inventory;
    }

    public void addInventory(String inventory) {
        if(!this.inventory.containsKey(inventory)) {
            this.inventory.put(inventory, new HashMap<String, String>());
        }
    }

    public void addInventoryAttr(String inventory, String attrName, String attrValue) {
        if(this.inventory.containsKey(inventory)) {
            if (!this.inventory.get(inventory).containsKey(attrName)) {
                this.inventory.get(inventory).put(attrName, attrValue);
            }
        }
    }

    public void removeInventory(String inventory) {
        this.inventory.remove(inventory);
    }

    public boolean hasInventory(String inventory) {
        return this.inventory.containsKey(inventory);
    }

    public String checkEntityProperty(String entity) {
        HashMap<String, String> entityAttributesList = this.inventory.get(entity);
        for(Map.Entry<String, String> entry : entityAttributesList.entrySet()) {
            if(entry.getKey().equals("property")) {
                return entry.getValue();
            }
        }
        return null;
    }

}
