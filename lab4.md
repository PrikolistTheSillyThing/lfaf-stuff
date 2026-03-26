# Regular expressions thingy

---

## Theory

A **regular expression (regex)** is a formal way of describing patterns in strings. It defines a set of valid strings over a given alphabet using a combination of symbols and operators.

Regular expressions are widely used in:

* **Text processing** (search, replace, validation)
* **Compilers and interpreters** (lexical analysis)
* **Data validation** (emails, passwords, formats)
* **Automata theory** (connection with finite automata)

A regex describes a **regular language**, meaning it can be recognized by a **finite automaton**.

---

### Basic Regex Concepts

| Symbol | Meaning                      | Example               |                |
| ------ | ---------------------------- | --------------------- | -------------- |
| `a`    | Literal symbol               | `"a"`                 |                |
| `ab`   | Concatenation                | `"ab"`                |                |
| `a     | b`                           | OR (choice)           | `"a"` or `"b"` |
| `*`    | Repetition (0 or more times) | `"", "a", "aa", ..."` |                |
| `( )`  | Grouping                     | `(a                   | b)`            |

---

### Example

Regex:

```
(a|b)c*
```

Valid strings:

```
a, b, ac, bc, acc, bcc, ...
```

---

## Objectives

1. Understand what regular expressions are.
2. Learn how regex defines valid strings.
3. Implement a generator that produces valid strings from a regex.
4. Dynamically interpret regex (not hardcoded logic).
5. Limit infinite repetitions (`*`) to a maximum of 5.
6. (Bonus) Track and display processing steps.

---

## Implementation

The implementation is written in **Java** and dynamically interprets regular expressions to generate valid strings.

---

### Supported Features

The program supports:

| Feature         | Description               |                  |
| --------------- | ------------------------- | ---------------- |
| Concatenation   | Combining symbols         |                  |
| OR `            | `                         | Multiple choices |
| Grouping `( )`  | Nested expressions        |                  |
| Kleene Star `*` | Repetition (limited to 5) |                  |

---

### Core Idea

Instead of hardcoding patterns, the program:

1. Reads the regex character by character
2. Identifies structures (groups, OR, repetition)
3. Recursively processes sub-expressions
4. Combines results step-by-step

---

### `generate()`

Entry point of the program.

* Clears previous steps
* Starts processing the regex

---

### `process()`

Main function that interprets the regex.

Handles:

* Groups `( ... )`
* Symbols (`a`, `b`, `1`, etc.)
* Repetition `*`

Builds the result step-by-step using combinations.

---

### `handleOr()`

Handles expressions with `|`.

Example:

```
(a|b|c)
```

Steps:

* Split into parts: `a`, `b`, `c`
* Process each separately
* Merge results

---

### `repeat()`

Handles `*` operator.

Instead of infinite repetition:

* Limits to **0–5 repetitions**

Example:

```
a*
→ "", "a", "aa", "aaa", "aaaa", "aaaaa"
```

---

### `combine()`

Combines two lists of strings.

Example:

```
["a", "b"] + ["c", "d"]
→ ["ac", "ad", "bc", "bd"]
```

---

### `findClosing()`

Finds matching closing parenthesis.

Used to correctly extract grouped expressions:

```
(a(b|c)d)
```

---

### Bonus: Step Tracking

The program stores execution steps in:

```
List<String> steps
```

This shows:

* What is being processed
* When OR is handled
* When repetition is applied

---

## Main Program

Input regex examples:

```
(a|b)(c|d)E*G
P(Q|R|S)T(UV|W|X)*Z*
1(0|1)*2(3|4)*36
```

---

### Example Output

Generated strings:

```
PQTZ
PRTWZ
PSTUVZ
...
```

Steps:

```
Processing: P(Q|R|S)T(UV|W|X)*Z*
Handling OR in: Q|R|S
Applying * (0-5 times)
...
```

---

## Overall Logic

The algorithm works as a **recursive interpreter**:

1. Start with an empty result `[""]`
2. Read regex left to right
3. For each element:

   * Symbol → append
   * Group → recursively process
   * OR → branch into multiple paths
   * `*` → expand repetitions (0–5)
4. Combine intermediate results

This mimics how a **finite automaton explores all valid paths**.

---

## Conclusions

This laboratory demonstrated how regular expressions can be **interpreted dynamically** to generate valid strings.

Key takeaways:

* Regular expressions define **sets of valid strings**
* They are equivalent to **finite automata**
* Recursive processing allows handling complex patterns
* Limiting `*` avoids infinite generation
* Step tracking improves understanding of execution flow

The implementation shows how theoretical concepts from **formal languages** can be transformed into a working program that generates valid words from regex definitions.
