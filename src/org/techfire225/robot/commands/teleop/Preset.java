package org.techfire225.robot.commands.teleop;

import org.techfire225.robot.subsystems.Hopper;
import org.techfire225.robot.subsystems.Shooter;

public class Preset {
	public static final Preset OFF = new Preset(Shooter.Mode.OFF, Hopper.Mode.OFF);
	public static final Preset CLOSE = new Preset(Shooter.Mode.CLOSE, Hopper.Mode.FEED_CLOSE); 
	public static final Preset FAR = new Preset(Shooter.Mode.FAR_MANUAL, Hopper.Mode.FEED_FAR);

	public Shooter.Mode shooterMode;
	public Hopper.Mode hopperShootMode;
	
	public Preset(Shooter.Mode shooterMode, Hopper.Mode hopperShootMode) {
		this.shooterMode = shooterMode;
		this.hopperShootMode = hopperShootMode;
	}
}
