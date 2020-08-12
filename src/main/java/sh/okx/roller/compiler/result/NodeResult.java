package sh.okx.roller.compiler.result;

public interface NodeResult {
    int number();
    int[] array();

    String toHumanReadable();
}
