package de.recklessGreed.EarliboySubServerEssentials;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


public class HorseStats {

	public TranslationTextComponent getHorseStats(AbstractHorseEntity horseEntity){
		TextFormatting colourHealth = TextFormatting.WHITE;
		TextFormatting colourSpeed = TextFormatting.WHITE;
		TextFormatting colourJump = TextFormatting.WHITE;
		double health = horseEntity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
		double speed = horseEntity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
		double jump = horseEntity.getHorseJumpStrength();
		HorseEntity entity = (HorseEntity) horseEntity;
		String texture = entity.getHorseTexture();
		int variant = entity.getHorseVariant();
		String variantName = getVariantName(variant);
		jump = -0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367; // https://minecraft.gamepedia.com/Horse
		speed = speed * 43;

		if (health <= 20) {
			colourHealth = TextFormatting.GRAY;
		} else if (health <= 23) {
			colourHealth = TextFormatting.WHITE;
		} else if (health <= 26) {
			colourHealth = TextFormatting.YELLOW;
		} else if (health <= 29) {
			colourHealth = TextFormatting.AQUA;
		} else if (health <= Double.MAX_VALUE) {
			colourHealth = TextFormatting.LIGHT_PURPLE;
		}

		if (speed <= 7) {
			colourSpeed = TextFormatting.GRAY;
		} else if (speed <= 9) {
			colourSpeed = TextFormatting.WHITE;
		} else if (speed <= 11) {
			colourSpeed = TextFormatting.YELLOW;
		} else if (speed <= 13) {
			colourSpeed = TextFormatting.AQUA;
		} else if (speed <= Double.MAX_VALUE) {
			colourSpeed = TextFormatting.LIGHT_PURPLE;
		}

		if (jump <= 1.50) {
			colourJump = TextFormatting.GRAY;
		} else if (jump <= 2.0) {
			colourJump = TextFormatting.WHITE;
		} else if (jump <= 3.0) {
			colourJump = TextFormatting.YELLOW;
		} else if (jump <= 4.0) {
			colourJump = TextFormatting.AQUA;
		} else if (jump <= Double.MAX_VALUE) {
			colourJump = TextFormatting.LIGHT_PURPLE;
		}

		if (horseEntity instanceof LlamaEntity) {
			TextFormatting colourSlots = TextFormatting.WHITE;
			double slots = ((LlamaEntity) horseEntity).getStrength();

			slots = slots * 3;

			if (slots <= 3) {
				colourSlots = TextFormatting.GRAY;
			} else if (slots <= 6) {
				colourSlots = TextFormatting.WHITE;
			} else if (slots <= 9) {
				colourSlots = TextFormatting.YELLOW;
			} else if (slots <= 12) {
				colourSlots = TextFormatting.AQUA;
			} else if (slots <= Double.MAX_VALUE) {
				colourSlots = TextFormatting.LIGHT_PURPLE;
			}

			return new TranslationTextComponent(String.format("Variant:%s %sHealth: %.0f %sSpeed: %.1f %sChest Slots: %.0f",variantName, colourHealth, health, colourSpeed, speed, colourSlots, slots));

		} else {
			//player.sendStatusMessage(new TranslationTextComponent(String.format("%sHealth: %.0f %sSpeed: %.1f %sJump Height: %.1f", colourHealth, health, colourSpeed, speed, colourJump, jump)), true);
			return new TranslationTextComponent(String.format("Variant:%s %sHealth: %.0f %sSpeed: %.1f %sJump Height: %.1f",variantName, colourHealth, health, colourSpeed, speed, colourJump, jump));
		}
	}

	public String getVariantName(int variant) {

		String c = "";
		String v = "";

		String binary = Integer.toBinaryString(variant);

		switch (variant % 8) {
			case 0: c = "White"; break;
			case 1: c = "Creamy"; break;
			case 2: c = "Chestnut"; break;
			case 3: c = "Brown"; break;
			case 4: c = "Black"; break;
			case 5: c = "Gray"; break;
			case 6: c = "Dark Brown"; break;
		}

		if (variant < 255) {
			v = "";
		}
		else if (variant < 511) {
			v = "White";
		}
		else if (variant < 767) {
			v = "White Field";
		}
		else if (variant < 1023) {
			v = "White Dots";
		}
		else {
			v = "Black Dots";
		}

		return c + " with " + v;
	}
}
