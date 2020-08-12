package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.NodeResult;

public abstract class AstNode {
    public abstract NodeResult evaluate();
}
