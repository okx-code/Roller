package sh.okx.roller;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.commands.RollCommand;

public class RollShortcutCommand extends ListenerAdapter {
  private final RollCommand command;

  public RollShortcutCommand(RollCommand command) {
    this.command = command;
  }

  @Override
  public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
    User user = event.getAuthor();
    if (user.isBot()) {
      return;
    }

    String contents = event.getMessage().getContentDisplay();
    if (contents.length() < 3) {
      return;
    }

    char digit = contents.charAt(2);
    if (!contents.substring(0, 2).equalsIgnoreCase(",d") || digit < '0' || digit > '9') {
      return;
    }

    this.command.onSend(new CommandEvent(event.getMessage(), contents.substring(1)));
  }
}
