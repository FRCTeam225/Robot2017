package org.techfire225.robot.drivetrain.controllers;

import org.techfire225.lib.motion.TFPID;
import org.techfire225.robot.Constants;
import org.techfire225.robot.Robot;

public class HatAlignController implements DrivetrainController {

	TFPID pid = new TFPID();
	volatile double throttle = 0;
	
	public HatAlignController() {
		refreshConstants();
	}
	
	
	public void setAngle(double angle) {
		pid.setSetpoint(angle);
	}
	
	public void setThrottle(double throttle) {
		this.throttle = -throttle;
	}
	
	@Override
	public boolean update() {
		double turnOut = pid.calculate(Robot.drivetrain.getAngle());
		System.out.println("out:" +turnOut);
		Robot.drivetrain.set(throttle+turnOut, throttle-turnOut);
		
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
		return pid.getError();
	}

	@Override
	public double getAngularActual() {
		return pid.getInput();
	}

	@Override
	public double getAngularSetpoint() {
		return pid.getSetpoint();
	}

	@Override
	public void reset() {
		pid.reset();
	}

	@Override
	public void refreshConstants() {
		pid.setPIDF(Constants.getConstants().HatVisionAlignP, 0, 0, 0);
	}

}
