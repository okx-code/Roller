package sh.okx.roller.character;

import java.util.Map;

public class Character {
    private final int id;
    private final int level;
    private final String name;
    private final String initiative;
    private final Map<Ability, Integer> abilities;

    public Character(int id, int level, String name, String initiative, Map<Ability, Integer> abilities) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.initiative = initiative;
        this.abilities = abilities;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
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

    public static int getProficiencyBonus(int level) {
        return (int) Math.ceil(level / 4F) + 1;
    }
}
