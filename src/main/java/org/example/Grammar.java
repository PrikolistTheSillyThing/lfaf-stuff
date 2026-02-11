package org.example;
import java.util.*;

import java.util.*;

public class Grammar {

    private final Map<String, List<String>> productions;
    private final String startSymbol;
    private final Random random;

    public Grammar() {
        productions = new HashMap<>();
        random = new Random();
        startSymbol = "S";

        productions.put("S", Arrays.asList("bA"));

        productions.put("A", Arrays.asList(
                "b",
                "aB",
                "bA"
        ));

        productions.put("B", Arrays.asList(
                "bC",
                "aB"
        ));

        productions.put("C", Arrays.asList(
                "cA"
        ));
    }

    public String generateString() {
        String current = startSymbol;
        String result = "";

        while (true) {

            if (!productions.containsKey(current)) {
                break;
            }

            List<String> rules = productions.get(current);
            String chosenRule = rules.get(random.nextInt(rules.size()));

            if (chosenRule.length() == 1) {
                result += chosenRule;
                break;
            }

            char terminal = chosenRule.charAt(0);
            char nextNonTerminal = chosenRule.charAt(1);

            result += terminal;
            current = String.valueOf(nextNonTerminal);
        }

        return result;
    }

    public FiniteAutomaton toFiniteAutomaton() {

        Set<String> states = new HashSet<>(Arrays.asList("S", "A", "B", "C", "F"));
        Set<Character> alphabet = new HashSet<>(Arrays.asList('a', 'b', 'c'));

        Map<String, Map<Character, Set<String>>> transitions = new HashMap<>();

        for (String state : states) {
            transitions.put(state, new HashMap<>());
        }

        addTransition(transitions, "S", 'b', "A");

        addTransition(transitions, "A", 'b', "F");  // A → b
        addTransition(transitions, "A", 'b', "A");  // A → bA
        addTransition(transitions, "A", 'a', "B");

        addTransition(transitions, "B", 'b', "C");
        addTransition(transitions, "B", 'a', "B");

        addTransition(transitions, "C", 'c', "A");

        Set<String> finalStates = new HashSet<>();
        finalStates.add("F");

        return new FiniteAutomaton(
                states,
                alphabet,
                transitions,
                "S",
                finalStates
        );
    }

    private void addTransition(
            Map<String, Map<Character, Set<String>>> transitions,
            String from,
            char symbol,
            String to) {

        transitions
                .computeIfAbsent(from, k -> new HashMap<>())
                .computeIfAbsent(symbol, k -> new HashSet<>())
                .add(to);
    }

    public static void main(String[] args) {
        Grammar grammar = new Grammar();

        for (int i = 0; i < 5; i++) {
            String generatedString = grammar.generateString();
            System.out.println(generatedString);
        }

        FiniteAutomaton automaton = grammar.toFiniteAutomaton();

        System.out.println("\nTesting strings:");

        String test1 = "bb";
        String test2 = "bab";
        String test3 = "baab";
        String test4 = "bbbb";

        System.out.println(test1 + " -> " + automaton.stringBelongToLanguage(test1));
        System.out.println(test2 + " -> " + automaton.stringBelongToLanguage(test2));
        System.out.println(test3 + " -> " + automaton.stringBelongToLanguage(test3));
        System.out.println(test4 + " -> " + automaton.stringBelongToLanguage(test4));
    }
}