package de.recklessGreed.EarliboySubServerEssentials;

import de.recklessGreed.EarliboySubServerEssentials.utils.MinecraftProfile;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.util.text.TextFormatting;
import java.util.UUID;


public class HorseStats {

	private final TextFormatting LEGENDARY = TextFormatting.GOLD;
	private final TextFormatting EPIC      = TextFormatting.DARK_PURPLE;
	private final TextFormatting RARE      = TextFormatting.BLUE;
	private final TextFormatting UNCOMMON  = TextFormatting.WHITE;
	private final TextFormatting COMMON    = TextFormatting.GRAY;

	public String[] getHorseStats(AbstractHorseEntity horseEntity){

		String[] returner = new String[5];
		TextFormatting colourHealth = TextFormatting.WHITE;
		TextFormatting colourSpeed = TextFormatting.WHITE;
		TextFormatting colourJump = TextFormatting.WHITE;
		double health = horseEntity.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
		double speed = horseEntity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
		double jump = horseEntity.getHorseJumpStrength();
		String variantName = "";
		UUID ownerUUID = horseEntity.getOwnerUniqueId();
		MinecraftProfile profile = new MinecraftProfile(ownerUUID);
		String owner = profile.getUsername();
		if (horseEntity instanceof DonkeyEntity)
		{
			variantName = "Donkey";
		}
		else if (horseEntity instanceof LlamaEntity) {
			variantName = "Llama";
		}
		else if (horseEntity instanceof MuleEntity) {
			variantName = "Maultier";
		}
		else {
			HorseEntity entity = (HorseEntity) horseEntity;
			int variant = entity.getHorseVariant();
			variantName = getVariantName(variant);
		}
		jump = -0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367; // https://minecraft.gamepedia.com/Horse
		speed = speed * 43;

		if (health <= 20) {
			colourHealth = COMMON;
		} else if (health <= 23) {
			colourHealth = UNCOMMON;
		} else if (health <= 26) {
			colourHealth = RARE;
		} else if (health <= 29) {
			colourHealth = EPIC;
		} else if (health <= Double.MAX_VALUE) {
			colourHealth = LEGENDARY;
		}

		if (speed <= 7) {
			colourSpeed = COMMON;
		} else if (speed <= 9) {
			colourSpeed = UNCOMMON;
		} else if (speed <= 11) {
			colourSpeed = RARE;
		} else if (speed <= 13) {
			colourSpeed = EPIC;
		} else if (speed <= Double.MAX_VALUE) {
			colourSpeed = LEGENDARY;
		}

		if (jump <= 1.50) {
			colourJump = COMMON;
		} else if (jump <= 2.0) {
			colourJump = UNCOMMON;
		} else if (jump <= 3.0) {
			colourJump = RARE;
		} else if (jump <= 4.0) {
			colourJump = EPIC;
		} else if (jump <= Double.MAX_VALUE) {
			colourJump = LEGENDARY;
		}

		int healthVal = (int) Math.round(health);
		returner[SubserverEssentials.HorseArray.OWNER.ordinal()]       = owner == null ? "Untamed" : "Owner: " + owner;
		returner[SubserverEssentials.HorseArray.VARIANT.ordinal()]     = variantName;
		returner[SubserverEssentials.HorseArray.HEALTH.ordinal()]      = "    " + colourHealth + "Health:   " + healthVal + " Points or " + (int)(healthVal / 2) + " Hearts" ;
		returner[SubserverEssentials.HorseArray.JUMP_HEIGHT.ordinal()] = "    " + colourJump   + "Jump:     " + round(jump) + " Blocks";
		returner[SubserverEssentials.HorseArray.SPEED.ordinal()]       = "    " + colourSpeed  + "Speed:   " + round(speed) + " Blocks per Second";


		if (horseEntity instanceof LlamaEntity) {
			TextFormatting colourSlots = TextFormatting.WHITE;
			double slots = ((LlamaEntity) horseEntity).getStrength();

			slots = slots * 3;

			if (slots <= 3) {
				colourSlots = COMMON;
			} else if (slots <= 6) {
				colourSlots = UNCOMMON;
			} else if (slots <= 9) {
				colourSlots = RARE;
			} else if (slots <= 12) {
				colourSlots = EPIC;
			} else if (slots <= Double.MAX_VALUE) {
				colourSlots = LEGENDARY;
			}

			return returner;

		} else {



			//player.sendStatusMessage(new TranslationTextComponent(String.format("%sHealth: %.0f %sSpeed: %.1f %sJump Height: %.1f", colourHealth, health, colourSpeed, speed, colourJump, jump)), true);
			System.setProperty("java.awt.headless", "false");
			//Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			//clipboard.setContents(new StringSelection(String.format("%.0f %.1f %.1f", health, speed, jump)), null);
			return returner;
			//return new TranslationTextComponent(String.format("Owner: %s ; Variant: %s %sHealth: %.0f %sSpeed: %.1f %sJump Height: %.1f", owner, variantName, colourHealth, health, colourSpeed, speed, colourJump, jump));

		}
	}

	String getVariantName(int variant) {

		String c;
		String v;

		switch (variant % 8) {
			default: c = "White"; break;
			case 1: c = "Creamy"; break;
			case 2: c = "Chestnut"; break;
			case 3: c = "Brown"; break;
			case 4: c = "Black"; break;
			case 5: c = "Gray"; break;
			case 6: c = "Dark Brown"; break;
		}

		if (variant < 255) {
			return c;
		}
		else if (variant < 511) {
			v = "White Stockings";
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

	double round(double val) {
		return Math.round(100.0 * val) / 100.0;
	}
}
