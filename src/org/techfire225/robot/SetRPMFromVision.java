package org.techfire225.robot;

import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.command.Command;

public class SetRPMFromVision extends Command {

	public SetRPMFromVision() {
		requires(Robot.shooter);
	}
	
	protected void initialize() {
		Robot.shooter.setMode(Shooter.Mode.HOLD);
		double visionRPM = Robot.shooter.getVisionRPM();
		if ( visionRPM != 0 )
			Robot.shooter.setRPM(visionRPM);
	}
	
	@Override
	protected boolean isFinished() {
		return true;
	}

}
