package sh.okx.roller.compiler.result;

import sh.okx.roller.compiler.Util;

public class ArrayResult implements NodeResult {
    private final String human;
    private final int[] array;

    public ArrayResult(String human, int[] array) {
        this.human = human;
        this.array = array;
    }

    @Override
    public int number() {
        return Util.sum(array);
    }

    @Override
    public int[] array() {
        return array;
    }

    @Override
    public int[][] array2d() {
        return new int[][] {array};
    }

    @Override
    public String result() {
        return String.valueOf(Util.sum(array));
    }

    @Override
    public String toHumanReadable() {
        return human;
    }
}
