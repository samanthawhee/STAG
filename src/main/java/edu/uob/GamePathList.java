package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GamePathList {
    private final HashMap<String, HashSet<String>> path;

    public GamePathList() {
        this.path = new HashMap<>();
    }

    public HashMap<String, HashSet<String>> getPath() {
        return this.path;
    }

    public HashSet<String> getAccessiblePath(String location) {
        return this.path.get(location);
    }

    public void addPath(String fromLoc, String desLoc) {
        this.path.putIfAbsent(fromLoc, new HashSet<>());
        this.path.get(fromLoc).add(desLoc);
    }

    public void removePath(String fromLoc, String desLoc) {
        if(this.path.containsKey(fromLoc)) {
            HashSet<String> desLocSet = this.path.get(fromLoc);
            for(String desLocStr : desLocSet) {
                if(desLocStr.equals(desLoc)) {
                    desLocSet.remove(desLocStr);
                }
            }
        }
    }

    public boolean hasPath(String fromLoc, String desLoc) {
        return this.path.containsKey(fromLoc) && this.path.get(fromLoc).contains(desLoc);
    }
}
