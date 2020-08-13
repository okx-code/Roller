package sh.okx.roller.character;

import java.util.List;

public interface CharacterDao {
    List<Character> getShallowCharacters(long id);
    Character getShallowCharacter(long id);
    Character getCharacter(long id);

    void setInitiative(long id, String initiative);
    void setScore(long id, Ability ability, int score);
    void setLevel(long id, int level);

    void createCharacter(long id, String name);
    void deleteCharacter(int id);
    void selectCharacter(long userId, int id);
}
