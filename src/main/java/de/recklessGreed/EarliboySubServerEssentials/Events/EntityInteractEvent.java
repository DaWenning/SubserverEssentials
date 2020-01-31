package de.recklessGreed.EarliboySubServerEssentials.Events;

import de.recklessGreed.EarliboySubServerEssentials.HorseStats;
import de.recklessGreed.EarliboySubServerEssentials.SubserverEssentials;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class EntityInteractEvent {

    SubserverEssentials essentials;

    public EntityInteractEvent(SubserverEssentials essentials) {
        this.essentials = essentials;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void EntityInteractEvent(PlayerInteractEvent.EntityInteract event) {
        if (event.getPlayer() != null && event.getWorld() != null && event.getTarget() instanceof AbstractHorseEntity) {

            PlayerEntity player = event.getPlayer();
            AbstractHorseEntity horseEntity = (AbstractHorseEntity) event.getTarget();
            if (player.isCrouching()) {
                Item main = player.getHeldItemMainhand().getItem();
                for (Item a : essentials.allowed) {
                    if (main.equals(a)) {
                        String[] result = new HorseStats().getHorseStats(horseEntity);
                        for (String s : result) {
                            if (s != null && !s.isEmpty())
                                player.sendMessage(new TranslationTextComponent(s));
                        }
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        event.setResult(Event.Result.DENY);
                        event.setCanceled(true);

                        return;
                    }
                }
            }
        }
    }



}
