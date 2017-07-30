package org.techfire225.robot;

import org.techfire225.robot.subsystems.GearHolder;
import org.techfire225.robot.subsystems.Hopper;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;

public abstract class Autonomous extends CommandGroup {
	public abstract String getAutonomousName();
	
	@Override
	protected void interrupted() {
		super.interrupted();
		
		Robot.shooter.setMode(Shooter.Mode.OFF);
		Robot.hopper.setMode(Hopper.Mode.OFF);
		Robot.gearHolder.setMode(GearHolder.Mode.CLOSED);
	}
	
	@Override
	protected void end() {
		super.end();
		
		Robot.shooter.setMode(Shooter.Mode.OFF);
		Robot.hopper.setMode(Hopper.Mode.OFF);
		Robot.gearHolder.setMode(GearHolder.Mode.CLOSED);
	}
}