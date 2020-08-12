package sh.okx.roller.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;
import javax.sql.DataSource;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterDao;

public class SqlCharacterDao implements CharacterDao {
    private static final String CREATE_CHARACTER_TABLE = "CREATE TABLE IF NOT EXISTS characters ("
            + "id BIGINT, "
            + "initiative VARCHAR(255), "
            + "PRIMARY KEY (id))";
    private static final String CREATE_ABILITY_TABLE = "CREATE TABLE IF NOT EXISTS abilities ("
            + "character_id BIGINT, "
            + "ability VARCHAR(255), "
            + "score INT, "
            + "PRIMARY KEY (character_id, ability))";

    private static final String GET_CHARACTER = "SELECT * FROM characters WHERE id = ?";
    private static final String GET_ABILITIES = "SELECT * FROM abilities WHERE character_id = ?";
    private static final String SET_SCORE = "REPLACE INTO abilities (character_id, ability, score) VALUES (?, ?, ?)";
    private static final String SET_INITIATIVE = "INSERT INTO characters (id, initiative) VALUES (?, ?) ON DUPLICATE KEY UPDATE initiative = ?";

    private final DataSource source;

    public SqlCharacterDao(DataSource source) {
        this.source = source;

        init();
    }

    private void init() {
        try(Connection connection = source.getConnection()) {
            connection.createStatement().executeUpdate(CREATE_CHARACTER_TABLE);
            connection.createStatement().executeUpdate(CREATE_ABILITY_TABLE);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Character getCharacter(long id) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement characters = connection.prepareStatement(GET_CHARACTER);
            characters.setLong(1, id);

            ResultSet characterResult = characters.executeQuery();
            String initiative = null;
            if (characterResult.next()) {
                initiative = characterResult.getString("initiative");
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

            return new Character(initiative, scores);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setInitiative(long id, String initiative) {
        try(Connection connection = source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SET_INITIATIVE);
            statement.setLong(1, id);
            statement.setString(2, initiative);
            statement.setString(3, initiative);

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
}
