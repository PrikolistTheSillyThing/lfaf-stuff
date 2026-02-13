# Grammar thingy  
## Creating Strings and Finite Automaton

The project was created in Java and follows the requirements of the task:

## Task Requirements

1. Create the grammar for the given variant  

```
VN = {S, A, B, C}
VT = {a, b, c}

P = {
    S → bA
    A → b
    A → aB
    B → bC
    C → cA
    A → bA
    B → aB
}
```

2. Generate 5 random strings that respect the production rules.
3. Convert the grammar into a finite automaton.
4. Verify whether a string belongs to the language using the finite automaton.

---

## Main Components

### `public Grammar()`

This constructor initializes the grammar.

It creates:
- A `Map<String, List<String>>` called `productions`, which stores all production rules.
- A start symbol (`S`).
- A `Random` object used for generating random strings.

Each non-terminal (`S`, `A`, `B`, `C`) is mapped to its possible right-hand side productions.

Example:

```
A → b
A → aB
A → bA
```

is stored as:

```java
productions.put("A", Arrays.asList("b", "aB", "bA"));
```

This structure allows dynamic rule selection during string generation.

---

### `generateString()`

This method generates a random string that follows the grammar rules.

How it works:

1. It starts from the start symbol `S`.
2. While the current symbol is a non-terminal:
   - It randomly selects one of its production rules.
   - Extracts the terminal (first character of the rule).
   - Moves to the next non-terminal (if it exists).
3. If the rule contains only one terminal (for example `A → b`), generation stops.

Because the grammar is **right-linear** (each rule is either of the form `A → aB` or `A → a`), the method always appends a terminal and optionally moves to another non-terminal.

The result is a valid string from the language defined by the grammar.

---

### `toFiniteAutomaton()`

This method converts the right-linear grammar into a **Non-Deterministic Finite Automaton (NFA)**.

#### Conversion Logic

- Each non-terminal becomes a state.
- A new final state `F` is added for productions of the form `A → a`.
- For every production:
  - `A → aB` becomes a transition from state `A` to `B` with symbol `a`.
  - `A → a` becomes a transition from state `A` to `F` with symbol `a`.

Example mappings:

```
S → bA     →  δ(S, b) = A
A → b      →  δ(A, b) = F
A → bA     →  δ(A, b) = A
A → aB     →  δ(A, a) = B
B → bC     →  δ(B, b) = C
B → aB     →  δ(B, a) = B
C → cA     →  δ(C, c) = A
```

Since some transitions share the same symbol (for example `A → b` and `A → bA`), the automaton is non-deterministic.

The method constructs:

- Set of states
- Alphabet
- Transition function (stored as a nested `Map`)
- Start state
- Final states

and returns a fully initialized `FiniteAutomaton` object.

---

### `stringBelongToLanguage(String inputString)`

This method checks whether a string is accepted by the finite automaton.

It simulates an NFA as follows:

1. Start with the start state.
2. For each symbol in the input string:
   - Compute all possible next states.
   - Replace the current state set with the new one.
3. If at any step there are no valid transitions, return `false`.
4. After processing the entire string:
   - If at least one of the current states is a final state, return `true`.
   - Otherwise, return `false`.

Because transitions are stored as `Set<String>`, multiple states can be active simultaneously.  
This correctly models non-deterministic behavior.

---

## Overall Logic

- The grammar defines the language.
- `generateString()` produces valid words from the grammar.
- `toFiniteAutomaton()` demonstrates the equivalence between right-linear grammars and finite automata.
- `stringBelongToLanguage()` verifies string membership using formal automaton simulation.

This project demonstrates the theoretical equivalence between **regular grammars** and **finite automata** through practical Java implementation.
