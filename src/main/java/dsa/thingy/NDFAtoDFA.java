package dsa.thingy;

import java.util.*;

public class NDFAtoDFA {

    static Map<String, Map<Character, Set<String>>> ndfa = new HashMap<>();

    static void addTransition(String from, char symbol, String to) {
        ndfa.putIfAbsent(from, new HashMap<>());
        ndfa.get(from).putIfAbsent(symbol, new HashSet<>());
        ndfa.get(from).get(symbol).add(to);
    }

    static void buildNDFA() {
        addTransition("q0", 'a', "q1");
        addTransition("q0", 'a', "q0");

        addTransition("q1", 'b', "q1");
        addTransition("q1", 'a', "q2");

        addTransition("q2", 'b', "q2");
        addTransition("q2", 'a', "q0");
    }

    static void convertToDFA() {
        Set<Character> alphabet = Set.of('a', 'b');

        Map<Set<String>, Map<Character, Set<String>>> dfa = new HashMap<>();
        Queue<Set<String>> queue = new LinkedList<>();

        Set<String> startState = Set.of("q0");
        queue.add(startState);
        dfa.put(startState, new HashMap<>());

        while (!queue.isEmpty()) {
            Set<String> current = queue.poll();
            dfa.putIfAbsent(current, new HashMap<>());

            for (char symbol : alphabet) {
                Set<String> next = new HashSet<>();

                for (String state : current) {
                    if (ndfa.containsKey(state) &&
                            ndfa.get(state).containsKey(symbol)) {
                        next.addAll(ndfa.get(state).get(symbol));
                    }
                }

                if (!next.isEmpty()) {
                    dfa.get(current).put(symbol, next);
                    if (!dfa.containsKey(next)) {
                        queue.add(next);
                    }
                }
            }
        }

        printDFA(dfa);
    }

    static void printDFA(Map<Set<String>, Map<Character, Set<String>>> dfa) {
        System.out.println("DFA Transitions:");
        for (Set<String> state : dfa.keySet()) {
            for (char c : dfa.get(state).keySet()) {
                System.out.println(state + " --" + c + "--> " + dfa.get(state).get(c));
            }
        }
    }

    public static void main(String[] args) {
        buildNDFA();
        convertToDFA();
    }
}
