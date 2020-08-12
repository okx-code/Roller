package sh.okx.roller.commands;

import sh.okx.roller.Roller;
import sh.okx.roller.character.Character;
import sh.okx.roller.character.CharacterContext;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.compiler.Compiler;
import sh.okx.roller.compiler.Util;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.result.NodeResult;

public class InitiativeCommand extends Command {
    private static final Compiler compiler = new Compiler();

    public InitiativeCommand(Roller bot) {
        super(bot, "initiative");
        this.description = "Roll for initiative";
        this.usage = "[new initiative roll]";
        this.aliases = new String[] {"init", "i"};
    }

    @Override
    public void onSend(CommandEvent event) {
        String[] opt = event.optionalArguments(1);
        Character character = bot.getCharacterDao().getCharacter(event.getUserId());
        if (opt.length > 0) {
            String dice = opt[0];
            if (character == null) {
                event.reply("You must select a character to set its initiative.");
            } else {
                bot.getCharacterDao().setInitiative(character.getId(), dice);
                event.reply("Set initiative roll to: `" + dice + "`");
            }
            return;
        }

        String initiative = character.getInitiative();
        boolean changed = false;

        if (initiative == null) {
            changed = true;
            initiative = "d20 + DEX";
        }

        NodeResult result;
        try {
            CharacterContext ctx = new CharacterContext(bot.getCharacterDao(), event.getUserId());
            ctx.setCharacter(character);
            AstNode compile = compiler.compile(ctx, initiative);
            result = compile.evaluate();
        } catch (IllegalArgumentException ex){
            event.reply("Error: " + ex.getMessage());
            return;
        }

        String name = event.getUser().getName();
        event.reply(name + " "
                + (changed ? "(using default initiative of d20 + DEX)" : "")
                + ", Roll: `" + result.toHumanReadable() + "`, "
                + "Result: `" + Util.sum(result.array()) + "`");
    }
}
