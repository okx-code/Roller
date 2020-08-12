package sh.okx.roller.character;

import java.util.Map;

public class Character {
    private final int id;
    private final String name;
    private final String initiative;
    private final Map<Ability, Integer> abilities;

    public Character(int id, String name, String initiative, Map<Ability, Integer> abilities) {
        this.id = id;
        this.name = name;
        this.initiative = initiative;
        this.abilities = abilities;
    }

    public int getId() {
        return id;
    }

    public String getInitiative() {
        return initiative;
    }

    public String getName() {
        return name;
    }

    public int getScore(Ability ability) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("Could not find ability score for: " + ability);
        }
        return abilities.get(ability);
    }
}
