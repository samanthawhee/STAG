package edu.uob;

public class CommandHandler {
    private final GamePlayerCreator playerCreator;
    private final CommandChecker commandChecker;
    private final GameStateAccessor gameStateAccessor;

    public CommandHandler(String command, GameStateAccessor gameStateAccessor) {
        this.gameStateAccessor = gameStateAccessor;
        this.playerCreator = new GamePlayerCreator(command, this.gameStateAccessor);
        this.commandChecker = new CommandChecker(command, this.playerCreator, this.gameStateAccessor);
    }

    public String processCommand() {

        if(!this.playerCreator.checkPlayerNameValid().isEmpty()){
            return this.playerCreator.checkPlayerNameValid();
        }else{
            this.playerCreator.createPlayer();
        }

        if(this.commandChecker.checkCommand().equals("Execute")){
            CommandExecutor commandExecutor = new CommandExecutor(this.commandChecker, this.gameStateAccessor);
            return commandExecutor.executeAction();
        }else{
            return this.commandChecker.checkCommand();
        }
    }
}
