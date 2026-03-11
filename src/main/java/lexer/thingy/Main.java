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

        while (current != '\0') {

            if (current == '<') {
                tokens.add(new Token(TokenType.TAG_OPEN, "<"));
                advance();

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
            }

            else if (current == '=') {
                tokens.add(new Token(TokenType.EQUALS, "="));
                advance();
            }

            else if (current == '"') {
                tokens.add(new Token(TokenType.STRING, readString()));
            }

            else if (Character.isLetter(current)) {
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

public class Main {

    public static void main(String[] args) {

        String html = "<div class=\"main\">Hello <b>world</b></div>";

        HtmlLexer lexer = new HtmlLexer(html);

        List<Token> tokens = lexer.tokenize();

        for (Token t : tokens)
            System.out.println(t);
    }
}
