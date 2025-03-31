package edu.uob;

abstract class BuildInActionHandler {
    protected final CommandChecker commandChecker;
    protected final String builtInEntity;
    protected final GameStateAccessor gameStateAccessor;
    
    public BuildInActionHandler(CommandChecker commandChecker, GameStateAccessor gameStateAccessor) {
        this.commandChecker = commandChecker;
        this.builtInEntity = this.commandChecker.getEntityPhrase();
        this.gameStateAccessor = gameStateAccessor;

    }
    abstract String executeAction();
}
