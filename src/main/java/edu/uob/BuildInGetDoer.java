package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class BuildInGetDoer extends BuildInActionHandler {

    public BuildInGetDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        // Get artefact attributes
        GameLocation location = this.gameStateAccessor.getLocationList().getGameLocation(commandChecker.getPlayerLocation());
        HashMap<String, HashMap<String, String>> artifactList = location.getArtefacts();
        HashMap<String, String> artifactAttributes = artifactList.get(this.builtInEntity);

        // Add the artefact into inv of the player
        GamePlayer player = this.commandChecker.getPlayerDetail();
        player.addInventory(this.builtInEntity);
        for(Map.Entry<String, String> entry : artifactAttributes.entrySet()){
            player.addInventoryAttr(this.builtInEntity, entry.getKey(), entry.getValue());
        }

        // remove the artefact from the location
        location.removeArtefact(this.builtInEntity);
        location.removeEntity(this.builtInEntity);

        return this.responseProducer();
    }

    private String responseProducer(){
        String response = "You picked up a ";
        StringBuilder responseBuilder = new StringBuilder(response);
        responseBuilder.append(this.builtInEntity).append("\n");
        return responseBuilder.toString();
    }
}
