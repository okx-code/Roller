package sh.okx.roller;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.jetbrains.annotations.NotNull;
import sh.okx.roller.character.Skill;
import sh.okx.roller.commands.CheckCommand;

public class SlashCommandHandler extends ListenerAdapter {
  private final CheckCommand checkCommand;

  public SlashCommandHandler(CheckCommand checkCommand) {
    this.checkCommand = checkCommand;
  }

  @Override
  public void onReady(@NotNull ReadyEvent event) {
    CommandUpdateAction commands = event.getJDA().getGuildById(480324285798547457L).updateCommands();

    OptionData data = new OptionData(OptionType.STRING, "skill", "The skill to check")
        .setRequired(true);
    for (Skill skill : Skill.values()) {
      data.addChoice(skill.getName(), skill.name());
    }
    commands.addCommands(new CommandData("check", "Make a skill check")
        .addOption(data));

    commands.queue();
  }

  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    if (event.getName().equals("check")) {
      String strSkill = event.getOption("skill").getAsString();
      Skill skill = Skill.valueOf(strSkill);

      checkCommand.checkCommand(e -> event.reply(e).queue(), event.getUser(), skill);
    }
  }
}
