package de.recklessGreed.EarliboySubServerEssentials;

import de.recklessGreed.EarliboySubServerEssentials.Events.EntityInteractListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("subserveressentials")
public class SubserverEssentials {

    private static final Logger LOGGER = LogManager.getLogger();

    public SubserverEssentials() {
        LOGGER.info("Starting SubserverEssentials");
        MinecraftForge.EVENT_BUS.register(EntityInteractListener.class);
        LOGGER.info("Registered SubserverEssentials EntityInteract Class");

    }
}
