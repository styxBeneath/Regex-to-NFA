package ge.edu.freeuni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class run {
    public static void main(String[] args) {
        List<Condition> conditions = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        String word = scanner.nextLine();
        int conditionsNumber = scanner.nextInt();
        int acceptingNumber = scanner.nextInt();
        int transitionsNumber = scanner.nextInt();

        for (int i = 0; i < conditionsNumber; i++) {
            conditions.add(new Condition(i, false));
        }
        for (int i = 0; i < acceptingNumber; i++) {
            int accepting = scanner.nextInt();
            conditions.get(accepting).setAccepting(true);
        }

        for (int i = 0; i < conditionsNumber; i++) {
            int numOfTransitions = scanner.nextInt();
            for (int j = 0; j < numOfTransitions; j++) {
                Character symbol = scanner.next().charAt(0);
                int index = scanner.nextInt();
                Condition.addTransition(conditions.get(i), conditions.get(index), symbol);
            }
        }
        System.out.println(generateOutput(word, conditions.get(0)));
        scanner.close();
    }

    private static String generateOutput(String word, Condition start) {
        char[] result = new char[word.length()];
        Arrays.fill(result, 'N');

        Queue<Condition> queue = new LinkedList<>();
        start.setCurrTransition(0);
        queue.add(start);
        while (!queue.isEmpty()) {
            Condition condition = queue.remove();
            Character currSymbol = word.charAt(condition.getCurrTransition());
            if (!condition.getTransitions().containsKey(currSymbol)) {
                continue;
            }
            Set<Condition> nextSteps = condition.getTransitions().get(currSymbol);
            for (Condition nextCondition : nextSteps) {
                int transition = condition.getCurrTransition();
                nextCondition.setCurrTransition(condition.getCurrTransition() + 1);
                if (nextCondition.isAccepting()) {
                    result[transition] = 'Y';
                }
                queue.add(nextCondition);
            }
        }
        return new String(result);
    }
}
