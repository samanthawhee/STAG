package edu.uob;

public class GameStateAccessor {
    private final GameLocationList locationList;
    private final GamePathList pathList;
    private final GamePlayerList playerList;
    private final GameActionList actionList;

    public GameStateAccessor() {
        this.locationList = new GameLocationList();
        this.pathList = new GamePathList();
        this.playerList = new GamePlayerList();
        this.actionList = new GameActionList();
    }

    public GameLocationList getLocationList() {return locationList;}

    public GamePathList getPathList() {return pathList;}

    public GamePlayerList getPlayerList() {return playerList;}

    public GameActionList getActionList() {return actionList;}

}
