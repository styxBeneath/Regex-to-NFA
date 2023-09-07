package ge.edu.freeuni;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class build {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String regex = scanner.nextLine();

        NFA nfa = NFA.generateNFA(regex);
        List<Condition> nfaAsList = NFA.clearEpsilons(nfa);

        printFirstTwoLines(nfaAsList);
        printOtherLines(nfaAsList);

        scanner.close();
    }

    private static void printOtherLines(List<Condition> nfaAsList) {
        for (Condition condition1 : nfaAsList) {
            int transSize = 0;
            StringBuilder pairs = new StringBuilder();
            for (Character symbol : condition1.getTransitions().keySet()) {
                for (Condition condition2 : condition1.getTransitions().get(symbol)) {
                    transSize++;
                    pairs.append(symbol);
                    pairs.append(" ");
                    pairs.append(condition2.getId());
                    pairs.append(" ");
                }
            }
            System.out.println(transSize + " " + pairs);
        }
    }

    private static void printFirstTwoLines(List<Condition> nfaAsList) {
        int numConditions = nfaAsList.size();
        int numAccepting = 0;
        int numTransitions = 0;
        List<Integer> acceptingIds = new ArrayList<>();

        for (int id = 0; id < nfaAsList.size(); id++) {
            if (nfaAsList.get(id).isAccepting()) {
                numAccepting++;
                acceptingIds.add(id);
            }
            for (Character c : nfaAsList.get(id).getTransitions().keySet()) {
                numTransitions += nfaAsList.get(id).getTransitions().get(c).size();
            }
        }

        System.out.println(numConditions + " " + numAccepting + " " + numTransitions);
        for (Integer id : acceptingIds) {
            System.out.print(id + " ");
        }
        System.out.println();
    }
}
