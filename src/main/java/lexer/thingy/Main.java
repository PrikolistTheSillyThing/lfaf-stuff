package lexer.thingy;

import java.util.*;

enum TokenType {
    TAG_OPEN,
    TAG_CLOSE,
    TAG_SLASH,
    TAG_NAME,
    ATTRIBUTE_NAME,
    EQUALS,
    STRING,
    TEXT
}

class Token {

    TokenType type;
    String value;

    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return type + " : " + value;
    }
}


class HtmlLexer {

    private String input;
    private int pos;
    private char current;

    HtmlLexer(String input) {
        this.input = input;
        pos = 0;
        current = input.length() > 0 ? input.charAt(0) : '\0';
    }

    private void advance() {
        pos++;
        if (pos >= input.length())
            current = '\0';
        else
            current = input.charAt(pos);
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(current))
            advance();
    }

    private String readWord() {
        StringBuilder sb = new StringBuilder();

        while (Character.isLetterOrDigit(current)) {
            sb.append(current);
            advance();
        }

        return sb.toString();
    }

    private String readString() {
        StringBuilder sb = new StringBuilder();
        advance();

        while (current != '"' && current != '\0') {
            sb.append(current);
            advance();
        }

        advance();
        return sb.toString();
    }

    private String readText() {
        StringBuilder sb = new StringBuilder();

        while (current != '<' && current != '\0') {
            sb.append(current);
            advance();
        }

        return sb.toString().trim();
    }

    List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        boolean insideTag = false;   // <-- track context

        while (current != '\0') {

            if (current == '<') {
                tokens.add(new Token(TokenType.TAG_OPEN, "<"));
                advance();
                insideTag = true;    // <-- now inside

                if (current == '/') {
                    tokens.add(new Token(TokenType.TAG_SLASH, "/"));
                    advance();
                }

                String name = readWord();
                tokens.add(new Token(TokenType.TAG_NAME, name));
            }

            else if (current == '>') {
                tokens.add(new Token(TokenType.TAG_CLOSE, ">"));
                advance();
                insideTag = false;   // <-- now outside
            }

            else if (current == '=') {
                tokens.add(new Token(TokenType.EQUALS, "="));
                advance();
            }

            else if (current == '"') {
                tokens.add(new Token(TokenType.STRING, readString()));
            }

            else if (insideTag && Character.isLetter(current)) {   // <-- guard added
                String attr = readWord();
                tokens.add(new Token(TokenType.ATTRIBUTE_NAME, attr));
            }

            else if (!Character.isWhitespace(current)) {
                String text = readText();
                if (!text.isEmpty())
                    tokens.add(new Token(TokenType.TEXT, text));
            }

            else {
                advance();
            }
        }

        return tokens;
    }
}

class Node {
    String tagName;
    Map<String, String> attributes = new HashMap<>();
    List<Node> children = new ArrayList<>();
    String text;

    Node(String tagName) {
        this.tagName = tagName;
    }

    void print(int indent) {
        String pad = "  ".repeat(indent);
        System.out.println(pad + "<" + tagName + "> " + attributes);
        if (text != null)
            System.out.println(pad + "  text: \"" + text + "\"");
        for (Node child : children)
            child.print(indent + 1);
    }
}

class HtmlParser {

    private List<Token> tokens;
    private int pos;

    HtmlParser(List<Token> tokens) {
        this.tokens = tokens;
        pos = 0;
    }

    private Token current() {
        if (pos >= tokens.size())
            return null;
        return tokens.get(pos);
    }

    private void advance() {
        pos++;
    }


    Node parse() {
        return parseElement();
    }

    private Node parseElement() {

        expect(TokenType.TAG_OPEN);

        String tagName = expect(TokenType.TAG_NAME).value;

        Node node = new Node(tagName);

        while (current().type == TokenType.ATTRIBUTE_NAME) {
            String attrName = expect(TokenType.ATTRIBUTE_NAME).value;
            expect(TokenType.EQUALS);
            String attrValue = expect(TokenType.STRING).value;
            node.attributes.put(attrName, attrValue);
        }

        expect(TokenType.TAG_CLOSE);

        while (current() != null && !(current().type == TokenType.TAG_OPEN &&
                peekNext().type == TokenType.TAG_SLASH)) {

            if (current().type == TokenType.TEXT) {
                node.text = expect(TokenType.TEXT).value;
            } else {
                node.children.add(parseElement());
            }
        }

        expect(TokenType.TAG_OPEN);
        expect(TokenType.TAG_SLASH);
        expect(TokenType.TAG_NAME);
        expect(TokenType.TAG_CLOSE);

        return node;
    }
    private Token expect(TokenType type) {
        Token t = current();

        if (t == null || t.type != type)
            throw new RuntimeException("Expected " + type + " but got " + t);

        advance();
        return t;
    }

    private Token peekNext() {
        if (pos + 1 >= tokens.size())
            return null;
        return tokens.get(pos + 1);
    }
}

public class Main {

    public static void main(String[] args) {

        String html = "<div class=\"main\">Hello <b>world</b></div>";

        HtmlLexer lexer = new HtmlLexer(html);

        List<Token> tokens = lexer.tokenize();

        for (Token t : tokens)
            System.out.println(t);

        System.out.println();

        HtmlParser parser = new HtmlParser(tokens);
        Node root = parser.parse();
        root.print(0);
    }
}
