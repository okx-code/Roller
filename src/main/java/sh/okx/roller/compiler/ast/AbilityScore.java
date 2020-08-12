package sh.okx.roller.compiler.ast;

import sh.okx.roller.character.Ability;
import sh.okx.roller.compiler.context.Context;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class AbilityScore extends AstNode {
    private final Context context;
    private final Ability ability;

    public AbilityScore(Context context, Ability ability) {
        this.context = context;
        this.ability = ability;
    }

    @Override
    public NodeResult evaluate() {
        int val = context.getScore(ability);
        return new IntResult(String.valueOf(val), val);
    }
}
