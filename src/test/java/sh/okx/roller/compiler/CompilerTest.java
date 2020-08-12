package sh.okx.roller.compiler;

import org.junit.Test;
import sh.okx.roller.compiler.ast.AstNode;

public class CompilerTest {
    @Test
    public void testDice() {
        AstNode d8 = new Compiler().compile("d8");
        System.out.println(d8.evaluate().toHumanReadable());
    }
}
