package de.recklessGreed.EarliboySubServerEssentials.Events;

import de.recklessGreed.EarliboySubServerEssentials.SubserverEssentials;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.codehaus.plexus.util.ReflectionUtils;

import javax.swing.text.html.parser.Entity;

import static de.recklessGreed.EarliboySubServerEssentials.SubserverEssentials.*;

public class ClientTickEvent {

    SubserverEssentials essentials;

    public ClientTickEvent(SubserverEssentials essentials) {
        this.essentials = essentials;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (essentials.player == null || essentials.player != essentials.minecraftClient.player)
            essentials.player = essentials.minecraftClient.player;

        if (!essentials.minecraftClient.isGamePaused() && essentials.player != null){
            essentials.sendDebugMessage("Holding Rod: " + playerIsHoldingRod());
            if (playerIsHoldingRod()) {
                essentials.sendDebugMessage(getGameTime() +" | Hook: "+playerHookInWater() + " DuringHeel: " + !isDuringReelDelay() + " Biting: " + isFishBiting());
                if (playerHookInWater() && !isDuringReelDelay() && isFishBiting()) {
                    essentials.sendDebugMessage("pHIW && !iDRD && iFB");
                    startReelDelay();
                    reelIn();
                    scheduleNextCast();
                } else if (isTimeToCast()) {
                    essentials.sendDebugMessage("iTTC - "  + essentials.castScheduledAt + " - " + getGameTime());
                    if (rodIsCast()) {
                        essentials.sendDebugMessage("rIC");
                        startFishing();
                    }
                    resetReelDelay();
                    resetCastSchedule();
                    resetBiteTracking();
                }
                //checkForMissedBite();
            } else {
                resetReelDelay();
                resetCastSchedule();
                resetBiteTracking();
            }

        }
    }

    boolean playerIsHoldingRod() {
        Item item = essentials.player.getHeldItemMainhand().getItem();
        if (item == null)
            return false;
        return item instanceof FishingRodItem;
    }

    boolean playerHookInWater() {
        PlayerEntity player = essentials.player;

        if (player.fishingBobber == null)
            return false;
        BlockState bobberState = player.fishingBobber.getEntityWorld().getBlockState(new BlockPos(player.fishingBobber));
        BlockState belowBobber = player.fishingBobber.getEntityWorld().getBlockState(new BlockPos(player.fishingBobber.serverPosX, player.fishingBobber.serverPosY - 0.25, player.fishingBobber.serverPosZ));
        boolean bobberInWater = bobberState.getMaterial() == Material.WATER || belowBobber.getMaterial() == Material.WATER;
        return bobberInWater;

    }

    boolean isDuringReelDelay() {
        boolean returner = essentials.startedReelDelayAt != 0 && getGameTime() < essentials.startedReelDelayAt + essentials.REEL_TICK_DELAY;
        return returner;
    }

    /* BITE DETECTION*/

    boolean isFishBiting() {
        return isFishBiting_fromMovement() || isFishBiting_fromBobberSound() || isFishBiting_fromWaterWake() || isFishBiting_fromAll();
    }

    private boolean isFishBiting_fromBobberSound() {
        /** If a bobber sound has been played at the fish hook, a fish is already biting **/
        if (essentials.closeBobberSplashDetectedAt > 0) {
            essentials.LOGGER.debug("Detected bite by BOBBER_SPLASH");
            return true;
        }
        return false;
    }

    private boolean isFishBiting_fromWaterWake() {
        /** An water wake indicates a probable bite "very soon", so make sure enough time has passed **/
        if (essentials.closeWaterWakeDetectedAt > 0
                && getGameTime() > essentials.closeWaterWakeDetectedAt + CLOSE_WATER_WAKE_DELAY_TICKS) {
            essentials.LOGGER.debug("Detected bite by WATER_WAKE");
            return true;
        }
        return false;
    }

    private boolean isFishBiting_fromMovement() {
        FishingBobberEntity fishEntity = essentials.player.fishingBobber;
        if (fishEntity != null
                // Checking for no X and Z motion prevents a false alarm when the hook is moving through the air
                && fishEntity.getMotion().x == 0
                && fishEntity.getMotion().z == 0
                && fishEntity.getMotion().y < MOTION_Y_THRESHOLD) {
            essentials.LOGGER.debug("Detected bite by MOVEMENT");
            return true;
        }
        return false;
    }

