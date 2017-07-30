package org.techfire225.robot.commands.teleop;

import org.techfire225.robot.OI;
import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.Drivetrain;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.command.Command;

public class VisionAlign extends Command {

	int numberLoopsStable = 0;

	public VisionAlign() {
		requires(Robot.shooter);
		requires(Robot.drivetrain);
	}
	
	protected void initialize() {
		numberLoopsStable = 0;
		Robot.currentPreset = Preset.FAR;
		Robot.drivetrain.setLowGear(true);
		Robot.drivetrain.setMode(Drivetrain.Mode.VISION);
		Robot.shooter.setMode(Shooter.Mode.FAR_VISION);
	}
	
	protected void end() {
		Robot.shooter.setMode(Shooter.Mode.HOLD);
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(0, 0);
	}
	
	@Override
	protected boolean isFinished() {
		if ( Robot.drivetrain.atSetpoint() )
			numberLoopsStable++;
		else
			numberLoopsStable = 0;
		
		return numberLoopsStable > 10 || Math.abs(OI.driver.getRawAxis(4)) > 0.1 || Math.abs(OI.driver.getRawAxis(1)) > 0.1 || isTimedOut();
	}
	
	public VisionAlign timeout(double t) {
		setTimeout(t);
		return this;
	}
}
