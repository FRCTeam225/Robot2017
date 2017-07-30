package org.techfire225.robot.commands.drivetrain;

import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class DriveForTime extends Command {

	double speed;
	public DriveForTime(double t, double speed) {
		setTimeout(t);
		this.speed = speed;
	}
	
	protected void initialize() {
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(speed, speed);
	}
	
	protected void execute() {
		// Go!
		Robot.drivetrain.set(speed, speed);
	}
	
	protected void end() {
		Robot.drivetrain.set(0.0, 0.0);
	}
	
	protected void interrupted() {
		Robot.drivetrain.set(0.0, 0.0);
	}
	
	@Override
	protected boolean isFinished() {
		return isTimedOut();
	}

}
