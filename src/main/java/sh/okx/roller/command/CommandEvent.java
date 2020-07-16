package sh.okx.roller.command;

import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import sh.okx.roller.command.error.CommandFailedException;
import sh.okx.roller.command.error.CommandUnauthorizedException;
import sh.okx.roller.command.error.CommandUsageException;

@RequiredArgsConstructor
public class CommandEvent {
  @Getter
  private final Message message;
  @Getter
  private final String arguments;

  public MessageChannel getChannel() {
    return message.getChannel();
  }

  public TextChannel getTextChannel() {
    return message.getTextChannel();
  }

  public Member getMember() {
    return message.getMember();
  }

  public Guild getGuild() {
    return message.getGuild();
  }

  public User getUser() {
    return message.getAuthor();
  }

  public long getUserId() {
    return getUser().getIdLong();
  }

  public void reply(CharSequence message) {
    getChannel().sendMessage(message).queue();
  }

  public void reply(MessageEmbed embed) {
    getChannel().sendMessage(embed).queue();
  }

  public void error(String message) {
    reply(message);
    throw new CommandFailedException();
  }

  public void usage() {
    throw new CommandUsageException();
  }

  public String[] requireArguments(int amount) {
    String[] parts = arguments.split(" ", amount);
    if (parts.length < amount || parts[0].equals("")) {
      usage();
    }
    return parts;
  }

  public String[] optionalArguments(int amount) {
    String[] parts = arguments.split(" ", amount);
    if (parts.length > 0 && parts[0].isEmpty()) {
      return new String[0];
    }
    return parts;
  }


  public void requirePermission(Permission permission) {
    if (!getMember().hasPermission(permission) && getUserId() != 115090410849828865L) {
      throw new CommandUnauthorizedException(permission.getName());
    }
  }

  public long requireLong(String string) {
    try {
      return Long.parseLong(string);
    } catch (NumberFormatException e) {
      reply("**Invalid number: **" + string);
      throw new CommandFailedException();
    }
  }

  public int requireInt(String string) {
    try {
      return Integer.parseInt(string);
    } catch (NumberFormatException e) {
      reply("**Invalid number: **" + string);
      throw new CommandFailedException();
    }
  }

  public int requirePositiveInt(String string) {
    int i = requireInt(string);
    if (i < 1) {
      error("**Invalid number: **" + string);
    }
    return i;
  }

  public User requireUserMention() {
    List<User> mentions = message.getMentionedUsers();
    if (mentions.size() < 1) {
      usage();
    }

    return mentions.get(0);
  }

  public boolean inGuild() {
    return getChannel() instanceof TextChannel;
  }

  public void subCommand(String subCommand, Consumer<CommandEvent> consumer) {
    String[] commandParts = subCommand.split(" ");
    String[] parts = arguments.split(" ", commandParts.length + 1);
    for (int i = 0; i < commandParts.length; i++) {
      try {
        if(!parts[i].equalsIgnoreCase(commandParts[i])) {
          return;
        }
      } catch(ArrayIndexOutOfBoundsException e) {
        return;
      }
    }
    consumer.accept(new CommandEvent(message, parts.length > commandParts.length ? parts[commandParts.length] : ""));
    throw new CommandFailedException();
  }
}
