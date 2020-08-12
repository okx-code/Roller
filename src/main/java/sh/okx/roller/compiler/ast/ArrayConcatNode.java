package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.Array2dResult;
import sh.okx.roller.compiler.result.NodeResult;

public class ArrayConcatNode extends AstNode {
    private final AstNode left;
    private final AstNode right;

    public ArrayConcatNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public NodeResult evaluate() {
        NodeResult rightEval = this.right.evaluate();
        int[][] right = rightEval.array2d();
        NodeResult leftEval = this.left.evaluate();
        int[][] left = leftEval.array2d();

        int[][] result = new int[right.length + left.length][];

        System.arraycopy(right, 0, result, 0, right.length);
        System.arraycopy(left, 0, result, right.length, left.length);

        return new Array2dResult(rightEval.toHumanReadable() + " ++ " + leftEval.toHumanReadable(), result);
    }
}
