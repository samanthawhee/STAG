package edu.uob;

import java.util.HashMap;

public class BuildInInvDoer extends BuildInActionHandler {

    public BuildInInvDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        // Get the player's inv
        GamePlayer player = this.commandChecker.getPlayerDetail();
        HashMap<String, HashMap<String, String>> playerInvList = player.getInventory();
        return this.responseProducer(playerInvList);
    }

    private String responseProducer(HashMap<String, HashMap<String, String>> playerInvList){
        String response = "Your inventory has : ";
        StringBuilder responseBuilder = new StringBuilder(response);
        responseBuilder.append("\n");
        for(String key : playerInvList.keySet()){
            responseBuilder.append(key).append("\n");
        }
        return responseBuilder.toString();
    }
}
