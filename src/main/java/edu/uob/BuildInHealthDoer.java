package edu.uob;

public class BuildInHealthDoer extends BuildInActionHandler {

    public BuildInHealthDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        // Get the player's health
        GamePlayer player = this.commandChecker.getPlayerDetail();
        Integer playerHealth = player.getHealth();
        return this.responseProducer(playerHealth);
    }

    private String responseProducer(Integer playerHealth){
        String response = "Your current health is : ";
        StringBuilder responseBuilder = new StringBuilder(response);
        responseBuilder.append(playerHealth).append("\n");
        return responseBuilder.toString();
    }
}
