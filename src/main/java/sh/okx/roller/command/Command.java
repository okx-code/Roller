package sh.okx.roller.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sh.okx.roller.Roller;

@RequiredArgsConstructor
public abstract class Command {
  protected final Roller bot;
  @Getter
  protected final String name;
  @Getter
  protected String[] aliases = new String[0];
  @Getter
  protected String description;
  @Getter
  protected String usage = "";
  @Getter
  protected boolean ownerOnly;

  public String[] getAllNames() {
    String[] names = new String[aliases.length + 1];
    names[0] = name;
    for(int i = 0; i < aliases.length; i++) {
      names[i + 1] = aliases[i];
    }
    return names;
  }

  public abstract void onSend(CommandEvent event);
}
