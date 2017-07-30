package org.techfire225.robot.commands.drivetrain;

import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Robot;
import org.techfire225.robot.drivetrain.controllers.ProfileDriveController;
import org.techfire225.robot.subsystems.Drivetrain;

import edu.wpi.first.wpilibj.command.Command;

public class DriveDistance extends Command {

	TrapezoidalMotionProfile profile;
	double vf = 0;
	
	public DriveDistance(double distance, double maxV, double maxAcc) {
		requires(Robot.drivetrain);
		profile = new TrapezoidalMotionProfile(distance, maxV, maxAcc);
	}
	
	public DriveDistance(double distance, double maxV, double maxAcc, double vi, double vf) {
		requires(Robot.drivetrain);
		profile = new TrapezoidalMotionProfile(distance, maxV, maxAcc, vi, vf);
		this.vf = vf;
	}
	
	@Override
	protected void initialize() {
		Robot.drivetrain.setLowGear(true);
		Robot.drivetrain.resetEncoders();
		ProfileDriveController controller = new ProfileDriveController(profile, Robot.drivetrain.getAngle());
		
		Robot.drivetrain.setController(controller);
		Robot.drivetrain.setMode(Drivetrain.Mode.CONTROLLED);
	}
	
	@Override
	protected boolean isFinished() {
		return Robot.drivetrain.atSetpoint() || isTimedOut();
	}
	
	protected void end() {
		Robot.drivetrain.setController(null);
		if ( vf == 0 ) {
			Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
			Robot.drivetrain.set(0, 0);
		}
	}
	
	public DriveDistance timeout(double t) {
		setTimeout(t);
		return this;
	}
}
