package sh.okx.roller.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterDao;
import sh.okx.roller.character.Skill;

public class SqlCharacterDao implements CharacterDao {
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users ("
            + "user_id BIGINT NOT NULL, "
            + "current_character_id INT NOT NULL, "
            + "PRIMARY KEY (user_id))";

    private static final String CREATE_CHARACTER_TABLE = "CREATE TABLE IF NOT EXISTS characters ("
            + "character_id INT NOT NULL AUTO_INCREMENT, "
            + "name VARCHAR(255), "
            + "user_id BIGINT, "
            + "level INT DEFAULT 1, "
            + "initiative VARCHAR(255) DEFAULT 'd20 + dex', "
            + "PRIMARY KEY (character_id))";
    private static final String CREATE_ABILITY_TABLE = "CREATE TABLE IF NOT EXISTS abilities ("
            + "character_id BIGINT, "
            + "ability VARCHAR(255), "
            + "score INT, "
            + "PRIMARY KEY (character_id, ability))";
    private static final String CREATE_SKILLS_TABLE = "CREATE TABLE IF NOT EXISTS skills ("
        + "character_id BIGINT, "
        + "skill VARCHAR(255), "
        + "roll VARCHAR(255), "
        + "PRIMARY KEY (character_id, skill))";

    private static final String GET_CHARACTER = "SELECT * FROM characters WHERE character_id = (SELECT current_character_id FROM users WHERE user_id = ?)";
    private static final String GET_ABILITIES = "SELECT * FROM abilities WHERE character_id = ?";
    private static final String GET_SKILLS = "SELECT * FROM skills WHERE character_id = ?";
    private static final String SET_SKILL = "REPLACE INTO skills (character_id, skill, roll) VALUES (?, ?, ?)";
    private static final String SET_SCORE = "REPLACE INTO abilities (character_id, ability, score) VALUES (?, ?, ?)";
    private static final String SET_INITIATIVE = "UPDATE characters SET initiative = ? WHERE character_id = ?";
    private static final String SET_LEVEL = "UPDATE characters SET level = ? WHERE character_id = ?";
    private static final String GET_SHALLOW_CHARACTERS = "SELECT * FROM characters WHERE user_id = ?";
    private static final String CREATE_CHARACTER = "INSERT INTO characters (name, user_id) VALUES (?, ?)";
    private static final String DELETE_ABILITIES = "DELETE FROM abilities WHERE character_id = ?";
    private static final String DELETE_CHARACTER = "DELETE FROM characters WHERE character_id = ?";
    private static final String DELETE_SKILLS = "DELETE FROM skills WHERE character_id = ?";
    private static final String SELECT_CHARACTER = "REPLACE INTO users (user_id, current_character_id) VALUES (?, ?)";

    private final DataSource source;

    public SqlCharacterDao(DataSource source) {
        this.source = source;

        init();
    }

    private void init() {
        try(Connection connection = source.getConnection()) {
            connection.createStatement().executeUpdate(CREATE_CHARACTER_TABLE);
            connection.createStatement().executeUpdate(CREATE_ABILITY_TABLE);
            connection.createStatement().executeUpdate(CREATE_USER_TABLE);
            connection.createStatement().executeUpdate(CREATE_SKILLS_TABLE);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Character> getShallowCharacters(long id) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_SHALLOW_CHARACTERS);
            statement.setLong(1, id);

            List<Character> characters = new ArrayList<>();
            ResultSet characterResult = statement.executeQuery();
            while (characterResult.next()) {
                String initiative = characterResult.getString("initiative");
                String name = characterResult.getString("name");
                characters.add(new Character(characterResult.getInt("character_id"), characterResult.getInt("level"), name, initiative, null, null));
            }

            return characters;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Character getShallowCharacter(long id) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement characters = connection.prepareStatement(GET_CHARACTER);
            characters.setLong(1, id);

            ResultSet characterResult = characters.executeQuery();
            if(!characterResult.next()) {
                return null;
            }

            return new Character(characterResult.getInt("character_id"),
                    characterResult.getInt("level"),
                    characterResult.getString("name"),
                    characterResult.getString("initiative"),
                    null, null);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Character getCharacter(long id) {
        try(Connection connection = source.getConnection()) {
            Character character = getShallowCharacter(id);
            if (character == null) {
                return null;
            }

            PreparedStatement abilities = connection.prepareStatement(GET_ABILITIES);
            abilities.setLong(1, character.getId());

            Map<Ability, Integer> scores = new EnumMap<>(Ability.class);

            ResultSet results = abilities.executeQuery();
            while (results.next()) {
                String abilityName = results.getString("ability");
                int score = results.getInt("score");
                Ability ability = Ability.valueOf(abilityName);
                scores.put(ability, score);
            }

            PreparedStatement skillStatement = connection.prepareStatement(GET_SKILLS);
            skillStatement.setLong(1, character.getId());

            Map<Skill, String> skills = new HashMap<>();

            ResultSet skillResult = skillStatement.executeQuery();
            while (skillResult.next()) {
                String skill = skillResult.getString("skill");
                String roll = skillResult.getString("roll");
                skills.put(Skill.valueOf(skill), roll);
            }

            return new Character(character.getId(), character.getLevel(), character.getName(), character.getInitiative(), scores, skills);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setInitiative(long id, String initiative) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SET_INITIATIVE);
            statement.setString(1, initiative);
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setLevel(long id, int level) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SET_LEVEL);
            statement.setInt(1, level);
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setSkill(long id, Skill skill, String roll) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SET_SKILL);
            statement.setLong(1, id);
            statement.setString(2, skill.name());
            statement.setString(3, roll);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setScore(long id, Ability ability, int score) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SET_SCORE);
            statement.setLong(1, id);
            statement.setString(2, ability.name());
            statement.setInt(3, score);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void createCharacter(long id, String name) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CREATE_CHARACTER);
            statement.setString(1, name);
            statement.setLong(2, id);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteCharacter(int id) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement deleteAbilities = connection.prepareStatement(DELETE_ABILITIES);
            deleteAbilities.setInt(1, id);
            deleteAbilities.executeUpdate();

            PreparedStatement deleteCharacter = connection.prepareStatement(DELETE_CHARACTER);
            deleteCharacter.setInt(1, id);
            deleteCharacter.executeUpdate();

            PreparedStatement deleteSkills = connection.prepareStatement(DELETE_SKILLS);
            deleteSkills.setInt(1, id);
            deleteSkills.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void selectCharacter(long userId, int characterId) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_CHARACTER);
            statement.setLong(1, userId);
            statement.setInt(2, characterId);

            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
