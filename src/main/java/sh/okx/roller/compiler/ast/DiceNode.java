package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.RandomSource;
import sh.okx.roller.compiler.Util;
import sh.okx.roller.compiler.result.ArrayResult;
import sh.okx.roller.compiler.result.NodeResult;

public class DiceNode extends AstNode {
    private final AstNode left;
    private final AstNode right;

    public DiceNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public NodeResult evaluate() {
        int sides = left.evaluate().number();
        int count = right.evaluate().number();

        int[] result = new int[count];
        for (int i = 0; i < result.length; i++) {
            result[i] = RandomSource.random(1, sides);
        }
        return new ArrayResult(Util.toString(result), result);
    }
}
