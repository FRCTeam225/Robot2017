package org.techfire225.robot.drivetrain.controllers;

import org.techfire225.robot.Constants;
import org.techfire225.robot.Robot;

public class DriveStraightAndShootController implements DrivetrainController {
	Constants constants;
	double forwardSpeed;
	double startDistance;
	double startRPM;
	
	double angleSetpoint = 0;
	
	public DriveStraightAndShootController(double speed) {
		constants = Constants.getConstants();
		this.forwardSpeed = speed;
	}

	
	@Override
	public void reset() {
		startRPM = Constants.distanceToRPM.getInterpolated(Robot.visionClient.getLastDistance());
		angleSetpoint = Robot.drivetrain.getLastVisionSetpoint();
		startDistance = Robot.drivetrain.getDistance();
	}

	
	@Override
	public boolean update() {
		double displacement = Robot.drivetrain.getDistance() - startDistance;
		 
		Robot.shooter.setRPM(startRPM 
				- constants.DriveAndShoot_ConstantRPMOffset 
				+ (displacement*constants.DriveAndShoot_DistanceToRPMOffsetSlope));
		
		
		double turn = (Robot.drivetrain.getAngle()-angleSetpoint)*constants.DriveProfile_theta_kP;
		
		Robot.drivetrain.set(forwardSpeed - turn, forwardSpeed + turn);
		return false;
	}

	@Override
	public double getLinearError() {
		return 0;
	}

	@Override
	public double getLinearActual() {
		return 0;
	}

	@Override
	public double getLinearSetpoint() {
		return 0;
	}

	@Override
	public double getAngularError() {
		return 0;
	}

	@Override
	public double getAngularActual() {
		return 0;
	}

	@Override
	public double getAngularSetpoint() {
		return 0;
	}

	@Override
	public void refreshConstants() {
	}

}
