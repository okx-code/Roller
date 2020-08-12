package sh.okx.roller.compiler.result;

import sh.okx.roller.compiler.Util;

public class Array2dResult implements NodeResult {
    private final String human;
    private final int[][] array;

    public Array2dResult(String human, int[][] array) {
        this.human = human;
        this.array = array;
    }

    @Override
    public int number() {
        return Util.sum(array());
    }

    @Override
    public int[] array() {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Util.sum(array[i]);
        }
        return result;
    }

    @Override
    public String result() {
        return Util.toString(array());
    }

    @Override
    public String toHumanReadable() {
        return human;
    }
}
