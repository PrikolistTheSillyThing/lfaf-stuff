# Parser & AST — HTML Lexer/Parser

---

## Theory

A **parser** is a program that analyzes a sequence of tokens produced by a lexer and builds a structured representation of the input according to a formal grammar. Parsing bridges the gap between raw token streams and meaningful program structure.

Parsing is typically divided into two stages:

* **Lexical analysis (lexing)** — breaks the input into tokens (identifiers, operators, literals, etc.)
* **Syntactic analysis (parsing)** — assembles tokens into a hierarchical structure according to grammar rules

The result of parsing is usually an **Abstract Syntax Tree (AST)** — a tree data structure that represents the syntactic structure of the input, abstracting away irrelevant details like whitespace or punctuation.

ASTs are used in:

* **Compilers and interpreters** (source-to-bytecode or source-to-machine-code translation)
* **Static analysis tools** (linters, type checkers)
* **Code formatters and refactoring tools**
* **HTML/XML processors** (browsers build a DOM tree from parsed HTML)

---

### Token Types

| Token | Description |
| ----- | ----------- |
| `TAG_OPEN` | The `<` character, opening a tag |
| `TAG_CLOSE` | The `>` character, closing a tag |
| `TAG_SLASH` | The `/` character inside a tag (closing marker) |
| `TAG_NAME` | The name of a tag (e.g. `div`, `b`) |
| `ATTRIBUTE_NAME` | An attribute identifier inside a tag (e.g. `class`) |
| `EQUALS` | The `=` character separating an attribute name from its value |
| `STRING` | A quoted attribute value (e.g. `"main"`) |
| `TEXT` | Raw text content between tags |

---

### AST Node Structure

Each node in the AST represents one HTML element and stores:

* `tagName` — the element name (e.g. `div`, `b`)
* `attributes` — a map of attribute name → value pairs
* `children` — a list of child nodes (nested elements)
* `text` — optional raw text content directly inside the element

---

## Objectives

1. Get familiar with parsing, what it is and how it can be programmed.
2. Understand the concept of an AST and how it represents syntactic structure.
3. Build on the existing lexer from Lab 3 by:
   1. Ensuring token types are defined via a `TokenType` enum.
   2. Implementing AST data structures suited to the HTML input domain.
   3. Implementing a parser that extracts syntactic structure from the token stream and constructs an AST.

---

## Implementation

The implementation is written in **Java** and consists of three components: the `HtmlLexer`, the `HtmlParser`, and the `Node` AST class.

---

### Supported Features

| Feature | Description |
| ------- | ----------- |
| Tag tokenization | Identifies `<`, `>`, `/`, tag names, attributes, and text nodes |
| Context-aware lexing | Distinguishes attribute names from text using an `insideTag` flag |
| AST construction | Builds a recursive tree of `Node` objects from the token stream |
| Attribute parsing | Extracts key-value attribute pairs into a map per node |
| Recursive descent | Parses nested elements by calling `parseElement()` recursively |

---

### Core Idea

The program is structured as a classic **two-phase pipeline**:

1. The **lexer** scans the raw HTML string character by character, classifying each portion as a typed token.
2. The **parser** consumes the token list sequentially, applying a recursive descent strategy to reconstruct the element hierarchy as an AST.

Each element in the HTML maps to one `Node` in the tree. Children are parsed recursively until a closing tag is detected.

---

### `HtmlLexer`

Converts a raw HTML string into a flat list of `Token` objects.

Key internals:

* `advance()` — moves the cursor forward one character
* `skipWhitespace()` — skips space, tab, and newline characters
* `readWord()` — reads a sequence of alphanumeric characters (used for tag and attribute names)
* `readString()` — reads a quoted string value (consumed past the closing `"`)
* `readText()` — reads everything up to the next `<` as a text node

The `tokenize()` method drives the main loop. It uses a boolean `insideTag` flag to distinguish attribute names (which appear inside `<...>`) from plain text content (which appears outside tags). This prevents text like `Hello` from being misclassified as an attribute.

Example — input `<div class="main">Hello</div>` produces:

```
TAG_OPEN    : <
TAG_NAME    : div
ATTRIBUTE_NAME : class
EQUALS      : =
STRING      : main
TAG_CLOSE   : >
TEXT        : Hello
TAG_OPEN    : <
TAG_SLASH   : /
TAG_NAME    : div
TAG_CLOSE   : >
```

---

### `Token` and `TokenType`

Each token is a simple value object:

```java
class Token {
    TokenType type;
    String value;
}
```

`TokenType` is an enum covering all legal token categories:

```java
enum TokenType {
    TAG_OPEN, TAG_CLOSE, TAG_SLASH,
    TAG_NAME, ATTRIBUTE_NAME, EQUALS,
    STRING, TEXT
}
```

---

### `Node` (AST)

The AST node class represents one HTML element:

```java
class Node {
    String tagName;
    Map<String, String> attributes;
    List<Node> children;
    String text;
}
```

The `print(int indent)` method recursively prints the tree with indentation to visualize nesting depth.

---

### `HtmlParser`

Converts the flat token list into a recursive `Node` tree using a **recursive descent** strategy.

* `parse()` — entry point, calls `parseElement()`
* `parseElement()` — expects and consumes an opening tag, reads attributes, then recursively parses children until a closing tag is found
* `expect(TokenType)` — consumes the next token and throws a `RuntimeException` if the type does not match
* `peekNext()` — looks one token ahead to distinguish an opening tag (`<div>`) from a closing tag (`</div>`) without consuming

The child parsing loop continues as long as the next two tokens are not `TAG_OPEN` followed by `TAG_SLASH`. Once that pattern is detected, the closing tag is consumed and the node is returned.

---

## Main Program

The input used for demonstration:

```
<div class="main">Hello <b>world</b></div>
```

Defined as a string literal in `Main.java`:

```java
String html = "<div class=\"main\">Hello <b>world</b></div>";
HtmlLexer lexer = new HtmlLexer(html);
List<Token> tokens = lexer.tokenize();
HtmlParser parser = new HtmlParser(tokens);
Node root = parser.parse();
root.print(0);
```

---

### Example Output

Token stream printed first:

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

AST printed after:

```
<div> {class=main}
  text: "Hello"
  <b> {}
    text: "world"
```

Each line represents one node in the tree. Indentation reflects element nesting depth.

---

## Overall Logic

The system operates as a **two-pass pipeline**:

1. **Lexing** — the raw string is consumed character by character; each recognized pattern emits a typed token into a flat list.
2. **Parsing** — the token list is consumed left-to-right; recursive calls to `parseElement()` build up the AST as a tree of `Node` objects.

The `insideTag` guard in the lexer is a critical design detail: without it, text content between tags would be misclassified as attribute names, producing incorrect tokens and a malformed parse.

The `peekNext()` lookahead in the parser allows the child loop to distinguish `<b>` (an opening tag, recurse deeper) from `</b>` (a closing tag, stop and return) without consuming the token prematurely.

---

## Conclusions

This laboratory demonstrated how a lexer and a recursive descent parser work together to extract structured information from raw HTML text.

Key takeaways:

* A `TokenType` enum cleanly categorizes all token categories and makes the parser logic explicit and readable
* Lexer context (the `insideTag` flag) is necessary to avoid ambiguity between attribute names and text content
* Recursive descent parsing maps naturally onto the nested structure of HTML, with one function call per element
* The `Node` AST captures the full semantic structure of the input — tag names, attributes, text, and nesting — in a form suitable for further processing
* Lookahead (`peekNext()`) enables the parser to distinguish structurally similar token sequences without backtracking
