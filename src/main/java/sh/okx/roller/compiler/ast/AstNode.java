package sh.okx.roller.compiler.ast;

import java.util.List;
import sh.okx.roller.compiler.result.NodeResult;

public abstract class AstNode {
    public abstract List<AstNode> children();
    public abstract NodeResult evaluate();
}
