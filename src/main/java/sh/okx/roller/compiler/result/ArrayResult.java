package sh.okx.roller.compiler.result;

public class ArrayResult implements NodeResult {
    private final String human;
    private final int[] array;

    public ArrayResult(String human, int[] array) {
        this.human = human;
        this.array = array;
    }

    @Override
    public int number() {
        int result = 0;
        for (int elem : array) {
            result += elem;
        }
        return result;
    }

    @Override
    public int[] array() {
        return array;
    }

    @Override
    public String toHumanReadable() {
        return human;
    }
}