    private boolean isFishBiting_fromAll() {
        /** Assume a bit if the following conditions are true:
         * (1) There is at least a little Y motion of the fish hook
         * (2) Either (a) There has been a "close" bobber splash very recently; OR
         *            (b) A "close" water wake was detected long enough ago
         */
        FishingBobberEntity fishEntity = essentials.player.fishingBobber;
        if (fishEntity != null
                // Checking for no X and Z motion prevents a false alarm when the hook is moving through the air
                && fishEntity.getMotion().x == 0
                && fishEntity.getMotion().z == 0
                && fishEntity.getMotion().y < MOTION_Y_THRESHOLD)  {
//            long totalWorldTime = getGameTime();
            if (recentCloseBobberSplash() || recentCloseWaterWake()) {
                essentials.LOGGER.debug("Detected bite by ALL");
                return true;
            }
        }
        return false;
    }

    private boolean recentCloseBobberSplash() {
        /** Close bobber sound must have been quite recent to indicate probable bite **/
        if (essentials.closeBobberSplashDetectedAt > 0
                && getGameTime() < essentials.closeBobberSplashDetectedAt + 20) {
            return true;
        }
        return false;
    }

    private boolean recentCloseWaterWake() {
        /** A close water wake indicates probable bite "soon", so make sure enough time has passed **/
        if (essentials.closeWaterWakeDetectedAt > 0
                && getGameTime() > essentials.closeWaterWakeDetectedAt + CLOSE_WATER_WAKE_DELAY_TICKS) {
            return true;
        }
        return false;
    }

    private void startReelDelay() {
        essentials.startedReelDelayAt = getGameTime();
    }

    private void startCastDelay() {
        essentials.startedCastDelayAt = getGameTime();
    }

    private void reelIn() {
        playerUseRod();
        essentials.player.fishingBobber = null;
    }

    private void playerUseRod() {
        essentials.minecraftClient.playerController.processRightClick(essentials.player, essentials.minecraftClient.world, Hand.MAIN_HAND);
    }

    private void scheduleNextCast() {
        essentials.castScheduledAt = getGameTime();
    }

    private boolean isTimeToCast() {
        return (essentials.castScheduledAt > 0 && getGameTime() > essentials.castScheduledAt + 40);
    }

    long getGameTime() {
        try {
            long gametime =  essentials.minecraftClient.world.getGameTime();
            return gametime;
        }
        catch (NullPointerException nullex) {
            essentials.LOGGER.error("Cannot get GameTime");
            return 0;
        }
    }

    private boolean rodIsCast() {
        if (!playerIsHoldingRod()) {
            return false;
        }
        //ItemStack fishingrod = essentials.player.getHeldItemMainhand();
        //return fishingrod.getItem().getPropertyGetter(new ResourceLocation("cast")).call(fishingrod, essentials.minecraftClient.world, essentials.player) > 0F;
        return essentials.player.fishingBobber == null;
    }

    private void startFishing() {
        playerUseRod();
        startCastDelay();
    }

    private void resetReelDelay() {
        essentials.startedReelDelayAt = 0L;
    }

    private void resetCastSchedule() {
        essentials.castScheduledAt = 0L;
    }

    private void resetBiteTracking() {
        essentials.xpLastAddedAt = 0L;
        essentials.closeWaterWakeDetectedAt = 0L;
//        this.exactWaterWakeDetectedAt = 0L;
        essentials.closeBobberSplashDetectedAt = 0L;
//        this.exactBobberSplashDetectedAt = 0L;
    }

    private PlayerEntity getServerPlayerEntity() {
        if (essentials.minecraftClient.getIntegratedServer() == null || essentials.minecraftClient.getIntegratedServer().getWorld(DimensionType.OVERWORLD) == null) {
            return null;
        } else {
            return (PlayerEntity) essentials.minecraftClient.getIntegratedServer().getWorld(DimensionType.OVERWORLD).getEntityByUuid(essentials.minecraftClient.player.getUniqueID());
        }
    }




}
