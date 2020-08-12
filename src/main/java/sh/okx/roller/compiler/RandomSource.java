package sh.okx.roller.compiler;

import java.security.SecureRandom;

public class RandomSource {
    private static final SecureRandom random = new SecureRandom();

    public static int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
