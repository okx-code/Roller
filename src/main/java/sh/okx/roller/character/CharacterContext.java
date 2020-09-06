package sh.okx.roller.character;

import sh.okx.roller.compiler.context.Context;

public class CharacterContext implements Context {
    private final CharacterDao characterDao;
    private final long id;
    private Character character;

    public CharacterContext(CharacterDao characterDao, long id) {
        this.characterDao = characterDao;
        this.id = id;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    @Override
    public int getScore(Ability ability) {
        synchronized (characterDao) {
            if (character == null) {
                character = characterDao.getCharacter(id);
            }
        }
        return Ability.getModifier(character.getScore(ability));
    }

    @Override
    public int getLevel() {
        return character.getLevel();
    }

}
