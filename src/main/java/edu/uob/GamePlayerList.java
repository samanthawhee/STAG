package edu.uob;

import java.util.HashSet;
import java.util.Iterator;

public class GamePlayerList {
    private final HashSet<GamePlayer> players;

    public GamePlayerList() {
        this.players = new HashSet<>();
    }

    public HashSet<String> getPlayers() {
        HashSet<String> playerSet = new HashSet<>();
        Iterator<GamePlayer> playerIterator = players.iterator();
        while (playerIterator.hasNext()) {
            playerSet.add(playerIterator.next().getName());
        }
        return playerSet;
    }

    public GamePlayer getPlayer(String playerName) {
        for (GamePlayer currentPlayer : this.players) {
            if (currentPlayer.getName().equals(playerName)) {
                return currentPlayer;
            }
        }
        return null;
    }

    public void addPlayers(GamePlayer player) {
        this.players.add(player);
    }
}
