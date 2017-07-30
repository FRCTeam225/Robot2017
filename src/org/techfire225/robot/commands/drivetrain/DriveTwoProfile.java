package org.techfire225.robot.commands.drivetrain;

import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.LinearAngularFollowerController;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTwoProfile extends Command {

	TrapezoidalMotionProfile linear;
	TrapezoidalMotionProfile angular;
	
	public DriveTwoProfile(TrapezoidalMotionProfile linear, TrapezoidalMotionProfile angular) {
		requires(Robot.drivetrain);
		this.linear = linear;
		this.angular = angular;
	}
	
	public DriveTwoProfile(TrapezoidalMotionProfile linear, TrapezoidalMotionProfile angular, double timeout) {
		this(linear, angular);
		setTimeout(timeout);
	}
	
	@Override
	protected void initialize() {
		Robot.drivetrain.setLowGear(true);
		Robot.drivetrain.resetEncoders();
		LinearAngularFollowerController controller = new LinearAngularFollowerController(linear, angular);
		
		Robot.drivetrain.setController(controller);
		Robot.drivetrain.setMode(Drivetrain.Mode.CONTROLLED);
	}
	
	@Override
	protected boolean isFinished() {
		return Robot.drivetrain.atSetpoint() || isTimedOut();
	}
	
	protected void end() {
		Robot.drivetrain.setController(null);
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(0, 0);
	}
	
	public DriveTwoProfile timeout(double t) {
		setTimeout(t);
		return this;
	}
}
