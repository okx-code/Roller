package sh.okx.roller.compiler.ast;

import sh.okx.roller.character.Character;
import sh.okx.roller.compiler.context.Context;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class ProficiencyNode extends AstNode {
  private final Context context;

  public ProficiencyNode(Context context) {
    this.context = context;
  }

  @Override
  public NodeResult evaluate() {
    int prof = Character.getProficiencyBonus(context.getLevel());
    return new IntResult(String.valueOf(prof), prof);
  }

}
