package de.recklessGreed.EarliboySubServerEssentials;

import de.recklessGreed.EarliboySubServerEssentials.Events.EntityInteractListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("subserveressentials")
public class SubserverEssentials {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mod_subserver_essentials";

    public static SubserverEssentials instance;

    public SubserverEssentials() {
        instance = this;
        LOGGER.info("Starting SubserverEssentials");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        MinecraftForge.EVENT_BUS.register(this);


    }

    @SubscribeEvent
    public void clientInit (final FMLClientSetupEvent event) {
        LOGGER.info("(client) Initializing " + SubserverEssentials.MODID);
        MinecraftForge.EVENT_BUS.register(EntityInteractListener.class);


    }
}
