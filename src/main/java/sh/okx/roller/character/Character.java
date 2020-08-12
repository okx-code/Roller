package sh.okx.roller.character;

import java.util.Map;

public class Character {
    private final String initiative;
    private final Map<Ability, Integer> abilities;

    public Character(String initiative, Map<Ability, Integer> abilities) {
        this.initiative = initiative;
        this.abilities = abilities;
    }

    public String getInitiative() {
        return initiative;
    }

    public int getScore(Ability ability) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("Could not find ability score for: " + ability);
        }
        return abilities.get(ability);
    }
}
