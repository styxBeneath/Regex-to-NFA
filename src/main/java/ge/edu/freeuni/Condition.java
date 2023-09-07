package ge.edu.freeuni;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Condition {
    private int id;
    private boolean isAccepting;
    private Map<Character, Set<Condition>> transitions;
    private Set<Condition> epsilonCovered;
    private int currTransition;

    public Condition() {
        this.transitions = new HashMap<>();
        this.epsilonCovered = new HashSet<>();
        this.id = -1;
    }

    public Condition(int id, boolean isAccepting) {
        this.id = id;
        this.isAccepting = isAccepting;
        this.transitions = new HashMap<>();
        this.currTransition = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAccepting() {
        return isAccepting;
    }

    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }

    public Map<Character, Set<Condition>> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<Character, Set<Condition>> transitions) {
        this.transitions = transitions;
    }

    public Set<Condition> getEpsilonCovered() {
        return epsilonCovered;
    }

    public int getCurrTransition() {
        return currTransition;
    }

    public void setCurrTransition(int currTransition) {
        this.currTransition = currTransition;
    }

    public static void addTransition(Condition condition1, Condition condition2, Character c) {
        condition1.getTransitions().computeIfAbsent(c, t -> new HashSet<>()).add(condition2);
    }

    public static void addAllTransitions(Condition condition1, Condition condition2) {
        for (Character symbol : condition2.getTransitions().keySet()) {
            for (Condition condition : condition2.getTransitions().get(symbol)) {
                if (!condition1.getTransitions().computeIfAbsent(symbol, t -> new HashSet<>()).contains(condition)) {
                    condition1.getTransitions().get(symbol).add(condition);
                }
            }
        }
    }
}
