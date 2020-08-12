package sh.okx.roller.compiler.ast;

import java.util.Collections;
import java.util.List;
import sh.okx.roller.compiler.result.IntResult;
import sh.okx.roller.compiler.result.NodeResult;

public class AbilityScore extends AstNode {
    private final Ability ability;

    public AbilityScore(Ability ability) {
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    public enum Ability {
        DEXTERITY,
        CONSTITUTION,
        STRENGTH,
        INTELLIGENCE,
        WISDOM,
        CHARIsMA
    }

    @Override
    public List<AstNode> children() {
        return Collections.emptyList();
    }

    @Override
    public NodeResult evaluate() {
        return new IntResult("1", 1);
    }
}
