package sh.okx.roller.commands;

import java.util.List;
import java.util.Map;
import sh.okx.roller.Roller;
import sh.okx.roller.character.Ability;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.Skill;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class CharacterCommand extends Command {
    public CharacterCommand(Roller bot) {
        super(bot, "character");
        this.aliases = new String[] {"char", "c", "ch"};
        this.usage = "list / create <name> / select <name> / delete";
        this.description = "Show your character's statistics or modify your character";
    }

    @Override
    public void onSend(CommandEvent event) {
        event.subCommand("list", subevent -> {
            List<Character> characters = bot.getCharacterDao().getShallowCharacters(event.getUserId());
            Character selected = bot.getCharacterDao().getShallowCharacter(event.getUserId());
            StringBuilder reply = new StringBuilder("*Your characters:*");

            for (Character ch : characters) {
                if (selected != null && ch.getId() == selected.getId()) {
                    reply.append("\n**").append(ch.getName()).append("** ");
                } else {
                    reply.append("\n").append(ch.getName()).append(" ");
                }
            }

            subevent.reply(reply);
        });
        event.subCommand("create", subevent -> {
            List<Character> characters = bot.getCharacterDao().getShallowCharacters(event.getUserId());
            String name = subevent.getArguments().trim();
            if (name.isEmpty()) {
                event.usage();
                return;
            }
            for (Character ch : characters) {
                if (ch.getName().equalsIgnoreCase(name)) {
                    subevent.error("You have already created a character with that name!");
                }
            }

            bot.getCharacterDao().createCharacter(event.getUserId(), name);
            subevent.reply("Created character: " + name + "\n"
                    + "Use `,character select " + name + "` to select it.");
        });
        event.subCommand("select", subevent -> {
            List<Character> characters = bot.getCharacterDao().getShallowCharacters(event.getUserId());
            String name = subevent.getArguments().trim();
            if (name.isEmpty()) {
                event.usage();
                return;
            }
            for (Character ch : characters) {
                if (ch.getName().equalsIgnoreCase(name)) {
                    bot.getCharacterDao().selectCharacter(subevent.getUserId(), ch.getId());
                    event.reply("Selected character: " + name);
                    return;
                }
            }

            event.reply("Could not find a character with that name.");
        });
        event.subCommand("delete", subevent -> {
            List<Character> characters = bot.getCharacterDao().getShallowCharacters(event.getUserId());
            String name = subevent.getArguments().trim();
            if (name.isEmpty()) {
                event.usage();
                return;
            }
            for (Character ch : characters) {
                if (ch.getName().equalsIgnoreCase(name)) {
                    bot.getCharacterDao().deleteCharacter(ch.getId());
                    event.reply("Deleted character: " + name);
                    return;
                }
            }

            event.reply("Could not find a character with that name.");
        });

        Character character = bot.getCharacterDao().getCharacter(event.getUserId());
        if (character == null) {
            event.reply("You must create and select a character to use this command.\n"
                    + "See `,help character` for usage.");
            return;
        }

        StringBuilder msg = new StringBuilder("Character: **" + character.getName() + "**");

        msg.append("\nLevel: **").append(character.getLevel()).append("**")
                .append("\nInitiative roll: " + "`").append(character.getInitiative()).append("`");

        Map<Skill, String> skills = character.getSkills();
        for (Skill skill : Skill.values()) {
            String roll = skills.get(skill);
            if (roll != null) {
                msg.append("\n*").append(skill.getName()).append("*: ").append(roll);
            }
        }

        for (Ability ability : Ability.values()) {
            String score;
            try {
                score = String.valueOf(character.getScore(ability));
            } catch (IllegalArgumentException e) {
                score = "not set";
            }


            msg.append("\n**").append(ability).append("**: ").append(score);
        }

        event.reply(msg.toString());
    }
}
