package sh.okx.roller.command;

import lombok.extern.java.Log;
import sh.okx.roller.Roller;

@Log
public class StopCommand extends Command {
  public StopCommand(Roller bot) {
    super(bot, "stop");
    this.description = "Stop the bot";
    this.ownerOnly = true;
  }

  @Override
  public void onSend(CommandEvent event) {
    event.reply("Stopping...");
    bot.getCommands().shutdownCommandThreads();
    bot.getJda().shutdown();

    Thread shutdown = new Thread(() -> {
      try {
        Thread.sleep(8000);
        log.severe("Could not shutdown within 8 seconds, forcing shutdown");
        System.exit(0);
      } catch (InterruptedException ignored) {
        // we shutdown so it's all good
      }
    }, "Shutdown");
    shutdown.setDaemon(true);
    shutdown.start();
  }
}
