package org.techfire225.robot.commands.drivetrain;

import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.TurnPIDController;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class PIDTurnTo extends Command {

	TurnPIDController controller = new TurnPIDController();
	
	public PIDTurnTo(double theta) {
		requires(Robot.drivetrain);
		controller.setAngle(90);
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
		return Robot.drivetrain.atSetpoint();
	}
	
	protected void end() {
		Robot.drivetrain.setController(null);
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(0, 0);
	}
}
