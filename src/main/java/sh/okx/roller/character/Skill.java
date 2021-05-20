package sh.okx.roller.character;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sh.okx.roller.util.JaroWinklerDistance;

@RequiredArgsConstructor
@Getter
public enum Skill {
  ACROBATICS("Acrobatics", Ability.DEXTERITY),
  ANIMAL_HANDLING("Animal Handling", Ability.WISDOM),
  ARCANA("Arcana", Ability.INTELLIGENCE),
  ATHLETICS("Athletics", Ability.STRENGTH),
  DECEPTION("Deception", Ability.CHARISMA),
  HISTORY("History", Ability.INTELLIGENCE),
  INSIGHT("Insight", Ability.WISDOM),
  INTIMIDATION("Intimidation", Ability.CHARISMA),
  INVESTIGATION("Investigation", Ability.INTELLIGENCE),
  MEDICINE("Medicine", Ability.WISDOM),
  NATURE("Nature", Ability.INTELLIGENCE),
  PERCEPTION("Perception", Ability.WISDOM),
  PERFORMANCE("Performance", Ability.CHARISMA),
  PERSUASION("Persuasion", Ability.CHARISMA),
  RELIGION("Religion", Ability.INTELLIGENCE),
  SLEIGHT_OF_HAND("Sleight of Hand", Ability.DEXTERITY),
  STEALTH("Stealth", Ability.DEXTERITY),
  SURVIVAL("Survival", Ability.WISDOM);

  private static final JaroWinklerDistance jwd = new JaroWinklerDistance();

  private final String name;
  private final Ability ability;

  public static Skill matchSkill(String string) {
    Skill closestSkill = null;
    double closestMatch = 0.7;

    for (Skill skill : values()) {
      double match = jwd.apply(skill.getName(), string);
      if (match > closestMatch) {
        closestMatch = match;
        closestSkill = skill;
      }
    }

    return closestSkill;
  }
}
