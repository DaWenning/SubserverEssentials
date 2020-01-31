package de.recklessGreed.EarliboySubServerEssentials.Events;

import de.recklessGreed.EarliboySubServerEssentials.SubserverEssentials;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerLeaveEvent {

    SubserverEssentials essentials;

    public ServerLeaveEvent(SubserverEssentials essentials) {
        this.essentials = essentials;
    }

    @SubscribeEvent
    public void onServerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        essentials.player = null;

    }
}
