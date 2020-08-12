package sh.okx.roller.character;

public interface CharacterDao {
    Character getCharacter(long id);
    void setInitiative(long id, String initiative);
    void setScore(long id, Ability ability, int score);
}
