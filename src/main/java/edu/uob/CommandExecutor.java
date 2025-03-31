package edu.uob;

import java.util.HashMap;

public class CommandExecutor {
    private final CommandChecker commandChecker;
    private final HashMap<String, BuildInActionHandler> buildInActionMap;
    private final GameStateAccessor gameStateAccessor;

    public CommandExecutor(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        this.commandChecker = commandChecker;
        this.gameStateAccessor = gameStateAccessor;
        this.buildInActionMap = createBuildInActionMap();

    }

    public String executeAction(){
        if(this.commandChecker.isBuiltInAction()){
            return this.executeBuiltInAction();
        }else{
            return new OtherActionDoer(this.commandChecker, this.gameStateAccessor).executeAction();
        }
    }

    private String executeBuiltInAction(){
        String keyPhrase = this.commandChecker.getKeyPhrase().toLowerCase();
        if(this.buildInActionMap.containsKey(keyPhrase)){
            return buildInActionMap.get(keyPhrase).executeAction();
        }else{
            return "Unknown action";
        }
    }

    private HashMap<String, BuildInActionHandler> createBuildInActionMap(){
        HashMap<String, BuildInActionHandler> buildInActionMap = new HashMap<>();
        buildInActionMap.put("get", new BuildInGetDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("drop", new BuildInDropDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("goto", new BuildInGotoDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("look", new BuildInLookDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("inv", new BuildInInvDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("inventory", new BuildInInvDoer(this.commandChecker, this.gameStateAccessor));
        buildInActionMap.put("health", new BuildInHealthDoer(this.commandChecker, this.gameStateAccessor));
        return buildInActionMap;
    }
}
