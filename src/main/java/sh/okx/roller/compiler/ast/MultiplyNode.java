package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.Array2dResult;
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
        int times = right.evaluate().number();

        int len = 0;
        StringBuilder human = new StringBuilder();
        NodeResult[] results = new NodeResult[times];
        for (int i = 0; i < times; i++) {
            results[i] = left.evaluate();
            len += results[i].array().length;
            if (i > 0) {
                human.append(" ");
            }
            human.append(results[i].toHumanReadable());
        }

        int[][] array = new int[times][];
        int index = 0;
        for (NodeResult result : results) {
            array[index++] = result.array();
        }

        return new Array2dResult(human.toString(), array);
    }
}
