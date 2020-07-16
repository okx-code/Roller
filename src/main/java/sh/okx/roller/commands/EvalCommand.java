package sh.okx.roller.commands;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import run.tio.java.TIO;
import sh.okx.roller.Roller;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.util.Util;

public class EvalCommand extends Command {
  private TIO tio = new TIO();

  public EvalCommand(Roller bot) {
    super(bot, "eval");
    this.description = "Evaluate code in node.js.";
    this.usage = "<code>";
    this.aliases = new String[] {"node", "nodejs", "js", "javascript"};
  }

  @Override
  public void onSend(CommandEvent event) {
    String code = event.requireArguments(1)[0];

    try {
      TIO.Result result = tio.run("bash", "node -p", code);

      EmbedBuilder embed = new EmbedBuilder()
          .setTitle("Evaluation")
          .addField("Input", "```javascript\n" + Util.restrictLength(code, 1000) + "```", true)
          .setFooter("Took " + Util.format(result.getRealMillis(), TimeUnit.MILLISECONDS), null)
          .setColor(Color.RED);
      String debug = result.getDebug();
      String output = result.getOutput();
      if(!output.isEmpty() || debug.isEmpty()) {
        embed.addField("Output", "```\n" + Util.restrictLength(output, 1000) + "```", true);
      }
      if(!debug.isEmpty()) {
        embed.addField("Error", "```\n" + Util.restrictLength(debug, 1000) + "```", true);
      }
      if(result.getState() != TIO.Result.State.SUCCESS) {
        embed.addField("Warning", result.getState().getMessage(), false);
      }
      event.reply(embed.build());
    } catch (IOException e) {
      e.printStackTrace();
      event.reply("An IOException occurred.");
    }
  }
}
