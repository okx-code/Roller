package sh.okx.roller.commands.ability;

import sh.okx.roller.Roller;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class ScoreCommand extends Command {
    private final Ability ability;

    public ScoreCommand(Roller bot, Ability ability, String... aliases) {
        super(bot, ability.name().toLowerCase());
        this.ability = ability;
        this.aliases = aliases;
        this.usage = "<" + ability.name().toLowerCase() + ">";
        this.description = "Set your " + ability.name().toLowerCase() + " score";
    }

    @Override
    public void onSend(CommandEvent event) {
        int i = event.requireInt(event.requireArguments(1)[0]);

        Character character = bot.getCharacterDao().getShallowCharacter(event.getUserId());
        if (character == null) {
            event.error("You must select a character to use this command.");
            return;
        }

        bot.getCharacterDao().setScore(character.getId(), ability, i);
        event.reply("Set your `" + ability + "` score to: **" + i + "** with modifier: **" + Ability.getModifier(i) + "**");
    }
}
