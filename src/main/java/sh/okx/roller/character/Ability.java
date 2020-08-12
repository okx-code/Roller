package sh.okx.roller.character;

public enum Ability {
    DEXTERITY,
    CONSTITUTION,
    STRENGTH,
    INTELLIGENCE,
    WISDOM,
    CHARISMA;

    public static int getModifier(int score) {
        return (int) Math.floor((score - 10) / 2F);
    }
}