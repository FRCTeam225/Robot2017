package org.techfire225.robot.commands.shooter;

import org.techfire225.robot.Constants;
import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.Hopper.Mode;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class Shoot extends Command {
	boolean overpower = false;
	 
	Constants constants;
	 
	Timer overpowerTimer = new Timer();
	public Shoot() {
		requires(Robot.hopper);
		constants = Constants.getConstants();
	}
	
	@Override
	protected void initialize() {
		if (Robot.currentPreset == null || Robot.shooter.getCurrentMode() == Shooter.Mode.OFF)
			return;
		
		overpowerTimer.reset();
		overpowerTimer.start();
		
		overpower = true;
	}
	
	@Override
	protected void execute () {
		if (Robot.currentPreset == null)
			return;
		
		if (overpower && overpowerTimer.get() > constants.ShooterOverpowerDuration) {
			overpower = false;
			Robot.shooter.setRPMAdjustment(-constants.ShooterOverpowerOffset);
		}
		
		Robot.hopper.setMode(Robot.currentPreset.hopperShootMode);
	}
	
	@Override
	protected boolean isFinished() {
		return isTimedOut() 
				|| Robot.currentPreset == null 
				|| Robot.shooter.getCurrentMode() == Shooter.Mode.OFF;
	}
	
	@Override
	protected void end() {
		Robot.hopper.setMode(Mode.OFF);
		Robot.shooter.setRPMAdjustment(0);
	}
	
	@Override
	protected void interrupted() {
		Robot.hopper.setMode(Mode.OFF);
		Robot.shooter.setRPMAdjustment(0);
	}
	
	public Shoot timeout(double t) {
		setTimeout(t);
		return this;
	}
}