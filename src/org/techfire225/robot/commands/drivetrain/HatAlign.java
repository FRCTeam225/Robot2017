package org.techfire225.robot.commands.drivetrain;

import org.techfire225.robot.OI;
import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class HatAlign extends Command {

	public HatAlign() {
		requires(Robot.drivetrain);
	}
	
	protected void initialize() {
		Robot.drivetrain.setController(Robot.drivetrain.hatAlignController);
		Robot.drivetrain.setMode(Drivetrain.Mode.CONTROLLED);
	}
	
	protected void execute() {
		Robot.drivetrain.hatAlignController.setThrottle(OI.driver.getRawAxis(1));
	}
	
	protected void end() {
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.setController(null);
		Robot.drivetrain.set(0, 0);
	}
	
	@Override
	protected boolean isFinished() {
		return Math.abs(OI.driver.getRawAxis(4)) > 0.1;
	}

}
