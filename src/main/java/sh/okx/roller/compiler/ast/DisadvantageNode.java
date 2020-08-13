package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.RandomSource;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class DisadvantageNode extends AstNode {
    @Override
    public NodeResult evaluate() {
        int sides = 20;

        int r0 = RandomSource.random(1, sides);
        int r1 = RandomSource.random(1, sides);
        return new IntResult("dis[" + r0 + " " + r1 + "]", Math.min(r0, r1));
    }
}
