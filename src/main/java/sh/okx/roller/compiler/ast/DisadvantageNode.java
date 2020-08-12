package sh.okx.roller.compiler.ast;

import java.util.Collections;
import java.util.List;
import sh.okx.roller.compiler.RandomSource;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class DisadvantageNode extends AstNode {
    private final AstNode child;

    public DisadvantageNode(AstNode child) {
        this.child = child;
    }

    @Override
    public List<AstNode> children() {
        return Collections.singletonList(child);
    }

    @Override
    public NodeResult evaluate() {
        int sides = child.evaluate().number();

        int r0 = RandomSource.random(1, sides);
        int r1 = RandomSource.random(1, sides);
        return new IntResult("dis[" + r0 + " " + r1 + "]", Math.min(r0, r1));
    }
}
