package io.github.jamalam360.reaping.forge;

import io.github.jamalam360.reaping.ReapingMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ReapingVillagerTrades {
    @SubscribeEvent
    public static void addReaperTradeVillager(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.BUTCHER) {
            event.getTrades().get(2).add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 3),
                    ReapingMod.ITEMS.getRegistrar()
                            .get(new Identifier(ReapingMod.MOD_ID, "iron_reaper"))
                            .getDefaultStack(),
                    2,
                    8,
                    0.2f
            ));

            event.getTrades().get(5).add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 13),
                    ReapingMod.ITEMS.getRegistrar()
                            .get(new Identifier(ReapingMod.MOD_ID, "diamond_reaper"))
                            .getDefaultStack(),
                    1,
                    12,
                    0.2f
            ));
        }
    }

    @SubscribeEvent
    public static void addReaperTradeWanderingTrader(WandererTradesEvent event) {
        event.getRareTrades().add((entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, 7),
                ReapingMod.ITEMS.getRegistrar()
                        .get(new Identifier(ReapingMod.MOD_ID, "human_meat"))
                        .getDefaultStack(),
                4,
                5,
                0.2f
        ));
    }
}
