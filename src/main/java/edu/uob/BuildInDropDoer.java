package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class BuildInDropDoer extends BuildInActionHandler{

    public BuildInDropDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        // Get artefact attributes from the player's inventory
        String playerName = this.commandChecker.getPlayerName();
        GamePlayer player = this.gameStateAccessor.getPlayerList().getPlayer(playerName);
        HashMap<String, HashMap<String, String>> playerInvList = player.getInventory();
        HashMap<String, String> artefactInfo = playerInvList.get(this.builtInEntity);

        // Add the artefact into the location
        GameLocation currentLocation = this.gameStateAccessor.getLocationList().getGameLocation(this.commandChecker.getPlayerLocation());
        currentLocation.addArtefact(this.builtInEntity);
        for(Map.Entry<String, String> attr : artefactInfo.entrySet()){
            currentLocation.addArtefactAttr(this.builtInEntity, attr.getKey(), attr.getValue());
        }
        currentLocation.addEntity(this.builtInEntity);

        // Remove the artefact from the player's inventory
        player.removeInventory(this.builtInEntity);

        return this.responseProducer();
    }

    private String responseProducer(){
        String response = "You dropped a ";
        StringBuilder responseBuilder = new StringBuilder(response);
        responseBuilder.append(this.builtInEntity).append("\n");
        return responseBuilder.toString();
    }
}
