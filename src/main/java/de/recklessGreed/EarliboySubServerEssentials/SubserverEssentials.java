package de.recklessGreed.EarliboySubServerEssentials;

import de.recklessGreed.EarliboySubServerEssentials.Events.ClientTickEvent;
import de.recklessGreed.EarliboySubServerEssentials.Events.EntityInteractEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("subserveressentials")
public class SubserverEssentials {

    public static final Logger LOGGER = LogManager.getLogger();
    static final String MODID = "mod_subserver_essentials";

    /**
     * Allowed Items to check Horsestats
     */
    public static final Item[] allowed = new Item[] {Items.STICK, Items.SADDLE, Items.LEAD, Items.OAK_SIGN, Items.JUNGLE_SIGN, Items.BIRCH_SIGN, Items.DARK_OAK_SIGN};

    public Minecraft minecraftClient;
    public PlayerEntity player;
    public long castScheduledAt = 0L;
    public long startedReelDelayAt = 0L;
    public long startedCastDelayAt = 0L;
    public boolean isFishing = false;
    public long closeWaterWakeDetectedAt = 0L;
    //    public long exactWaterWakeDetectedAt = 0L;
    public long xpLastAddedAt = 0L;
    public long closeBobberSplashDetectedAt = 0L;
//    public long exactBobberSplashDetectedAt = 0L;

    public static final int TICKS_PER_SECOND = 20;

    /** How long to suppress checking for a bite after starting to reel in.  If we check for a bite while reeling
     in, we may think we have a bite and try to reel in again, which will actually cause a re-cast and lose the fish */
    public static final int REEL_TICK_DELAY = 15;

    /** How long to wait after casting to check for Entity Clear.  If we check too soon, the hook entity
     isn't in the world yet, and will trigger a false alarm and cause infinite recasting. */
    public static final int CAST_TICK_DELAY = 20;

    /** When Break Prevention is enabled, how low to let the durability get before stopping or switching rods */
    public static final int AUTOFISH_BREAKPREVENT_THRESHOLD = 2;

    /** The threshold for vertical movement of the fish hook that determines when a fish is biting, if using
     the movement method of detection.
     and the movement threshold that, combined with other factors, is a probable indicator that a fish is biting */
    public static final double MOTION_Y_THRESHOLD = -0.05d;
    public static final double MOTION_Y_MAYBE_THRESHOLD = -0.03d;

    /** The number of ticks to set as the "catchable delay" when Fast Fishing is enabled. *
     * (Vanilla ticksCatchableDelay is random between 20 and 80, but we seem to have trouble catching
     * it if it is less than 40) **/
    public static final int FAST_FISH_CATCHABLE_DELAY_TICKS = 40;
    public static final int FAST_FISH_DELAY_VARIANCE = 40;

    /** The maximum number of ticks that is is reasonable for a fish hook to be flying in the air after a cast */
    public static final int MAX_HOOK_FLYING_TIME_TICKS = 120;

    /** The amount of time to wait for a fish before something seems wrong and we want to recast **/
    public static final int MAX_WAITING_TIME_SECONDS = 60;

    /** The distance (squared) threshold for determining that a water wake is "close" to the fish Hook
     * and "most certainly at" the fish Hook **/
    public static final double CLOSE_WATER_WAKE_THRESHOLD = 1.0d;
//    public static final double EXACT_WATER_WAKE_THRESHOLD = 0.3d;

    /** The number of ticks to wait after detecting a "close" or "exact" water wake before reeling in **/
    public static final int CLOSE_WATER_WAKE_DELAY_TICKS = 30;
//    public static final int EXACT_WATER_WAKE_DELAY_TICKS = 20;

    /** The distance (squared) threshold for determining that a bobber splash sound is "close" to the fish Hook
     * and "most certainly at" the fish Hook **/
    public static final double CLOSE_BOBBER_SPLASH_THRESHOLD = 2.0d;
//    public static final double EXACT_BOBBER_SPLASH_THRESHOLD = 0.5d;

    public static SubserverEssentials instance;

    public enum HorseArray {OWNER, VARIANT, HEALTH, JUMP_HEIGHT, SPEED, }

    public SubserverEssentials() {
        instance = this;
        LOGGER.info("Starting SubserverEssentials");

        this.minecraftClient = Minecraft.getInstance();

        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
        //MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EntityInteractEvent(instance));
        MinecraftForge.EVENT_BUS.register(new ClientTickEvent(instance));


    }

    @SubscribeEvent
    public void clientInit (final FMLClientSetupEvent event) {
        LOGGER.info("(client) Initializing " + SubserverEssentials.MODID);
        MinecraftForge.EVENT_BUS.register(EntityInteractEvent.class);
    }

    public boolean debugMode() {
        return player != null && player.getHeldItemOffhand().getItem() == Items.ROTTEN_FLESH;
    }

    public void sendDebugMessage(String message) {
        try {
            if (player != null && !minecraftClient.isGamePaused() && player.getHeldItemOffhand().getItem() == Items.ROTTEN_FLESH) {
                player.sendMessage(new TranslationTextComponent(message));
            }
        }
        catch (NullPointerException nullex) {
            LOGGER.error("Nullpointer Exception while trying to send Message");
        }
    }
}
