package cc.sleek.client.util;

import cc.sleek.client.Sleek;
import cc.sleek.client.event.impl.TickEvent;
import cc.sleek.client.event.impl.UpdateEvent;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;

public class Player {

	// How fast you fall
	private static double fallSpeed = 0.08;
	
	// How strong the gravity is
	private static double gravity = 0.9800000190734863;
	
	// Data to help me bypasses anticheats
	private static transient double dist = 0, distX = 0, distZ = 0, lastDist = 0, lastDistX = 0, lastDistZ = 0;
	private static transient boolean onGround = false, lastOnGround = false, lastLastOnground = false;
	
	// Minecraft
	public static transient Minecraft mc = Minecraft.getMinecraft();

	// Player constructor that subscribes to events
	public Player() {
		// Subscribe to events
		Sleek.INSTANCE.getEventBus().subscribe(this);
	}

	// Event hook
	@EventLink
	Listener<UpdateEvent> updateEventListener = e -> {
		// Reset any values that need resetting
		if (e.isPre()) {
			fallSpeed = 0.08;
			gravity = 0.9800000190734863;
		} else {
			lastDist = dist;
			lastDistX = distX;
			lastDistZ = distZ;
			// Set calculate distance
			distX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
			distZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
			dist = (distX * distX) + (distZ * distZ);

			// Set last on ground values
			lastLastOnground = lastOnGround;
			lastOnGround = onGround;
			// Set on ground
			onGround = mc.thePlayer.onGround;
		}
	};


	
	public static double getDist() {
		return dist;
	}

	public static double getDistX() {
		return distX;
	}

	public static double getDistZ() {
		return distZ;
	}

	public static double getLastDist() {
		return lastDist;
	}

	public static double getLastDistX() {
		return lastDistX;
	}

	public static double getLastDistZ() {
		return lastDistZ;
	}

	public static boolean isOnGround() {
		onGround = mc.thePlayer.onGround;
		return onGround;
	}

	public static boolean isLastOnGround() {
		return lastOnGround;
	}

	public static boolean isLastLastOnground() {
		return lastLastOnground;
	}
	
	public static double getFallSpeed() {
		return fallSpeed;
	}

	public static void setFallSpeed(double fallSpeed) {
		Player.fallSpeed = fallSpeed;
	}

	public static double getGravity() {
		return gravity;
	}

	public static void setGravity(double gravity) {
		Player.gravity = gravity;
	}

	
}
