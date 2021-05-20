package sh.okx.roller.commands;

import net.dv8tion.jda.api.entities.User;
import sh.okx.roller.Roller;
import sh.okx.roller.character.CharacterContext;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;
import sh.okx.roller.compiler.Compiler;
import sh.okx.roller.compiler.ast.AstNode;
import sh.okx.roller.compiler.result.NodeResult;

public class RollCommand extends Command {
    private static final Compiler compiler = new Compiler();

    public RollCommand(Roller bot) {
        super(bot, "roll");
        this.aliases = new String[] {"r"};
        this.usage = "<dice = 1d20 + x>";
        this.description = "Roll D&D dice";
    }

    @Override
    public void onSend(CommandEvent event) {
        String dice = event.getArguments();

        User user = event.getUser();
        NodeResult result;
        try {
            AstNode compile = compiler.compile(
                    new CharacterContext(bot.getCharacterDao(), user.getIdLong()),
                    dice);
            result = compile.evaluate();
        } catch (RuntimeException ex){
            event.reply("Error: " + ex.getMessage());
            return;
        }

        String name = user.getName();
        event.reply(name + ", "
                + "Roll: `" + result.toHumanReadable() + "`, "
                + "Result: `" + result.result() + "`");
    }

    /*@Override
    public void onSend(CommandEvent event) {
        String dice = event.getArguments();

        String[] add = dice.split("\\+");

        int max = 1;
        int min = 1000;
        int total = 0;
        List<String> sp = new ArrayList<>(add.length);
        for (int k = 0; k < add.length; k++) {
            String part = add[k].trim();
            try {
                int i = Integer.parseInt(part);
                total += i;
                sp.add(Integer.toString(i));
                continue;
            } catch (NumberFormatException ignored) {
            }

            if (part.startsWith("adv ")) {
                String[] s = part.split(" ");
                int sides = event.requirePositiveInt(s[1]);
                int a = random.nextInt(sides) + 1;
                int b = random.nextInt(sides) + 1;

                int high = Math.max(a, b);
                sp.add("adv {" + a + ", " + b + "}=" + high);
                if (high < min) {
                    min = high;
                }
                if (high > max) {
                    max = high;
                }
                total += high;
                continue;
            } else if (part.startsWith("dis ")) {
                String[] s = part.split(" ");
                int sides = event.requirePositiveInt(s[1]);
                int a = random.nextInt(sides) + 1;
                int b = random.nextInt(sides) + 1;

                int low = Math.min(a, b);
                sp.add("dis {" + a + ", " + b + "}=" + low);
                if (low < min) {
                    min = low;
                }
                if (low > max) {
                    max = low;
                }
                total += low;
                continue;
            }

            String[] parts = part.split("d");
            if (parts.length != 2) {
                event.error("Invalid dice part: " + part);
            }

            String first = parts[0].trim();
            String[] div = first.split("/");

            int top;
            int times;
            if (div.length == 2) {
                top = event.requirePositiveInt(div[0]);
                times = event.requirePositiveInt(div[1]);
            } else {
                times = first.isEmpty() ? 1 : event.requirePositiveInt(first);
                top = times;
            }

            int sides = event.requireInt(parts[1].trim());

            if (times > 200) {
                event.error("Dice must be at most 200.");
            } else if (sides > 1000) {
                event.error("Sides must be at most 1000.");
            }

            int[] rolls = random.ints(1, sides + 1).limit(times).boxed()
                .sorted(Comparator.reverseOrder())
                .mapToInt(Integer::intValue)
                .toArray();

            StringBuilder sa = new StringBuilder("[");
            for (int i = 0; i < top; i++) {
                total += rolls[i];
                if (rolls[i] < min) {
                    min = rolls[i];
                }
                if (rolls[i] > max) {
                    max = rolls[i];
                }
                if (i > 0) {
                    sa.append(", ");
                }
                sa.append(rolls[i]);
            }

            if (top < times) {
                sa.append(" / ");
                for (int i = top; i < rolls.length; i++) {
                    if (i > top) {
                        sa.append(", ");
                    }
                    sa.append(rolls[i]);
                }
            }

            sa.append("]");
            sp.add(sa.toString());
        }

        User user = event.getUser();
        String name = user.getName();
        log.info(name + ": " + event.getMessage().getContentDisplay());
        event.reply(name + ", "
            + "Roll: `" + String.join(" + ", sp) + "`, "
//            + "Max: `" + max + "`\n"
//            + "Min: `" + min + "`\n"
            + "Result: `" + total + "`");
    }*/
}
