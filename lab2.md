# Finite Automaton Thingy  
## Grammar Conversion and Determinism Analysis

The project was created in Java and follows the requirements of the task.  
No external libraries or frameworks were used for automaton handling, all logic was implemented manually.


## Task Requirements

1. Convert the given finite automaton into a regular grammar  
2. Determine whether the finite automaton is deterministic or non-deterministic  
3. Implement functionality to convert a Non-Deterministic Finite Automaton (NDFA) into a Deterministic Finite Automaton (DFA)


## Given Finite Automaton

The finite automaton is defined as:

```
Q = { q0, q1, q2 }
Σ = { a, b }
F = { q2 }

δ(q0, a) = q1
δ(q0, a) = q0
δ(q1, b) = q1
δ(q1, a) = q2
δ(q2, b) = q2
δ(q2, a) = q0
```


## Part A: Conversion to a Regular Grammar

### Conversion Rules

To convert a finite automaton into a **right-linear regular grammar**, the following rules are applied:

- Each state becomes a non-terminal symbol
- Each transition  
  `δ(qi, x) = qj`  
  becomes a production rule  
  `qi → x qj`
- Each final state `qf` receives an additional production  
  `qf → ε`


### Resulting Grammar

Applying the conversion rules to the given automaton produces the following grammar:

```
q0 → a q1 | a q0
q1 → b q1 | a q2
q2 → b q2 | a q0 | ε
```


This grammar is **right-linear**, meaning it is a regular grammar and is equivalent to the given finite automaton.

## Part B: Determinism Analysis

A finite automaton is **deterministic** if for every state and every input symbol there is exactly one transition.

In the given automaton, the following transitions exist:

```
δ(q0, a) = q1
δ(q0, a) = q0
```


Since there are multiple transitions for the same state and input symbol, the automaton does not satisfy the determinism condition.

### Conclusion

The given finite automaton is **Non-Deterministic (NDFA)**.

---

## Part C: NDFA to DFA Conversion

### Core Idea

The conversion from NDFA to DFA is done using the **subset construction algorithm**.

Key ideas:

- Each DFA state represents a **set of NDFA states**
- The DFA start state is the set containing the NDFA start state
- For each DFA state and each input symbol:
  - All possible NDFA transitions are followed
  - The union of the resulting states forms a new DFA state
- A DFA state is accepting if it contains at least one NDFA final state

## Main Components

### NDFA Representation

The NDFA is stored using nested maps:

```
Map<String, Map<Character, Set<String>>>
```

- The outer map represents states
- The inner map represents transitions for each input symbol
- ```Set<String>``` allows multiple possible next states, modeling non-determinism

### NDFA to DFA Conversion Logic

The conversion process works like this:

1. Initialize the DFA start state as ```{q0}```
2. Add it to a queue of unprocessed DFA states
3. While the queue is not empty:
- Remove one DFA state from the queue
- For each symbol in the alphabet:
  - Compute all reachable NDFA states
  - Merge them into a new DFA state
  - If the state has not been seen before, add it to the queue
4. Stop when no new DFA states are discovered

<img width="2382" height="1528" alt="nfa to dfa" src="https://github.com/user-attachments/assets/748c3555-3e0d-4e06-aabd-360a4c9dce10" />

### DFA Final States
A DFA state is considered final if it contains the NDFA final state ```q2```.

```
{q2}        → accepting
{q0, q2}    → accepting
{q1}        → not accepting
```

## Overall Logic

- The finite automaton is converted into a regular grammar using standard FA-to-grammar rules
- Non-determinism is identified by detecting multiple transitions for the same input
- The subset construction algorithm is used to eliminate non-determinism
- The resulting DFA is fully deterministic
- The project demonstrates the equivalence between:
  - Finite automata and regular grammars
  - Non-deterministic and deterministic finite automata
