package sh.okx.roller.compiler;

public class Token {
    private final Type type;
    private final String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public enum Type {
        ABILITIY(), // "dex" in d20 + dex
        DICE(Notation.INFIX, 2, 90), // "d" in 1d20
        LITERAL(), // "20" in d20
        ADD(Notation.INFIX, 2, 10), // "+" in d20 + 5
        ARRAY_CONCAT(Notation.INFIX, 2, 5), // "++" in 3/4d20 ++ 3/4d20
        REPEAT(Notation.INFIX, 2, 20),
        TAKE(Notation.INFIX, 2, 80), // "/" in 3/4d20
        MODIFIER(Notation.PREFIX, 1, 95),
        MULTIPLY(Notation.INFIX, 2, 25),
        LEFT_PARENTHESIS(Notation.PREFIX, 1, 100),
        RIGHT_PARENTHESIS(Notation.PREFIX, 1, 100);

        private final Notation notation;
        private final int arity;
        private final int precedence;

        Type() {
            this(null, 0, 0);
        }

        Type(Notation notation, int arity, int precedence) {
            this.notation = notation;
            this.arity = arity;
            this.precedence = precedence;
        }

        public Notation notation() {
            return notation;
        }

        public int arity() {
            return arity;
        }

        public int precedence() {
            return precedence;
        }
    }

    public enum Notation {
        INFIX,
        POSTFIX,
        PREFIX;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
