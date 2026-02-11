package org.example;

import java.util.*;

public class FiniteAutomaton {

    private final Set<String> states;
    private final Set<Character> alphabet;
    private final Map<String, Map<Character, Set<String>>> transitions;
    private final String startState;
    private final Set<String> finalStates;

    public FiniteAutomaton(Set<String> states,
                           Set<Character> alphabet,
                           Map<String, Map<Character, Set<String>>> transitions,
                           String startState,
                           Set<String> finalStates) {

        this.states = states;
        this.alphabet = alphabet;
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
    }

    public boolean stringBelongToLanguage(final String inputString) {

        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        for (int i = 0; i < inputString.length(); i++) {
            char symbol = inputString.charAt(i);
            Set<String> nextStates = new HashSet<>();

            for (String state : currentStates) {

                if (transitions.containsKey(state) &&
                        transitions.get(state).containsKey(symbol)) {

                    nextStates.addAll(
                            transitions.get(state).get(symbol)
                    );
                }
            }

            currentStates = nextStates;

            if (currentStates.isEmpty()) {
                return false;
            }
        }

        for (String state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }

        return false;
    }
}