package edu.uob;

import java.util.HashSet;

public class GameAction {
    private final HashSet<String> triggers;
    private final HashSet<String> subjects;
    private final HashSet<String> consumed;
    private final HashSet<String> produced;
    private final String narration;

    public GameAction(String narration) {
        this.triggers = new HashSet<>();
        this.subjects = new HashSet<>();
        this.consumed = new HashSet<>();
        this.produced = new HashSet<>();
        this.narration = narration;

    }

    public HashSet<String> getTriggers() { return this.triggers; }

    public void addTrigger(String trigger) { this.triggers.add(trigger); }

    public boolean hasTrigger(String trigger) { return this.triggers.contains(trigger); }

    public HashSet<String> getObjects() { return this.subjects; }

    public void addSubjects(String object) { this.subjects.add(object); }

    public boolean hasObject(String object) { return this.subjects.contains(object); }

    public HashSet<String> getConsumed() { return this.consumed; }

    public void addConsumed(String consumed) { this.consumed.add(consumed); }

    public boolean hasConsumed(String consumed) { return this.consumed.contains(consumed); }

    public HashSet<String> getProduced() { return this.produced; }

    public void addProduced(String produced) { this.produced.add(produced); }

    public boolean hasProduced(String produced) { return this.produced.contains(produced); }

    public String getNarration() { return this.narration; }

}
