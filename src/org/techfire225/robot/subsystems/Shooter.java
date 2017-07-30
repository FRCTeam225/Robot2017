package org.techfire225.robot.subsystems;

import org.techfire225.firevision2017.VisionMessage;
import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.lib.framework.Publisher;
import org.techfire225.lib.framework.TFSubsystem;
import org.techfire225.lib.webapp.ErrorReporter;
import org.techfire225.robot.Constants;
import org.techfire225.robot.PortMap;
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Solenoid;

public class Shooter extends TFSubsystem<Shooter.Mode> implements Publisher.Subscriber<VisionMessage> {

	public enum Mode{
		OFF,
		CLOSE,
		FAR_MANUAL,
		FAR_VISION,
		HOLD
	};
	
	private double setpoint;
	private double rpmAdjustment;
	
	private CANTalon[] shooter;
	
	private Solenoid shooterHood;
	
	private Constants constants;
	
	private volatile double visionRPMSetpoint;
	private volatile boolean hasVisionTarget;
	private volatile double visionDistance;
	
	public Shooter() {		
		constants = Constants.getConstants();
		hasVisionTarget = false;
		visionRPMSetpoint = 0;
		visionDistance = 0;
		
		shooter = new CANTalon[]{new CANTalon(PortMap.SHOOTER[0]), new CANTalon(PortMap.SHOOTER[1])};
		
		shooter[0].enableBrakeMode(false);
		shooter[0].configPeakOutputVoltage(12, 0);
		//shooter[0].setNominalClosedLoopVoltage(12.0);
		shooter[0].DisableNominalClosedLoopVoltage();
		shooter[0].setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		shooter[0].changeControlMode(CANTalon.TalonControlMode.Speed);
		shooter[0].reverseSensor(false);
		
		shooter[0].setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 10);
		
		shooter[0].SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_100Ms);
		shooter[0].SetVelocityMeasurementWindow(32);
		
		shooter[1].enableBrakeMode(false);
		shooter[1].configPeakOutputVoltage(12, 0);
		shooter[1].changeControlMode(CANTalon.TalonControlMode.Follower);
		shooter[1].set(shooter[0].getDeviceID());
		
		if ( shooter[0].isSensorPresent(CANTalon.FeedbackDevice.CtreMagEncoder_Relative) != CANTalon.FeedbackDeviceStatus.FeedbackStatusPresent )
			ErrorReporter.report("Shooter encoder isn't connected!");
		
		shooterHood = new Solenoid(PortMap.SHOOTER_HOOD);

		refreshConstants();
		setpoint = 0;
		rpmAdjustment = 0;
		onTarget = false;
		setMode(Mode.OFF);
	}
	
	private double getSetpoint() {
		return setpoint;
	}

	private double getRPM() {
		return shooter[0].getSpeed();
	}
	
	private double getError() {
		return shooter[0].getError();
	}
	
	public double getRPMAdjustment() {
		return rpmAdjustment;
	}
	
	public void setRPMAdjustment(double delta) {
		rpmAdjustment = delta;
		
		// Apply the adjustment if nothing else is updating it
		if ( getCurrentMode() == Mode.HOLD && setpoint != 0 )
			setRPM(setpoint);
	}
	
	public double getVisionRPM() {
		return visionRPMSetpoint;
	}
	
	public void setRPM(double speed) {
		shooter[0].set(speed + rpmAdjustment);
		setpoint = speed;
	}
	
	@Override
	public void setMode(Mode mode) {
		shooter[0].clearIAccum();
		super.setMode(mode);
	}
	
	@Override
	public void update(FireLogSample log) {
		Mode mode = getCurrentMode();
		log.put("shooterMode", mode.toString());
		
		log.put("shooterRPM", getRPM());
		log.put("shooterSetpoint", setpoint + rpmAdjustment);
		log.put("shooterRPMAdjustment", rpmAdjustment);
		
		log.put("shooterError", getError());
		
		log.put("visionDistance", visionDistance);
		

		
		if ( mode == Mode.FAR_VISION )
			onTarget = visionRPMSetpoint != 0 && hasVisionTarget && Math.abs(getError()) < 130;
		else
			onTarget = Math.abs(getError()) < 100; // 85
			
		
		switch (mode) {
		case OFF:
			updateOff();
			break;
		case CLOSE:
			updateClose();
			break;
		case FAR_MANUAL:
			updateFarManual();
			break;
		case FAR_VISION:
			updateFarVision();
			break;
		case HOLD:
			break;
		}
	}
	
	private void updateOff() {
		setRPM(0);
		shooterHood.set(false);
	}
	
	private void updateClose() {
		setRPM(constants.ShooterCloseRPM);
		shooterHood.set(true);
	}
	
	private void updateFarManual() {
		setRPM(constants.ShooterFarRPM);
		shooterHood.set(false);
	}
	
	private void updateFarVision() {
		setRPM(visionRPMSetpoint);
	}
	
	public boolean hasTarget() {
		return hasVisionTarget;
	}

	@Override
	protected void initDefaultCommand() {}

	@Override
	public synchronized void refreshConstants() {
		shooter[0].setPID(constants.ShooterP, constants.ShooterI, constants.ShooterD, constants.ShooterF, constants.ShooterIZone, 0, 0); // 0.3, 0.0, 20.0, 0.035, 0, 0, 0
	}

	@Override
	public void receiveMessage(VisionMessage msg) {
		hasVisionTarget = msg.found;
		if ( msg.found ) {
			System.out.println(msg.distance);
			visionDistance = msg.distance;
			Double rpm  = Constants.distanceToRPM.getInterpolated(msg.distance);
			if ( rpm != null )
				visionRPMSetpoint = rpm;
		}
	}
}