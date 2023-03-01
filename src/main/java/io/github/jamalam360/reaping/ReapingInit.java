/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.reaping;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.jamalam360.jamlib.compatibility.JamLibCompatibilityModuleHandler;
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.villager.api.TradeOfferHelper;

public class ReapingInit implements ModInitializer, ModMenuApi {

    public static final String MOD_ID = "reaping";
    public static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);

    @Override
    public void onInitialize(ModContainer mod) {
        JamLibConfig.init(MOD_ID, Config.class);
        JamLibRegistry.register(ModRegistry.class);
        registerTrades();
        JamLibCompatibilityModuleHandler.initialize(MOD_ID);

        LOGGER.logInitialize();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> JamLibConfig.getScreen(parent, MOD_ID);
    }

    public static Identifier idOf(String path) {
        return new Identifier(MOD_ID, path);
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(
              VillagerProfession.BUTCHER,
              2,
              factories -> factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 3),
                    ItemStack.EMPTY,
                    ModRegistry.IRON_REAPER.getDefaultStack(),
                    5,
                    10,
                    1
              ))
        );

        TradeOfferHelper.registerVillagerOffers(
              VillagerProfession.BUTCHER,
              5,
              factories -> factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 13),
                    ItemStack.EMPTY,
                    ModRegistry.DIAMOND_REAPER.getDefaultStack(),
                    3,
                    30,
                    1
              ))
        );

        TradeOfferHelper.registerWanderingTraderOffers(
              1,
              factories -> factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 7),
                    ItemStack.EMPTY,
                    ModRegistry.HUMAN_MEAT.getDefaultStack(),
                    6,
                    20,
                    1
              ))
        );
    }

    public static class Config extends JamLibConfig {
        @Entry
        public static boolean enableDispenserBehavior = true;

        @Entry
        public static boolean reapPlayers = true;
    }
}
