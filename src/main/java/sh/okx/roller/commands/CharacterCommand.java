package sh.okx.roller.commands;

import sh.okx.roller.Roller;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class CharacterCommand extends Command {
    public CharacterCommand(Roller bot) {
        super(bot, "character");
        this.aliases = new String[] {"char", "c", "ch"};
        this.description = "Show your character's statistics";
    }

    @Override
    public void onSend(CommandEvent event) {
        Character character = bot.getCharacterDao().getCharacter(event.getUserId());

        StringBuilder msg = new StringBuilder("Character for: " + event.getUser().getName());

        msg.append("\nInitiative roll: " + "`").append(character.getInitiative()).append("`\n");

        for (Ability ability : Ability.values()) {
            String score;
            try {
                score = String.valueOf(character.getScore(ability));
            } catch (IllegalArgumentException e) {
                score = "not set";
            }


            msg.append("\n`").append(ability).append("`: ").append(score);
        }

        event.reply(msg.toString());
    }
}
