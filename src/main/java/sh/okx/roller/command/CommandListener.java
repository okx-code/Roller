package sh.okx.roller.command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sh.okx.roller.Roller;
import sh.okx.roller.command.error.CommandFailedException;
import sh.okx.roller.command.error.CommandUnauthorizedException;
import sh.okx.roller.command.error.CommandUsageException;
import sh.okx.roller.util.JaroWinklerDistance;

@RequiredArgsConstructor
@Log
public class CommandListener extends ListenerAdapter {
  private final ExecutorService threads = Executors.newCachedThreadPool(new ThreadFactory() {
    private int count = 0;

    @Override
    public synchronized Thread newThread(Runnable runnable) {
      return new Thread(runnable, "Command-" + count++);
    }
  });
  @Getter
  private final List<Command> commands = new ArrayList<>();
  private final Roller roller;
  @Getter
  private final String prefix;

  public void shutdownCommandThreads() {
    threads.shutdown();
  }

  public void addCommand(Command command) {
    commands.add(command);
  }

  public Command getCommand(String name) {
    JaroWinklerDistance jaro = new JaroWinklerDistance();

    Command closest = null;
    double distance = -1;
    for (Command command : commands) {
      for (String commandName : command.getAllNames()) {
        double commandDistance = jaro.apply(commandName, name);
        if (commandDistance > distance) {
          closest = command;
          distance = commandDistance;
        }
      }
    }
    return closest;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    User user = event.getAuthor();
    if (user.isBot()) {
      return;
    }

    MessageChannel channel = event.getChannel();
    Message message = event.getMessage();

    String content = message.getContentDisplay();

    boolean mentioned = message.getContentRaw().startsWith(event.getJDA().getSelfUser().getAsMention());
    if (!content.startsWith(prefix) && !mentioned) {
      return;
    } else if (mentioned) {
      String[] mentionedParts = content.split(" ", 2);
      if (mentionedParts.length < 2) {
        return;
      }
      content = prefix + content.split(" ", 2)[1];
    }

    String[] parts = content.split(" ", 2);
    String commandName = parts[0];
    String arguments = parts.length > 1 ? parts[1] : "";

    for (Command command : commands) {
      for (String name : command.getAllNames()) {
        if (!commandName.equalsIgnoreCase(prefix + name)) {
          continue;
        }
        threads.execute(() -> {
          try {

            String in = event.isFromGuild() ? event.getGuild().getName() + " #" + channel.getName() : "DM";
            log.info("[" + in + "] " + user.getName() + ": <"
                + commandName + "> " + arguments);

            try {
              command.onSend(new CommandEvent(message, arguments));
            } catch (CommandUsageException usage) {
              channel.sendMessage("**Usage:** " + prefix + name + " " + command.getUsage()).queue();
            } catch (CommandUnauthorizedException unauthorized) {
              channel.sendMessage("You need the **" + unauthorized.getPermission()
                  + "** permission to run that command.").queue();
            } catch (CommandFailedException ignored) {
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        });
      }
    }
  }
}
