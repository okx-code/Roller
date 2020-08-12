package sh.okx.roller.compiler.result;

public class IntResult implements NodeResult {
    private final String human;
    private final int value;

    public IntResult(String human, int value) {
        this.human = human;
        this.value = value;
    }

    @Override
    public int number() {
        return value;
    }

    @Override
    public int[] array() {
        return new int[] {value};
    }

    @Override
    public int[][] array2d() {
        return new int[][] {{value}};
    }

    @Override
    public String result() {
        return String.valueOf(value);
    }

    @Override
    public String toHumanReadable() {
        return human;
    }
}
