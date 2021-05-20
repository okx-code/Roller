package sh.okx.roller.commands;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.User;
import sh.okx.roller.Roller;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterContext;
import sh.okx.roller.character.Skill;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.compiler.Compiler;
import sh.okx.roller.compiler.Util;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.result.NodeResult;

public class CheckCommand extends Command {
  private static final Compiler compiler = new Compiler();

  public CheckCommand(Roller bot) {
    super(bot, "check");
    this.aliases = new String[] {"chk"};
  }

  @Override
  public void onSend(CommandEvent event) {
    String skillName = event.requireArguments(1)[0];
    Skill skill = Skill.matchSkill(skillName);
    if (skill == null) {
      event.reply("Invalid skill: " + skillName);
      return;
    }

    checkCommand(event::reply, event.getUser(), skill);
  }

  public void checkCommand(Consumer<String> reply, User user, Skill skill) {
    Character character = bot.getCharacterDao().getCharacter(user.getIdLong());
    if (character == null) {
      reply.accept("You must create and select a character to use this command.\n"
          + "See `,help character` for usage.");
      return;
    }

    boolean changed = false;
    String roll = character.getSkill(skill);
    if (roll == null) {
      changed = true;
      roll = "d20 + " + skill.getAbility().name().substring(0, 3);
    }

    NodeResult result;
    try {
      CharacterContext ctx = new CharacterContext(bot.getCharacterDao(), user.getIdLong());
      ctx.setCharacter(character);
      AstNode compile = compiler.compile(ctx, roll);
      result = compile.evaluate();
    } catch (RuntimeException ex){
      reply.accept("Error: " + ex.getMessage());
      return;
    }

    String name = user.getName();
    reply.accept(name + ", " + skill.getName() + " "
        + (changed ? "(using default roll of " + roll + ")" : "(" + roll + ")")
        + ", Roll: `" + result.toHumanReadable() + "`, "
        + "Result: `" + Util.sum(result.array()) + "`");
  }
}
