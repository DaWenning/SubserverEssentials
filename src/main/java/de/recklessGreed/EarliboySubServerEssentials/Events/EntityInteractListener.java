package de.recklessGreed.EarliboySubServerEssentials.Events;

import de.recklessGreed.EarliboySubServerEssentials.HorseStats;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



public class EntityInteractListener {

    static int overflow = 0;

    @SubscribeEvent
    public static void EntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
        if (event.getPlayer() != null && event.getWorld() != null && event.getTarget() instanceof AbstractHorseEntity) {

            PlayerEntity player = event.getPlayer();
            AbstractHorseEntity horseEntity = (AbstractHorseEntity) event.getTarget();
            if (player.isCrouching() && (player.getHeldItemMainhand().getItem() == Items.STICK || player.getHeldItemMainhand().getItem() == Items.SHIELD)) {
                player.sendMessage(new HorseStats().getHorseStats(horseEntity));
                event.setCancellationResult(ActionResultType.SUCCESS);
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }

}
