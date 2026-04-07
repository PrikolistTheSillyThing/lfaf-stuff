# Chomsky Normal Form thingy

---

## Theory

**Chomsky Normal Form (CNF)** is a standardized way of writing context-free grammars (CFGs). A grammar is in CNF if every production rule follows one of two forms:

* `A → BC` — a non-terminal produces exactly two non-terminals
* `A → a` — a non-terminal produces exactly one terminal

CNF is widely used in:

* **Parsing algorithms** (CYK algorithm requires CNF input)
* **Compiler design** (grammar normalization for efficient parsing)
* **Formal language theory** (proof techniques and grammar transformations)
* **Automata theory** (equivalence of CFGs and pushdown automata)

Any context-free grammar can be converted to CNF without changing the language it generates (excluding ε, unless the original grammar generates it).

---

### Conversion Steps

| Step | Name | Description |
| ---- | ---- | ----------- |
| 1 | Eliminate ε-productions | Remove rules that produce the empty string |
| 2 | Eliminate unit productions | Remove rules of the form `A → B` |
| 3 | Convert terminals in long rules | Replace terminals in mixed rules with new non-terminals |
| 4 | Break long productions | Split rules with 3+ symbols into binary rules |

---

### Example

Original grammar:

```
S → AB | b
A → a | ε
B → b
```

After CNF conversion:

```
S  → TB | b | B
T0 → a
B  → b
```

---

## Objectives

1. Understand the structure of context-free grammars.
2. Learn the four-step process of converting a CFG to CNF.
3. Implement each conversion step as a separate method.
4. Handle edge cases such as nullable symbols and cascading unit productions.
5. Verify the output grammar satisfies CNF constraints.

---

## Implementation

The implementation is written in **Java** and transforms a given context-free grammar into Chomsky Normal Form through four sequential passes.

---

### Supported Features

| Feature | Description |
| ------- | ----------- |
| ε-elimination | Detects all nullable non-terminals and expands their occurrences |
| Unit production removal | Resolves chains of single non-terminal rules |
| Terminal wrapping | Introduces helper non-terminals (`T0`, `T1`, ...) for terminals in long rules |
| Binary rule splitting | Introduces helper non-terminals (`X0`, `X1`, ...) to split rules longer than 2 symbols |

---

### Core Idea

Instead of transforming the grammar all at once, the program applies each normalization step in order:

1. Parse the grammar as a map from non-terminals to lists of productions
2. Detect and eliminate ε-productions using subset expansion
3. Eliminate unit productions by tracing reachable non-terminals
4. Wrap bare terminals in rules of length ≥ 2 with new non-terminals
5. Break any rule with 3+ symbols into a chain of binary rules

---

### `convertToCNF()`

Entry point for the full conversion pipeline.

* Calls all four steps in order
* Each step modifies the shared `rules` map in place

---

### `removeEpsilon()`

Eliminates ε-productions (rules that derive the empty string).

Handles:

* Detecting all **nullable** non-terminals via fixed-point iteration
* Expanding every production by considering all subsets of nullable positions
* Removing empty productions from the result

Example:

```
A → ε       →    A is nullable
S → AB      →    S → AB | A | B
```

---

### `removeUnit()`

Removes unit productions of the form `A → B`.

Steps:

* For each non-terminal `A`, perform a BFS over unit-reachable non-terminals
* Collect all non-unit productions from reachable non-terminals into `A`'s rule set
* Discard the unit rules themselves

Example:

```
A → B
B → a | b
→  A → a | b
```

---

### `convertTerminals()`

Wraps terminals that appear in productions of length ≥ 2 with dedicated non-terminals.

Example:

```
S → aB
→  T0 → a
   S  → T0 B
```

* Reuses the same wrapper non-terminal if the terminal was already seen
* Only applies to rules with 2 or more symbols (single-terminal rules are already in CNF)

---

### `breakLong()`

Splits productions with 3 or more symbols into a chain of binary rules.

Example:

```
S → A B C D
→  S  → A X0
   X0 → B X1
   X1 → C D
```

* Introduces fresh non-terminals `X0`, `X1`, ... for each split
* Preserves productions of length ≤ 2 unchanged

---

## Main Program

The grammar used as input:

```
S → AB | b
A → a | ε
B → b
```

Defined programmatically as:

```java
g.addRule("S", Arrays.asList("A", "B"));
g.addRule("S", Arrays.asList("b"));
g.addRule("A", Arrays.asList("a"));
g.addRule("A", new ArrayList<>());  // epsilon
g.addRule("B", Arrays.asList("b"));
```

---

### Example Output

After conversion, printed rules:

```
S -> [B]
S -> [A, B]
S -> [b]
A -> [a]
B -> [b]
```

Each line represents one production in the normalized grammar. All rules now conform to CNF.

---

## Overall Logic

The algorithm works as a **sequential rewriting pipeline**:

1. Start with an arbitrary CFG represented as a rule map
2. **Pass 1** — propagate nullability and expand ε-affected rules
3. **Pass 2** — resolve unit chains via BFS reachability
4. **Pass 3** — introduce terminal proxies for mixed/long rules
5. **Pass 4** — binarize all remaining long rules with fresh non-terminals

Each pass operates on the output of the previous one. The result is a grammar equivalent to the original (for non-empty strings) where every rule is either `A → BC` or `A → a`.

---

## Conclusions

This laboratory demonstrated how any context-free grammar can be mechanically transformed into Chomsky Normal Form.

Key takeaways:

* CNF simplifies parsing by restricting rule shapes to exactly two forms
* ε-elimination requires careful subset enumeration to preserve the language
* Unit production removal can be solved efficiently using BFS reachability
* Terminal wrapping and rule binarization are straightforward structural rewrites
* The order of the four steps matters — each step assumes the previous one is complete

The implementation shows how formal grammar theory translates directly into a working program, and how systematic transformation preserves language equivalence across structural changes.
