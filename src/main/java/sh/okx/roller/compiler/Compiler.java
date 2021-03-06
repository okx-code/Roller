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

    public static void main(String[] args) {
        System.out.println(new Compiler().compile(null, "d20").evaluate().toHumanReadable());
        System.out.println(new Compiler().compile(null, "2d20").evaluate().toHumanReadable());
        System.out.println(new Compiler().compile(null, "5+d20").evaluate().toHumanReadable());
    }

    public List<Token> tokenize(String input) {
        int index = 0;
        List<Token> tokens = new ArrayList<>();

        while (index < input.length()) {
            char c = input.charAt(index);

            // "+" operator
            if (c == '+') {
                if (index + 1 < input.length() && input.charAt(index + 1) == '+') {
                    // "++" operator (array concat)
                    tokens.add(new Token(Type.ARRAY_CONCAT, "++"));
                    index += 2;
                    continue;
                }
                tokens.add(new Token(Type.ADD, "+"));
                index++;
                continue;
            }
            if (c == '-') {
                tokens.add(new Token(Type.SUBTRACT, "-"));
                index++;
                continue;
            }

            // repeat
            if (c == '^') {
                tokens.add(new Token(Type.REPEAT, "^"));
                index++;
                continue;
            }

            // int multiply
            if (c == '*') {
                tokens.add(new Token(Type.MULTIPLY, "*"));
                index++;
                continue;
            }

            // sort
            if (c == '#') {
                tokens.add(new Token(Type.SORT, "#"));
                index++;
                continue;
            }

            if (c == '(') {
                tokens.add(new Token(Type.LEFT_PARENTHESIS, "("));
                index++;
                continue;
            }
            if (c == ')') {
                tokens.add(new Token(Type.RIGHT_PARENTHESIS, ")"));
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
                    throw new IllegalArgumentException("Invalid token: " + string);
                }

                tokens.add(token);
                continue;
            }

            if (c == '/') {
                if (index + 1 < input.length() && input.charAt(index + 1) == '/') {
                    // "//" operator (divide)
                    tokens.add(new Token(Type.DIVIDE, "//"));
                    index += 2;
                    continue;
                }
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
            return new Token(Type.ABILITY, string);
        } else if (string.equalsIgnoreCase("pro")
            || string.equalsIgnoreCase("proficiency")
            || string.equalsIgnoreCase("prof")) {
            return new Token(Type.PROFICIENCY, string);
        }
        return null;
    }

    public void parse(List<Token> tokens) {
        int index = 0;

    }

    private void walk() {

    }
}
