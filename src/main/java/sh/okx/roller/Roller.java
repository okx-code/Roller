package sh.okx.roller;

import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.CharacterDao;
import sh.okx.roller.command.CommandListener;
import sh.okx.roller.command.StopCommand;
import sh.okx.roller.commands.CharacterCommand;
import sh.okx.roller.commands.EvalCommand;
import sh.okx.roller.commands.HelpCommand;
import sh.okx.roller.commands.InitiativeCommand;
import sh.okx.roller.commands.RollCommand;
import sh.okx.roller.commands.ability.ScoreCommand;
import sh.okx.roller.database.SqlCharacterDao;

@Log
public class Roller {
    @Getter
    private final JDA jda;
    private final Properties config;
    @Getter
    private CommandListener commands;
    @Getter
    private final CharacterDao characterDao;

    public Roller(Properties config) throws LoginException {
        this.config = config;

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/" + config.getProperty("sql.database"));
        ds.setUsername(config.getProperty("sql.username"));
        ds.setPassword(config.getProperty("sql.password"));
        this.characterDao = new SqlCharacterDao(ds);

        registerCommands();

        this.jda = JDABuilder.createDefault(config.getProperty("token"))
            .addEventListeners(commands)
            .setActivity(Activity.playing("D&D 5e: " + commands.getPrefix() + "help"))
            .build();
    }

    private void registerCommands() {
        commands = new CommandListener(this, config.getProperty("prefix"));
        commands.addCommand(new HelpCommand(this));
        commands.addCommand(new RollCommand(this));
        commands.addCommand(new EvalCommand(this));
        commands.addCommand(new StopCommand(this));

        commands.addCommand(new CharacterCommand(this));

        commands.addCommand(new InitiativeCommand(this));

        commands.addCommand(new ScoreCommand(this, Ability.DEXTERITY, "dex"));
        commands.addCommand(new ScoreCommand(this, Ability.STRENGTH, "str"));
        commands.addCommand(new ScoreCommand(this, Ability.CHARISMA, "cha"));
        commands.addCommand(new ScoreCommand(this, Ability.CONSTITUTION, "con"));
        commands.addCommand(new ScoreCommand(this, Ability.INTELLIGENCE, "int"));
        commands.addCommand(new ScoreCommand(this, Ability.WISDOM, "wis"));

    }

    public static void main(String[] args) throws Exception {
        File config = new File("config.properties");
        if (config.exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(config));
            new Roller(properties);
        } else {
            if (copyResource(config.getName())) {
                log.info("Copied default configuration");
            }
        }
    }

    private static boolean copyResource(String resource) throws IOException {
        File file = new File(resource);
        if (file.exists()) {
            return false;
        }

        InputStream configResource = Roller.class.getResourceAsStream("/" + resource);
        FileOutputStream stream = new FileOutputStream(file);

        byte[] buf = new byte[1024];
        int read;
        while ((read = configResource.read(buf)) != -1) {
            stream.write(buf, 0, read);
        }
        return true;
    }
}
