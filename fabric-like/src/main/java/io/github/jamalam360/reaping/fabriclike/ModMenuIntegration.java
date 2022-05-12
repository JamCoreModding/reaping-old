package io.github.jamalam360.reaping.fabriclike;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.jamalam360.reaping.config.ReapingConfig;
import me.shedaniel.autoconfig.AutoConfig;

/**
 * @author Jamalam360
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ReapingConfig.class, parent).get();
    }
}
