package io.github.jamalam360.reaping.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

/**
 * @author Jamalam360
 */

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
@Config(name = "reaping")
public class ReapingConfig implements ConfigData {
    public boolean enableDispenserBehavior = true;
    public boolean damageAnimals = true;
    public boolean dropXp = true;
    public boolean reapBabies = true;
    public boolean reapPlayers = true;
    public int deathChance = 10;

    @Override
    public void validatePostLoad() throws ValidationException {
        if (deathChance < 0 || deathChance > 100) {
            throw new ValidationException("deathChance must be between 0 and 100");
        }
    }
}
