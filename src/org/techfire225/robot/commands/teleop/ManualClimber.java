package org.techfire225.robot.commands.teleop;

import org.techfire225.robot.Constants;
import org.techfire225.robot.OI;
import org.techfire225.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ManualClimber extends Command {
	
	Constants constants;
	
	public ManualClimber() {
		requires(Robot.climber);
		constants = Constants.getConstants();
	}
	
	
	public void execute() {
		double speed = Math.abs(OI.operator.getRawAxis(3));
		
		// Deadband
		if (speed < 0.1)
			speed = 0;
		else {
			speed *= (1-constants.ClimberStartSpeed);
			speed += constants.ClimberStartSpeed;
		}
		
		Robot.climber.set(speed);
	}
	
	@Override
	protected boolean isFinished() {
		return false;
	}
}
