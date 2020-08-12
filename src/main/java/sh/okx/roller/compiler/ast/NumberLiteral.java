package sh.okx.roller.compiler.ast;

import java.util.Collections;
import java.util.List;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class NumberLiteral extends AstNode {
    private final int value;

    public NumberLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public List<AstNode> children() {
        return Collections.emptyList();
    }

    @Override
    public NodeResult evaluate() {
        return new IntResult(String.valueOf(value), value);
    }
}
