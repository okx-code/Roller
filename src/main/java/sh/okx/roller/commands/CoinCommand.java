package sh.okx.roller.commands;

import sh.okx.roller.Roller;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class CoinCommand extends Command {

    public CoinCommand(Roller bot) {
        super(bot, "coin");
        this.description = "Find the value of coins";
        this.aliases = new String[] {"coins", "bal", "worth", "w", "c"};
    }

    @Override
    public void onSend(CommandEvent event) {

    }
}
