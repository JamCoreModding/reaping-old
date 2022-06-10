package io.github.jamalam360.reaping.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

/**
 * @author Jamalam360
 */

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
@Config(name = "reaping")
public class ReapingConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    public boolean enableDispenserBehavior = true;
    @ConfigEntry.Gui.Tooltip
    public boolean reapPlayers = true;
}
