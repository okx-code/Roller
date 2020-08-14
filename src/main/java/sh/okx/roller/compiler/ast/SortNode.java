package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.Util;
import sh.okx.roller.compiler.result.ArrayResult;
import sh.okx.roller.compiler.result.NodeResult;

public class SortNode extends AstNode {
    private final AstNode child;

    public SortNode(AstNode child) {
        this.child = child;
    }

    @Override
    public NodeResult evaluate() {
        int[] arr = child.evaluate().array();
        Util.sortDescending(arr);
        return new ArrayResult(Util.toString(arr), arr);
    }
}
