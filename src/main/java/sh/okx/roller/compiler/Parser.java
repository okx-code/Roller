package sh.okx.roller.compiler;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import sh.okx.roller.compiler.Token.Type;
import sh.okx.roller.compiler.ast.AbilityScore;
import sh.okx.roller.compiler.ast.AbilityScore.Ability;
import sh.okx.roller.compiler.ast.AddNode;
import sh.okx.roller.compiler.ast.AdvantageNode;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.ast.DiceNode;
import sh.okx.roller.compiler.ast.DisadvantageNode;
import sh.okx.roller.compiler.ast.NumberLiteral;
import sh.okx.roller.compiler.ast.TakeNode;

public class Parser {
    private int index;

    public AstNode parse(List<Token> tokens) {
        index = 0;

        Deque<AstNode> nodes = new ArrayDeque<>();

        while (index < tokens.size()) {

            Token token = tokens.get(index);
            Type type = token.getType();
            String val = token.getValue();
            if (type == Type.LITERAL) {
                index++;

                int value;
                try {
                    value = Integer.parseInt(val);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Cannot parse number (too big?): " + val);
                }
                nodes.push(new NumberLiteral(value));
            } else if (type == Type.ABILITIY) {
                index++;

                for (Ability ability : Ability.values()) {
                    if (ability.name().toUpperCase().startsWith(val.toUpperCase())) {
                        nodes.push(new AbilityScore(ability));
                    }
                }

                throw new IllegalArgumentException("Invalid ability: " + val);
            } else if (type == Type.MODIFIER) {
                index++;

                if (val.equalsIgnoreCase("adv")) {
                    nodes.push(new AdvantageNode(nodes.pop()));
                } else if (val.equalsIgnoreCase("dis")) {
                    nodes.push(new DisadvantageNode(nodes.pop()));
                }
            } else if (type == Type.DICE) {
                index++;
                AstNode count = nodes.pop();
                AstNode sides = nodes.peek();
                if (sides instanceof NumberLiteral) {
                    nodes.push(new DiceNode(count, nodes.pop()));
                } else {
                    nodes.push(new DiceNode(count, new NumberLiteral(1)));
                }
            } else if (type == Type.OPERATOR) {
                index++;
                nodes.push(new AddNode(nodes.pop(), nodes.pop()));
            } else if (type == Type.TAKE) {
                index++;
                nodes.push(new TakeNode(nodes.pop(), nodes.pop()));
            }
        }

        return nodes.pop();
    }
}
