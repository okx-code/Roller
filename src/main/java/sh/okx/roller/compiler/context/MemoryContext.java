package sh.okx.roller.compiler.context;

import java.util.EnumMap;
import sh.okx.roller.character.Ability;

public class MemoryContext implements Context {
    private EnumMap<Ability, Integer> map = new EnumMap<>(Ability.class);

    public void setScore(Ability ability, int score) {
        map.put(ability, score);
    }

    @Override
    public int getScore(Ability ability) {
        if (!map.containsKey(ability)) {
            throw new IllegalArgumentException("Unknown ability: " + ability);
        }
        return map.get(ability);
    }
}
