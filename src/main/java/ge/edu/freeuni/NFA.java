package ge.edu.freeuni;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class NFA {
    public static final Character E = 'E';
    private final Condition start;
    private final Condition end;

    public NFA(Condition start, Condition end) {
        this.start = start;
        this.end = end;
    }

    public Condition getStart() {
        return start;
    }

    public Condition getEnd() {
        return end;
    }

    public static NFA getSymbolNFA(Character c) {
        Condition start = new Condition();
        Condition end = new Condition();
        Condition.addTransition(start, end, c);
        return new NFA(start, end);
    }

    public static NFA getConcatenationNFA(NFA nfa1, NFA nfa2) {
        nfa1.getEnd().setTransitions(nfa2.getStart().getTransitions());
        return new NFA(nfa1.getStart(), nfa2.getEnd());
    }

    public static NFA getUnionNFA(NFA nfa1, NFA nfa2) {
        Condition newStart = new Condition();
        Condition.addTransition(newStart, nfa1.getStart(), E);
        Condition.addTransition(newStart, nfa2.getStart(), E);

        Condition newEnd = new Condition();
        Condition.addTransition(nfa1.getEnd(), newEnd, E);
        Condition.addTransition(nfa2.getEnd(), newEnd, E);

        return new NFA(newStart, newEnd);
    }

    public static NFA getStarNFA(NFA nfa) {
        Condition newStart = new Condition();
        Condition newEnd = new Condition();
        Condition.addTransition(newStart, nfa.getStart(), E);
        Condition.addTransition(newStart, newEnd, E);
        Condition.addTransition(nfa.getEnd(), newEnd, E);
        Condition.addTransition(nfa.getEnd(), nfa.getStart(), E);
        return new NFA(newStart, newEnd);
    }

    public static NFA generateNFA(String regex) {
        String postFix = producePostFix(addConcatSymbols(regex));
        Stack<NFA> nfaStack = new Stack<>();

        for (int i = 0; i < postFix.length(); i++) {
            char c = postFix.charAt(i);
            if (isSymbol(c) || c == E) {
                NFA cur = getSymbolNFA(c);
                nfaStack.push(cur);
            } else if (c == '|') {
                NFA nfa2 = nfaStack.peek();
                nfaStack.pop();
                NFA nfa1 = nfaStack.peek();
                nfaStack.pop();
                nfaStack.push(getUnionNFA(nfa1, nfa2));
            } else if (c == '.') {
                NFA nfa2 = nfaStack.peek();
                nfaStack.pop();
                NFA nfa1 = nfaStack.peek();
                nfaStack.pop();
                nfaStack.push(getConcatenationNFA(nfa1, nfa2));
            } else if (c == '*') {
                NFA nfa1 = nfaStack.peek();
                nfaStack.pop();
                nfaStack.push(getStarNFA(nfa1));
            }
        }

        NFA finalNFA = nfaStack.peek();
        nfaStack.pop();
        finalNFA.getEnd().setAccepting(true);
        return finalNFA;
    }

    public static boolean isSymbol(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    public static boolean isOperator(char c) {
        return c == '.' || c == '|' || c == '*';
    }

    public static int getPriority(char op) {
        switch (op) {
            case '*':
                return 3;
            case '.':
                return 2;
            case '|':
                return 1;
            default:
                return 0;
        }
    }

    public static String addConcatSymbols(String regex) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < regex.length() - 1; i++) {
            char cur = regex.charAt(i);
            char next = regex.charAt(i + 1);

            if (cur == '(' && next == ')') {
                res.append(E);
                i++;
                if (i == regex.length() - 1) {
                    return res.toString();
                }
                continue;
            }

            res.append(cur);
            if (isSymbol(next) || next == '(') {
                if (isSymbol(cur) || cur == '*' || cur == ')') {
                    res.append('.');
                }
            }

        }

        res.append(regex.charAt(regex.length() - 1));
        return res.toString();
    }

    public static String producePostFix(String regex) {
        StringBuilder result = new StringBuilder();
        Stack<Character> opStack = new Stack<>();

        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (isSymbol(c) || c == E) {
                result.append(c);
            } else if (c == '(') {
                opStack.push(c);
            } else if (c == ')') {
                while (opStack.peek() != '(') {
                    result.append(opStack.peek());
                    opStack.pop();
                }
                opStack.pop();
            } else if (isOperator(c)) {
                while (!opStack.empty() && isOperator(opStack.peek()) &&
                        (getPriority(opStack.peek()) >= getPriority(c))) {
                    result.append(opStack.peek());
                    opStack.pop();
                }
                opStack.push(c);
            }
        }

        while (!opStack.empty()) {
            result.append(opStack.peek());
            opStack.pop();
        }

        return result.toString();
    }

    public static List<Condition> clearEpsilons(NFA nfa) {
        List<Condition> result = new ArrayList<>();
        Queue<Condition> queue = new LinkedList<>();
        int id = 0;
        nfa.getStart().setId(id++);
        queue.add(nfa.getStart());
        while (!queue.isEmpty()) {
            Condition condition1 = queue.remove();
            while (condition1.getTransitions().containsKey(E)) {
                Set<Condition> epsilonSet = condition1.getTransitions().remove(E);
                for (Condition condition2 : epsilonSet) {
                    if (!condition1.getEpsilonCovered().contains(condition2)) {
                        if (condition2.isAccepting()) {
                            condition1.setAccepting(true);
                        }
                        Condition.addAllTransitions(condition1, condition2);
                        condition1.getEpsilonCovered().add(condition2);
                    }
                }
            }
            result.add(condition1);
            for (Character c : condition1.getTransitions().keySet()) {
                for (Condition condition : condition1.getTransitions().get(c)) {
                    if (condition.getId() == -1) {
                        condition.setId(id++);
                        queue.add(condition);
                    }
                }
            }
        }
        return result;
    }

}
