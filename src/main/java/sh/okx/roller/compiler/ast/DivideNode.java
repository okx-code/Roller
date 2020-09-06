package sh.okx.roller.compiler.ast;

import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class DivideNode extends AstNode {
  private final AstNode left;
  private final AstNode right;

  public DivideNode(AstNode left, AstNode right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public NodeResult evaluate() {
    NodeResult rightEval = this.right.evaluate();
    int right = rightEval.number();
    NodeResult leftEval = this.left.evaluate();
    int left = leftEval.number();

    int result = left / right;

    return new IntResult(rightEval.toHumanReadable() + " / " + leftEval.toHumanReadable(), result);
  }
}
