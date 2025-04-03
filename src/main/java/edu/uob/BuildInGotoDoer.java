package edu.uob;

public class BuildInGotoDoer extends BuildInActionHandler{
    private final BuildInLookDoer buildInLookDoer;

    public BuildInGotoDoer(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        super(commandChecker, gameStateAccessor);
        this.buildInLookDoer = new BuildInLookDoer(commandChecker, gameStateAccessor);
    }

    @Override
    public String executeAction(){
        // Change the current location for player
        GamePlayer player = this.commandChecker.getPlayerDetail();
        player.setLocation(this.builtInEntity);
        return this.buildInLookDoer.executeAction();
    }
}
