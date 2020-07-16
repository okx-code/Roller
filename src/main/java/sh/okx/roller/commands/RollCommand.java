package sh.okx.roller.commands;

import java.security.SecureRandom;
import java.util.Comparator;
import sh.okx.roller.Roller;
import sh.okx.roller.command.Command;
import sh.okx.roller.command.CommandEvent;

public class RollCommand extends Command {

    private final SecureRandom random = new SecureRandom();

    public RollCommand(Roller bot) {
        super(bot, "roll");
        this.usage = "<dice = 1d20 + x>";
        this.description = "Roll D&D dice";
    }

    /*
     * Roll command usage in Backus-Naur grammar
     * <param> ::= <dice> | <dice> <addendum>
     * <dice> ::= <number> "d" <number> | "d" <number>
     * <addendum> ::= <opt-whitespace> "+" <opt-whitespace> <number>
     * <opt-whitespace> ::= " " <opt-whitespace> | ""
     * <number> ::= <digit> | <digit> <number>
     * <digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
     */
    @Override
    public void onSend(CommandEvent event) {
        String dice = event.getArguments();

        String[] add = dice.split("\\+");

        int max = 1;
        int min = 1000;
        int total = 0;
        StringBuilder rollsString = new StringBuilder();
        for (int k = 0; k < add.length; k++) {
            String part = add[k].trim();
            try {
                int i = Integer.parseInt(part);
                total += i;
                rollsString.append("+ ").append(i).append(" ");
                if (k != add.length - 1) {
                    rollsString.append("+ ");
                }
                continue;
            } catch (NumberFormatException ignored) {
            }

            if (part.startsWith("adv ")) {
                String[] s = part.split(" ");
                int sides = event.requirePositiveInt(s[1]);
                int a = random.nextInt(sides) + 1;
                int b = random.nextInt(sides) + 1;

                int high = Math.max(a, b);
                rollsString.append("adv {").append(a).append(", ").append(b).append("}=")
                    .append(high);
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
                rollsString.append("dis {").append(a).append(", ").append(b).append("}=")
                    .append(low);
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

            rollsString.append("[");
            for (int i = 0; i < top; i++) {
                total += rolls[i];
                if (rolls[i] < min) {
                    min = rolls[i];
                }
                if (rolls[i] > max) {
                    max = rolls[i];
                }
                if (i > 0) {
                    rollsString.append(", ");
                }
                rollsString.append(rolls[i]);
            }

            if (top < times) {
                rollsString.append(" / ");
                for (int i = top; i < rolls.length; i++) {
                    if (i > top) {
                        rollsString.append(", ");
                    }
                    rollsString.append(rolls[i]);
                }
            }

            rollsString.append("] ");
        }

        event.reply(event.getUser().getName() + ", "
            + "Roll: `" + rollsString.toString().trim() + "`, "
//            + "Max: `" + max + "`\n"
//            + "Min: `" + min + "`\n"
            + "Result: `" + total + "`");
    }
}
