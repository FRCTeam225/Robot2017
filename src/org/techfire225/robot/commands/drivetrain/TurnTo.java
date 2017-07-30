package org.techfire225.robot.commands.drivetrain;

import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.ProfileTurnController;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class TurnTo extends Command {

	TrapezoidalMotionProfile profile;
	
	public TurnTo(double distance, double maxV, double maxAcc) {
		requires(Robot.drivetrain);
		profile = new TrapezoidalMotionProfile(distance, maxV, maxAcc);
	}
	
	@Override
	protected void initialize() {
		Robot.drivetrain.resetGyro();
		Robot.drivetrain.setLowGear(true);
		ProfileTurnController controller = new ProfileTurnController(profile);
		
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
	
	public TurnTo timeout(double t) {
		setTimeout(t);
		return this;
	}
}
