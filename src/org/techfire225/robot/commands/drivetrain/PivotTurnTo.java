package org.techfire225.robot.commands.drivetrain;

import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.PivotTurnController;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class PivotTurnTo extends Command {
	
	PivotTurnController controller;
	public PivotTurnTo(double theta, boolean leftSide) {
		requires(Robot.drivetrain);
		controller = new PivotTurnController(leftSide);
		controller.setAngle(theta);
	}
	
	@Override
	protected void initialize() {
		Robot.drivetrain.resetGyro();
		Robot.drivetrain.setLowGear(true);
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

	public PivotTurnTo timeout(double t) {
		setTimeout(t);
		return this;
	}
}
