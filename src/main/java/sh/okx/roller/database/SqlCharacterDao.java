package sh.okx.roller.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterDao;

public class SqlCharacterDao implements CharacterDao {
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS users ("
            + "user_id BIGINT NOT NULL, "
            + "current_character_id INT NOT NULL, "
            + "PRIMARY KEY (user_id))";

    private static final String CREATE_CHARACTER_TABLE = "CREATE TABLE IF NOT EXISTS characters ("
            + "character_id INT NOT NULL AUTO_INCREMENT, "
            + "name VARCHAR(255), "
            + "user_id BIGINT, "
            + "initiative VARCHAR(255) DEFAULT 'd20 + dex', "
            + "PRIMARY KEY (character_id))";
    private static final String CREATE_ABILITY_TABLE = "CREATE TABLE IF NOT EXISTS abilities ("
            + "character_id BIGINT, "
            + "ability VARCHAR(255), "
            + "score INT, "
            + "PRIMARY KEY (character_id, ability))";

    private static final String GET_CHARACTER = "SELECT * FROM characters WHERE character_id = (SELECT current_character_id FROM users WHERE user_id = ?)";
    private static final String GET_ABILITIES = "SELECT * FROM abilities WHERE character_id = ?";
    private static final String SET_SCORE = "REPLACE INTO abilities (character_id, ability, score) VALUES (?, ?, ?)";
    private static final String SET_INITIATIVE = "UPDATE characters SET initiative = ? WHERE character_id = ?";
    private static final String GET_SHALLOW_CHARACTERS = "SELECT * FROM characters WHERE user_id = ?";
    private static final String CREATE_CHARACTER = "INSERT INTO characters (name, user_id) VALUES (?, ?)";
    private static final String DELETE_ABILITIES = "DELETE FROM abilities WHERE character_id = ?";
    private static final String DELETE_CHARACTER = "DELETE FROM characters WHERE character_id = ?";
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
                characters.add(new Character(characterResult.getInt("character_id"), name, initiative, null));
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
                    characterResult.getString("name"),
                    characterResult.getString("initiative"),
                    null);
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
            abilities.setLong(1, id);

            Map<Ability, Integer> scores = new EnumMap<>(Ability.class);

            ResultSet results = abilities.executeQuery();
            while (results.next()) {
                String abilityName = results.getString("ability");
                int score = results.getInt("score");
                Ability ability = Ability.valueOf(abilityName);
                scores.put(ability, score);
            }

            return new Character(character.getId(), character.getName(), character.getInitiative(), scores);
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
