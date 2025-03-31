package edu.uob;


import java.util.HashSet;

public class GamePlayerCreator {
    private String playerName;
    private final String command;
    private final String startLocation;
    private final GameStateAccessor gameStateAccessor;

    public GamePlayerCreator(String command, GameStateAccessor gameStateAccessor) {
        this.command = command;
        this.gameStateAccessor = gameStateAccessor;
        this.startLocation = this.gameStateAccessor.getLocationList().getStartLocation();
    }

    public String createPlayer() {
        if(!this.checkHasPlayerExist()){
            if(this.checkPlayerNameValid().isEmpty()){
                GamePlayer newPlayer = new GamePlayer(this.getPlayerName(), this.startLocation);
                this.gameStateAccessor.getPlayerList().addPlayers(newPlayer);
                newPlayer.setLocation(this.startLocation);
            }else{
                return this.checkPlayerNameValid();
            }
        }
        return "";
    }

    public String getPlayerName(){
        StringBuilder sb = new StringBuilder(this.command);

        int colonIndex = sb.indexOf(":");
        if(colonIndex > 0){
            this.playerName = sb.substring(0, colonIndex).trim();
            return this.playerName;

        }else{
            return " ";
        }
    }

    public String checkPlayerNameValid(){
        if(!this.getPlayerName().matches("[a-zA-Z\\-\\s']+")){
            return "The player name must be letters, space, apostrophes and hyphens.";
        }
        return "";
    }

    private boolean checkHasPlayerExist() {
        HashSet<String>players = this.gameStateAccessor.getPlayerList().getPlayers();
        for(String player : players){
            if(player.equals(this.playerName)){
                return true;
            }
        }
        return false;
    }
}
