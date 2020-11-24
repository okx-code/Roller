package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.ArrayResult;
import sh.okx.roller.compiler.result.NodeResult;

public class SubtractNode extends AstNode {
    private final AstNode left;
    private final AstNode right;

    public SubtractNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public NodeResult evaluate() {
        NodeResult rightEval = this.right.evaluate();
        int[] right = rightEval.array();
        NodeResult leftEval = this.left.evaluate();
        int[] left = leftEval.array();
        for (int i = 0; i < left.length; i++) {
            left[i] = -left[i];
        }

        int[] result = new int[right.length + left.length];

        System.arraycopy(right, 0, result, 0, right.length);
        System.arraycopy(left, 0, result, right.length, left.length);

        return new ArrayResult(rightEval.toHumanReadable() + " - " + leftEval.toHumanReadable(), result);
    }
}
