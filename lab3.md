# Lexer / Scanner thingy

---

## Theory

Lexical analysis is the first stage of a compiler or interpreter pipeline. It takes a raw sequence of characters and breaks it down into meaningful units called **tokens**. The component responsible for this is called a **lexer**, also known as a tokenizer or scanner.

A **lexeme** is the raw substring extracted from the input (e.g. `class`, `"main"`, `<`). A **token** wraps a lexeme and assigns it a category — a type that the rest of the pipeline can reason about (e.g. `ATTRIBUTE_NAME`, `STRING`, `TAG_OPEN`). The lexer is essentially a stream of such tokens, produced by scanning the input from left to right and applying pattern-matching rules.

In this lab, lexical analysis is applied to **HTML** — a structured markup language made up of tags, attributes, string values, and text content. HTML is a good fit for demonstrating a hand-written lexer because its syntax is simple enough to tokenize with straightforward character-by-character rules, yet rich enough to produce a meaningful variety of token types.

---

## Objectives

1. Understand what lexical analysis is.
2. Get familiar with the inner workings of a lexer/scanner/tokenizer.
3. Implement a sample lexer and demonstrate how it works.

---

## Implementation

### Token Types

The lexer recognizes the following token categories, defined in the `TokenType` enum:

| Token Type       | Description                            | Example       |
|------------------|----------------------------------------|---------------|
| `TAG_OPEN`       | Opening angle bracket                  | `<`           |
| `TAG_CLOSE`      | Closing angle bracket                  | `>`           |
| `TAG_SLASH`      | Slash inside a closing tag             | `/`           |
| `TAG_NAME`       | Name of an HTML tag                    | `div`, `b`    |
| `ATTRIBUTE_NAME` | Name of an HTML attribute              | `class`       |
| `EQUALS`         | Assignment operator in attribute pairs | `=`           |
| `STRING`         | Quoted attribute value                 | `"main"`      |
| `TEXT`           | Raw text content between tags          | `Hello`       |

Each token is represented by the `Token` class, which stores a `TokenType` and a `String` value, and overrides `toString()` for readable output.

---

### `HtmlLexer`

The `HtmlLexer` class performs the actual scanning. It holds:
- `input` — the full HTML string.
- `pos` — the current position in the string.
- `current` — the character at `pos`, used as a one-character lookahead.

#### `advance()`

Moves the cursor one step forward. If the end of the input is reached, `current` is set to `'\0'` as a sentinel value signaling end-of-input.

#### `skipWhitespace()`

Consumes all whitespace characters at the current position. Called implicitly by the main loop when `current` is a whitespace character.

#### `readWord()`

Reads a sequence of alphanumeric characters and returns them as a string. Used to extract tag names and attribute names.

#### `readString()`

Reads a double-quoted string value. It advances past the opening `"`, collects characters until the closing `"` or end-of-input, then advances past the closing `"`. Returns the inner content without quotes.

#### `readText()`

Reads raw text content between tags — everything up to the next `<` or end-of-input. The result is trimmed of surrounding whitespace before being returned.

---

### `tokenize()`

This is the main scanning loop. It runs until `current == '\0'` and dispatches to the appropriate helper or emits a token inline, based on the current character:

- **`<`** — emits `TAG_OPEN`, then checks if the next character is `/` (emits `TAG_SLASH` if so), then reads and emits the `TAG_NAME`.
- **`>`** — emits `TAG_CLOSE`.
- **`=`** — emits `EQUALS`.
- **`"`** — calls `readString()` and emits a `STRING` token.
- **Letter** — calls `readWord()` and emits an `ATTRIBUTE_NAME` token.
- **Non-whitespace, non-`<`** — calls `readText()` and emits a `TEXT` token if the result is non-empty.
- **Whitespace** — skipped via `advance()`.

---

### `Main`

The entry point constructs a sample HTML string and passes it to `HtmlLexer`. The tokenized output is printed to stdout, one token per line.

Input:
```
<div class="main">Hello <b>world</b></div>
```

Output:
```
TAG_OPEN : <
TAG_NAME : div
ATTRIBUTE_NAME : class
EQUALS : =
STRING : main
TAG_CLOSE : >
TEXT : Hello
TAG_OPEN : <
TAG_NAME : b
TAG_CLOSE : >
TEXT : world
TAG_OPEN : <
TAG_SLASH : /
TAG_NAME : b
TAG_CLOSE : >
TAG_OPEN : <
TAG_SLASH : /
TAG_NAME : div
TAG_CLOSE : >
```

---

## Overall Logic

The lexer operates as a **single-pass, character-driven state machine**:

1. A single character of lookahead (`current`) determines what rule to apply next.
2. Specialized reader methods consume multi-character sequences (words, strings, text blocks) without backtracking.
3. The main loop dispatches based on the value of `current`, keeping each branch simple and self-contained.
4. No regular expressions or external libraries are used — the entire tokenization is built from primitive character operations.

This design maps directly onto the theoretical model of a lexer: the input alphabet is the set of Unicode characters, each reader method corresponds to a pattern (or a small NFA), and the dispatch table in `tokenize()` mirrors the role of the DFA that drives real-world lexers.

---

## Conclusions

This laboratory demonstrated the practical construction of a hand-written lexer for a subset of HTML. The implementation confirmed several key ideas from formal language theory:

- Lexical analysis cleanly separates the concern of recognizing tokens from higher-level parsing concerns.
- A simple one-character lookahead is sufficient to tokenize a regular language without ambiguity.
- The distinction between lexemes (raw substrings) and tokens (typed, categorized units) is fundamental to how compilers and interpreters process source text.
- Right-linear structure — established in Lab 1 through regular grammars and finite automata — directly underlies the operation of a lexer: each token type corresponds to a regular language, and the scanner as a whole is a union of those recognizers.

The HTML lexer, while minimal, produces a well-structured token stream that could serve as input to a recursive-descent parser, illustrating how the stages of a language front-end compose naturally.
