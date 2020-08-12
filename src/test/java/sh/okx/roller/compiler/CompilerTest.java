package sh.okx.roller.compiler;

import org.junit.Test;
import sh.okx.roller.character.Ability;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.context.MemoryContext;

public class CompilerTest {
    @Test
    public void testDice() {
        AstNode d8 = new Compiler().compile(null, "d8");
        System.out.println(d8.evaluate().toHumanReadable());

        MemoryContext context = new MemoryContext();
        context.setScore(Ability.DEXTERITY, -1);
        AstNode d8dex = new Compiler().compile(context, "d8 + DEX");
        System.out.println(d8dex.evaluate().toHumanReadable());
    }
}
