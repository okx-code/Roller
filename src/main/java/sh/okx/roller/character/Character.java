package sh.okx.roller.character;

import java.util.Map;

public class Character {
    private final int id;
    private final int level;
    private final String name;
    private final String initiative;
    private final Map<Ability, Integer> abilities;
    private final Map<Skill, String> skills;

    public Character(int id, int level, String name, String initiative,
        Map<Ability, Integer> abilities,
        Map<Skill, String> skills) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.initiative = initiative;
        this.abilities = abilities;
        this.skills = skills;
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

    public String getSkill(Skill skill) {
        return skills.get(skill);
    }

    public static int getProficiencyBonus(int level) {
        return (int) Math.ceil(level / 4F) + 1;
    }
}
