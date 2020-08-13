package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class MultiplyNode extends AstNode {
    private final AstNode right;
    private final AstNode left;

    public MultiplyNode(AstNode right, AstNode left) {
        this.right = right;
        this.left = left;
    }

    @Override
    public NodeResult evaluate() {
        NodeResult rightNode = this.right.evaluate();
        int right = rightNode.number();
        NodeResult leftNode = this.left.evaluate();
        int left = leftNode.number();

        int result = right * left;
        return new IntResult(leftNode.toHumanReadable() + " * " + rightNode.toHumanReadable(), result);
    }
}
