package io.github.jamalam360.reaping.forge;

import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.village.VillagerProfession;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ReapingVillagerTrades {
    @SubscribeEvent
    public static void addReaperTradeVillager(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.BUTCHER) {
            event.getTrades().get(2).add((entity, random) -> new BasicItemListing(
                    3,
                    ReapingMod.IRON_REAPER.getDefaultStack(),
                    5,
                    10
            ).create(entity, random));

            event.getTrades().get(5).add((entity, random) -> new BasicItemListing(
                    13,
                    ReapingMod.DIAMOND_REAPER.getDefaultStack(),
                    3,
                    30
            ).create(entity, random));
        }
    }

    @SubscribeEvent
    public static void addReaperTradeWanderingTrader(WandererTradesEvent event) {
        event.getRareTrades().add((entity, random) -> new BasicItemListing(
                7,
                ReapingMod.HUMAN_MEAT.getDefaultStack(),
                3,
                30
        ).create(entity, random));
    }
}
