package org.techfire225.robot.commands.teleop;

import org.techfire225.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class PrepSystem extends InstantCommand {

	Preset preset;
	public PrepSystem(Preset preset) {
		this.preset = preset;
		requires(Robot.shooter);
	}
	
	
	protected void execute() {
		Robot.shooter.setMode(preset.shooterMode);
		if (preset == Preset.OFF)
			Robot.hopper.setMode(preset.hopperShootMode);
		Robot.currentPreset = preset;
	}
	
}
