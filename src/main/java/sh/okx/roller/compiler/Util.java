package sh.okx.roller.compiler;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Util {
    private static final DecimalFormat PLUS_NUMBER_FORMAT = new DecimalFormat("+#;-#");

    public static String toString(int[] array) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(array[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    public static void sortDescending(int[] from) {
        Arrays.sort(from);

        int[] reversed = new int[from.length];
        for (int i = 0; i < from.length; i++) {
            reversed[from.length - 1 - i] = from[i];
        }

        for (int i = 0; i < from.length; i++) {
            from[i] = reversed[i];
        }
    }

    public static int sum(int[] array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }

    public static String plusNumber(int num) {
        return PLUS_NUMBER_FORMAT.format(num);
    }
}
