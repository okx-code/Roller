package sh.okx.roller.commands;

import sh.okx.roller.Roller;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class SourceCommand extends Command {
    public SourceCommand(Roller bot) {
        super(bot, "source");
        this.aliases = new String[] {"src"};
        this.description = "View source code of the bot";
    }

    @Override
    public void onSend(CommandEvent event) {
        event.reply("https://github.com/okx-code/Roller");
    }
}
