package io.github.jamalam360.reaping.fabriclike;

import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

/**
 * @author Jamalam360
 */

@Config(name = "reaping")
public class ReapingConfigFabricLike implements ConfigData, ReapingConfig {
    private boolean enableDispenserBehavior = true;
    private boolean damageAnimals = true;
    private boolean dropXp = true;
    private boolean reapBabies = true;

    @Override
    public boolean enableDispenserBehavior() {
        return this.enableDispenserBehavior;
    }

    @Override
    public boolean damageAnimals() {
        return this.damageAnimals;
    }

    @Override
    public boolean dropXp() {
        return this.dropXp;
    }

    @Override
    public boolean reapBabies() {
        return this.reapBabies;
    }
}
