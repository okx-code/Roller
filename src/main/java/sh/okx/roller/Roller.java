package sh.okx.roller;

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
import sh.okx.roller.command.CommandListener;
import sh.okx.roller.commands.EvalCommand;
import sh.okx.roller.commands.HelpCommand;
import sh.okx.roller.commands.RollCommand;

@Log
public class Roller {
    @Getter
    private final JDA jda;
    private final Properties config;
    @Getter
    private CommandListener commands;

    public Roller(Properties config) throws LoginException {
        this.config = config;

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
