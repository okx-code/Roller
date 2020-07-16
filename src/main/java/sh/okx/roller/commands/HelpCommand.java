package sh.okx.roller.commands;

import java.awt.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import sh.okx.roller.Roller;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.command.CommandListener;

public class HelpCommand extends Command {
  private final CommandListener listener;

  public HelpCommand(Roller bot) {
    super(bot, "help");
    this.listener = bot.getCommands();
    this.description = "View a list of commands and their descriptions.";
    this.usage = "[command]";
  }

  @Override
  public void onSend(CommandEvent event) {
    String[] args = event.optionalArguments(1);
    String prefix = bot.getCommands().getPrefix();
    if (args.length > 0) {
      Command command = listener.getCommand(args[0]);
      EmbedBuilder embed = new EmbedBuilder()
          .setTitle(prefix + command.getName())
          .setDescription(command.getDescription())
          .setColor(Color.ORANGE);
      if (!command.getUsage().isEmpty()) {
        embed.setFooter(prefix + command.getName() + " " + command.getUsage(), null);
      }

      String[] aliases = command.getAliases();
      if (aliases.length > 0) {
        embed.addField("Aliases", String.join(", ", aliases), false);
      }

      event.reply(embed.build());
      return;
    }

    if (event.inGuild()) {
      event.getUser().openPrivateChannel().queue(channel -> {
        event.reply("Sent help in direct messages.");
        sendCommands(prefix, channel, event.getUser(), event.getGuild().getIdLong());
      }, e -> event.reply("Could not send help in direct messages. Do you have DMs enabled?"));
    } else {
      event.getUser().openPrivateChannel().queue(channel -> sendCommands(prefix, channel, event.getUser(), -1));
    }
  }

  private void sendCommands(String prefix, PrivateChannel channel, User user, long guild) {
    StringBuilder message = new StringBuilder();
    for (Command command : listener.getCommands()) {
      if (message.length() > 0) {
        message.append("\n");
      }
      message.append("**")
          .append(prefix)
          .append(command.getName());

      if (!command.getUsage().isEmpty()) {
        message.append(" ")
            .append(command.getUsage());
      }
      message.append("**")
          .append(" ")
          .append(command.getDescription());
    }

    message.append("\n*You can also mention ")
        .append(bot.getJda().getSelfUser().getAsMention())
        .append(" instead of using the prefix*");
    channel.sendMessage(message).queue();
  }
}
