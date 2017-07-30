package org.techfire225.robot.commands.drivetrain;

import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.DriveStraightAndShootController;
import org.techfire225.robot.subsystems.Drivetrain;
import org.techfire225.robot.subsystems.Hopper;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.command.Command;

public class DriveForwardAndShoot extends Command {

	DriveStraightAndShootController driveController;
	
	public DriveForwardAndShoot(double speed) {
		requires(Robot.drivetrain);
		requires(Robot.shooter);
		requires(Robot.hopper);
		
		driveController = new DriveStraightAndShootController(speed);
	}
	
	public DriveForwardAndShoot(double speed, double timeout) {
		this(speed);
		setTimeout(timeout);
	}
	
	
	public void initialize() {		
		Robot.shooter.setMode(Shooter.Mode.HOLD);
		driveController.reset();
		Robot.drivetrain.setController(driveController);
		Robot.drivetrain.setMode(Drivetrain.Mode.CONTROLLED);
		Robot.hopper.setMode(Hopper.Mode.FEED_FAR_INTAKE);
	}
	
	@Override
	protected boolean isFinished() {
		return isTimedOut();
	}
	
	public void end() {
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(0, 0);
		Robot.drivetrain.setController(null);
		
		Robot.hopper.setMode(Hopper.Mode.OFF);
	}

}
