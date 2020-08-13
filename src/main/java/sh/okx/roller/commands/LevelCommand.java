package sh.okx.roller.commands;

import sh.okx.roller.Roller;
import sh.okx.roller.character.Character;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.compiler.Util;

public class LevelCommand extends Command {
    public LevelCommand(Roller bot) {
        super(bot, "level");
        this.aliases = new String[] {"lvl", "l"};
        this.usage = "<level>";
        this.description = "Set your character's level";
    }

    @Override
    public void onSend(CommandEvent event) {
        int level = event.requirePositiveInt(event.requireArguments(1)[0]);

        Character character = bot.getCharacterDao().getShallowCharacter(event.getUserId());
        if (character == null) {
            event.error("You must select a character to use this command.");
            return;
        }

        bot.getCharacterDao().setLevel(character.getId(), level);
        event.reply("Set your level to: **" + level + "** with proficiency bonus **"
                + Util.plusNumber(Character.getProficiencyBonus(level)) + "**");
    }
}
