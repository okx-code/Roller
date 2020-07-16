package sh.okx.roller.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {
  private static final DecimalFormat numberFormat = new DecimalFormat("#,###.#");
  private static final DecimalFormat percentFormat = new DecimalFormat("#.###%");
  private static final Map<TimeUnit, String> timeUnits = new LinkedHashMap<>();

  static {
    timeUnits.put(TimeUnit.DAYS, "d");
    timeUnits.put(TimeUnit.HOURS, "h");
    timeUnits.put(TimeUnit.MINUTES, "m");
    timeUnits.put(TimeUnit.SECONDS, "s");
    timeUnits.put(TimeUnit.MILLISECONDS, "ms");
    timeUnits.put(TimeUnit.MICROSECONDS, "μs");
    timeUnits.put(TimeUnit.NANOSECONDS, "ns");
  }

  public static String formatPercent(double number) {
    return percentFormat.format(number);
  }

  public static String formatNumber(double number) {
    String suffix = "";
    if (number > 1000) {
      suffix = "k";
      number /= 1000;
    }
    return numberFormat.format(number) + suffix;
  }

  public static String formatNumberExact(double number) {
    return numberFormat.format(number);
  }

  public static String formatMemory(long bytes) {
    String suffix = "";
    if (bytes >= 1_000_000_000) {
      bytes /= 1_000_000_000;
      suffix = "GB";
    } else if (bytes >= 1_000_000) {
      bytes /= 1_000_000;
      suffix = "MB";
    } else if (bytes >= 1_000) {
      bytes /= 1_000;
      suffix = "kB";
    }
    return numberFormat.format(bytes) + suffix;
  }

  /**
   * Formats a given numeric time in the given time int to a string with a certain precision
   */
  public static String format(long time, TimeUnit input, TimeUnit precision) {
    AtomicLong nanos = new AtomicLong(input.toNanos(time));
    List<String> times = new ArrayList<>();
    for (Map.Entry<TimeUnit, String> entry : timeUnits.entrySet()) {
      TimeUnit unit = entry.getKey();
      if (nanos.get() >= unit.toNanos(1) && precision.compareTo(unit) <= 0) {
        times.add(getTime(nanos, unit, entry.getValue()));
      }
    }
    return String.join(" ", times);
  }

  /**
   * Formats assuming the given time is in milliseocnds
   *
   * @see Util#format(long, TimeUnit, TimeUnit)
   */
  public static String format(long time, TimeUnit precision) {
    return Util.format(time, TimeUnit.MILLISECONDS, precision);
  }

  private static String getTime(AtomicLong time, TimeUnit unit, String suffix) {
    long nanos = unit.toNanos(1);
    if (time.get() >= nanos) {
      long amount = time.get() / nanos;
      time.set(time.get() % nanos);
      return amount + suffix;
    }
    return "";
  }

  public static String bold(String string) {
    return "**" + string.replace("`", "\\`").replace("*", "\\*") + "**";
  }

  public static String restrictLength(String string, int length) {
    if (string.length() <= length) {
      return string;
    }

    return string.substring(0, length - 3) + "...";
  }

  @SafeVarargs
  public static <T> T random(T... array) {
    return array[ThreadLocalRandom.current().nextInt(array.length)];
  }

  public static String charToReaction(char c) {
    switch (c) {
      case '0':
        return "0⃣";
      case '1':
        return "1⃣";
      case '2':
        return "2⃣";
      case '3':
        return "3⃣";
      case '4':
        return "4⃣";
      default:
        return null;
    }
  }

  public static String toRomanNumeral(int i) {
    switch(i) {
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      default:
        return null;
    }
  }
}
