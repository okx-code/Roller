package sh.okx.roller.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import sh.okx.roller.compiler.Token.Type;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.context.Context;

public class Compiler {
    private static final Predicate<Character> IS_NUMBER = c -> c >= '0' && c <= '9';
    private static final Predicate<Character> IS_LETTER = c -> (c >= 'a' && c <= 'z') || (c >= 'A'
            && c <= 'Z');

    public AstNode compile(Context context, String input) {
        List<Token> tokens = tokenize(input);
        tokens = new Shunter().shunt(tokens);
        return new Parser(context).parse(tokens);
    }

    public List<Token> tokenize(String input) {
        int index = 0;
        List<Token> tokens = new ArrayList<>();

        while (index < input.length()) {
            char c = input.charAt(index);

            // "+" operator
            if (c == '+') {
                tokens.add(new Token(Type.OPERATOR, "+"));
                index++;
                continue;
            }

            // whitespace
            if (c == ' ' || c == '\t' || c == '\n') {
                index++;
                continue;
            }

            // number literals
            if (IS_NUMBER.test(c)) {
                StringBuilder value = new StringBuilder();
                do {
                    value.append(c);
                    index++;
                    if (index >= input.length()) {
                        break;
                    }
                    c = input.charAt(index);
                } while (c >= '0' && c <= '9');

                tokens.add(new Token(Type.LITERAL, value.toString()));
                continue;
            }

            // letters - ability score, dice roll
            if (IS_LETTER.test(c)) {
                StringBuilder value = new StringBuilder();
                do {
                    value.append(c);
                    index++;
                    if (index >= input.length()) {
                        break;
                    }
                    c = input.charAt(index);
                } while (IS_LETTER.test(c));

                String string = value.toString();
                Token token = letterToken(string);
                if (token == null) {
                    if (string.startsWith("d") || string.startsWith("D")) {
                        tokens.add(new Token(Type.DICE, string.substring(0, 1)));
                        token = letterToken(string.substring(1));
                    } else if (string.endsWith("d") || string.endsWith("D")) {
                        int len = string.length();
                        tokens.add(new Token(Type.DICE, string.substring(len - 1, len)));
                        token = letterToken(string.substring(0, len - 1));
                    }
                    if (token == null) {
                        throw new IllegalArgumentException("Invalid token: " + string);
                    }
                }

                tokens.add(token);
                continue;
            }

            if (c == '/') {
                tokens.add(new Token(Type.TAKE, "/"));
                index++;
                continue;
            }

            throw new IllegalArgumentException("Character not permitted: " + c);
        }

        return tokens;
    }

    private Token letterToken(String string) {
        if (string.equalsIgnoreCase("adv") || string.equalsIgnoreCase("dis")) {
            return new Token(Type.MODIFIER, string);
        } else if (string.equalsIgnoreCase("d")) {
            return new Token(Type.DICE, string);
        } else if (string.equalsIgnoreCase("str") || string.equalsIgnoreCase("strength")
                || string.equalsIgnoreCase("dex") || string.equalsIgnoreCase("dexterity")
                || string.equalsIgnoreCase("con") || string.equalsIgnoreCase("constitution")
                || string.equalsIgnoreCase("int") || string.equalsIgnoreCase("intelligence")
                || string.equalsIgnoreCase("wis") || string.equalsIgnoreCase("wisdom")
                || string.equalsIgnoreCase("cha") || string.equalsIgnoreCase("charisma")) {
            return new Token(Type.ABILITIY, string);
        }
        return null;
    }

    public void parse(List<Token> tokens) {
        int index = 0;

    }

    private void walk() {

    }
}
