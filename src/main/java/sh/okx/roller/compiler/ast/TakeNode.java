package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.Util;
import sh.okx.roller.compiler.result.ArrayResult;
import sh.okx.roller.compiler.result.NodeResult;

public class TakeNode extends AstNode {
    private final AstNode left;
    private final AstNode right;

    public TakeNode(AstNode left, AstNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public NodeResult evaluate() {
        int top = right.evaluate().number();
        int[] array = left.evaluate().array();

        Util.sortDescending(array);

        if (top >= array.length) {
            return new ArrayResult(Util.toString(array), array);
        }

        int[] result = new int[top];
        if (top >= 0) {
            System.arraycopy(array, 0, result, 0, top);
        }

        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < top; i++) {
            if (i > 0) {
                str.append(" ");
            }
            str.append(result[i]);
        }
        str.append(" /");

        for (int i = top; i < array.length; i++) {
            str.append(" ");
            str.append(array[i]);
        }
        str.append("]");

        return new ArrayResult(str.toString(), result);
    }
}
