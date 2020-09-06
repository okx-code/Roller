package sh.okx.roller.commands.ability;

import sh.okx.roller.Roller;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterDao;
import sh.okx.roller.character.Skill;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

/**
 * ,skill bonus perception 5
 * ,skill roll perception dex + 7
 * ,skill reset perception
 */
public class SkillCommand extends Command {

  public SkillCommand(Roller bot) {
    super(bot, "skill");
    this.usage = "bonus <skill> <bonus> / roll <skill> <roll> / reset <skill>";
    this.description = "Show your character's statistics or modify your character";
  }

  @Override
  public void onSend(CommandEvent event) {
    CharacterDao dao = bot.getCharacterDao();
    Character character = dao.getCharacter(event.getUserId());
    if (character == null) {
      event.reply("You must create and select a character to use this command.\n"
          + "See `,help character` for usage.");
      return;
    }

    event.subCommand("bonus", bonusEvent -> {
      String[] args = bonusEvent.requireArguments(2);
      Skill skill = Skill.matchSkill(args[0]);
      if (skill == null) {
        event.reply("Unknown skill: " + args[0]);
        return;
      }
      String roll = "d20 + " + skill.getAbility().name().substring(0, 3) + " + " + args[1];
      dao.setSkill(character.getId(), skill, roll);
      event.reply("Set skill roll for " + skill + " to `" + roll + "`");
    });
    event.subCommand("roll", bonusEvent -> {
      String[] args = bonusEvent.requireArguments(2);
      Skill skill = Skill.matchSkill(args[0]);
      if (skill == null) {
        event.reply("Unknown skill: " + args[0]);
        return;
      }
      dao.setSkill(character.getId(), skill, args[1]);
      event.reply("Set skill roll for " + skill + " to `" + args[1] + "`");
    });
    event.subCommand("reset", bonusEvent -> {
      String[] args = bonusEvent.requireArguments(1);
      Skill skill = Skill.matchSkill(args[0]);
      if (skill == null) {
        event.reply("Unknown skill: " + args[0]);
        return;
      }
      String roll = skill.getAbility().name().substring(0, 3);
      dao.setSkill(character.getId(), skill, null);
      event.reply("Set skill roll for " + skill + " to `" + roll + "`");
    });

    event.usage();
  }
}
