package sh.okx.roller.compiler.result;

public interface NodeResult {
    int number();
    int[] array();
    int[][] array2d();
    String result();

    String toHumanReadable();
}
