package sh.okx.roller.command.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommandUnauthorizedException extends CommandFailedException {
  private final String permission;
}
