package chomsky.thingy;

import java.util.*;

class Grammar {

    Set<String> nonTerminals = new HashSet<>();
    Set<String> terminals = new HashSet<>();
    Map<String, List<List<String>>> rules = new HashMap<>();
    String start;

    void addRule(String left, List<String> right) {
        rules.putIfAbsent(left, new ArrayList<>());
        rules.get(left).add(right);
    }

    void convertToCNF() {
        removeEpsilon();
        removeUnit();
        convertTerminals();
        breakLong();
    }

    void removeEpsilon() {
        Set<String> nullable = new HashSet<>();

        boolean changed;
        do {
            changed = false;
            for (String A : rules.keySet()) {
                for (List<String> r : rules.get(A)) {
                    if (r.isEmpty() || nullable.containsAll(r)) {
                        if (!nullable.contains(A)) {
                            nullable.add(A);
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);

        Map<String, List<List<String>>> newRules = new HashMap<>();

        for (String A : rules.keySet()) {
            newRules.putIfAbsent(A, new ArrayList<>());

            for (List<String> r : rules.get(A)) {
                List<Integer> pos = new ArrayList<>();

                for (int i = 0; i < r.size(); i++) {
                    if (nullable.contains(r.get(i))) pos.add(i);
                }

                int subsets = 1 << pos.size();

                for (int mask = 0; mask < subsets; mask++) {
                    List<String> newR = new ArrayList<>(r);

                    for (int j = pos.size() - 1; j >= 0; j--) {
                        if ((mask & (1 << j)) != 0) {
                            newR.remove((int) pos.get(j));
                        }
                    }

                    if (!newR.isEmpty()) {
                        newRules.get(A).add(newR);
                    }
                }
            }
        }

        rules = newRules;
    }

    void removeUnit() {
        Map<String, List<List<String>>> newRules = new HashMap<>();

        for (String A : rules.keySet()) {
            newRules.put(A, new ArrayList<>());
        }

        for (String A : rules.keySet()) {
            Set<String> reachable = new HashSet<>();
            Queue<String> q = new LinkedList<>();

            q.add(A);

            while (!q.isEmpty()) {
                String cur = q.poll();

                for (List<String> r : rules.get(cur)) {
                    if (r.size() == 1 && nonTerminals.contains(r.get(0))) {
                        q.add(r.get(0));
                    } else {
                        newRules.get(A).add(r);
                    }
                }
            }
        }

        rules = newRules;
    }

    void convertTerminals() {
        Map<String, String> map = new HashMap<>();
        int id = 0;

        for (String A : rules.keySet()) {
            for (List<String> r : rules.get(A)) {
                if (r.size() >= 2) {
                    for (int i = 0; i < r.size(); i++) {
                        String s = r.get(i);

                        if (terminals.contains(s)) {
                            if (!map.containsKey(s)) {
                                String newVar = "T" + id++;
                                map.put(s, newVar);
                                nonTerminals.add(newVar);

                                rules.putIfAbsent(newVar, new ArrayList<>());
                                rules.get(newVar).add(Arrays.asList(s));
                            }

                            r.set(i, map.get(s));
                        }
                    }
                }
            }
        }
    }

    void breakLong() {
        Map<String, List<List<String>>> newRules = new HashMap<>();
        int id = 0;

        for (String A : rules.keySet()) {
            newRules.putIfAbsent(A, new ArrayList<>());

            for (List<String> r : rules.get(A)) {
                if (r.size() <= 2) {
                    newRules.get(A).add(r);
                } else {
                    String prev = A;

                    for (int i = 0; i < r.size() - 2; i++) {
                        String newVar = "X" + id++;
                        nonTerminals.add(newVar);

                        newRules.putIfAbsent(prev, new ArrayList<>());
                        newRules.get(prev).add(Arrays.asList(r.get(i), newVar));

                        prev = newVar;
                    }

                    newRules.putIfAbsent(prev, new ArrayList<>());
                    newRules.get(prev).add(
                            Arrays.asList(r.get(r.size() - 2), r.get(r.size() - 1))
                    );
                }
            }
        }

        rules = newRules;
    }

    static void main(String[] args) {
        Grammar g = new Grammar();

        g.start = "S";

        g.nonTerminals.addAll(Arrays.asList("S", "A", "B"));
        g.terminals.addAll(Arrays.asList("a", "b"));

        g.addRule("S", Arrays.asList("A", "B"));
        g.addRule("S", Arrays.asList("b"));
        g.addRule("A", Arrays.asList("a"));
        g.addRule("A", new ArrayList<>()); // epsilon
        g.addRule("B", Arrays.asList("b"));

        g.convertToCNF();

        for (String A : g.rules.keySet()) {
            for (List<String> r : g.rules.get(A)) {
                System.out.println(A + " -> " + r);
            }
        }
    }
}


