package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.ArrayResult;
import sh.okx.roller.compiler.result.NodeResult;

public class AddNode extends AstNode {
    private final AstNode left;
    private final AstNode right;

    public AddNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public NodeResult evaluate() {
        NodeResult rightEval = this.right.evaluate();
        int[] right = rightEval.array();
        NodeResult leftEval = this.left.evaluate();
        int[] left = leftEval.array();

        int[] result = new int[right.length + left.length];

        System.arraycopy(right, 0, result, 0, right.length);
        System.arraycopy(left, 0, result, right.length, left.length);

        return new ArrayResult(rightEval.toHumanReadable() + " + " + leftEval.toHumanReadable(), result);
    }
}
