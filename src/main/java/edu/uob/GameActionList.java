package edu.uob;

import java.util.HashSet;
import java.util.Iterator;

public class GameActionList {
    private final HashSet<String> actionTriggerPhrases;
    private final HashSet<GameAction> gameActions;

    public GameActionList() {
        this.gameActions = new HashSet<>();
        this.actionTriggerPhrases = new HashSet<>();
    }

    public GameAction getGameAction(String keyPhrase) {
        Iterator<GameAction> actionIterator = this.gameActions.iterator();
        while (actionIterator.hasNext()) {
            GameAction action = actionIterator.next();
            HashSet<String> keyPhraseSet = action.getTriggers();
            if (keyPhraseSet.contains(keyPhrase)) {
                return action;
            }
        }
        return null;
    }

    public HashSet<GameAction> getGameActions() {
        return this.gameActions;
    }

    public void addGameAction(GameAction gameAction) {
        this.gameActions.add(gameAction);
    }

    public void addKeyPhrase(String keyPhrase) {
        this.actionTriggerPhrases.add(keyPhrase);
    }

    public HashSet<String> getActionTriggerPhrases() {
        return this.actionTriggerPhrases;
    }

    public HashSet<String> getSubjects(String triggerPhrase) {
        for(GameAction currentAction : this.getGameActions()){

            for (String currentTrigger : currentAction.getTriggers()) {
                if (currentTrigger.equalsIgnoreCase(triggerPhrase)) {
                    return currentAction.getObjects();
                }
            }
        }
        return null;
    }

}
