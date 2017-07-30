package org.techfire225.robot.subsystems;

import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.lib.framework.TFSubsystem;
import org.techfire225.robot.PortMap;
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Timer;

public class Hopper extends TFSubsystem<Hopper.Mode> {
	
	public enum Mode { 
		OFF,
		INTAKE_AND_AGITATE,
		AGITATE,
		FEED_CLOSE,
		FEED_FAR,
		PREPSHOT,
		FEED_FAR_INTAKE
	}
	
	private CANTalon intake;
	private CANTalon floor;
	private CANTalon feeder;
		
	private Timer t;
	Mode lastMode = Mode.OFF;
	
	private static final double INTAKE_STRENGTH = 1.0;
	
	private static final double FLOOR_SHOOT_STRENGTH = 1.0; // 0.75
	private static final double FLOOR_AGITATE_STRENGTH = 1.0;
	
	private static final double FEEDER_SHOOT_STRENGTH_OPENLOOP = 1.0; // 0.9
	private static final double FEEDER_SHOOT_STRENGTH_OPENLOOP_CLOSE = 0.65; // 1.0
	
	
	public Hopper() {
		intake = new CANTalon(PortMap.INTAKE);
		intake.enableBrakeMode(true);
		intake.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		intake.reverseSensor(true);
		intake.setInverted(false);
		
		floor = new CANTalon(PortMap.FLOOR);
		floor.enableBrakeMode(false);
		floor.reverseSensor(true);
		floor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		floor.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

		feeder = new CANTalon(PortMap.FEEDER);	
		feeder.enableBrakeMode(false);
		feeder.setInverted(true);
		feeder.configPeakOutputVoltage(12, 0);
		feeder.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		
		t = new Timer();
		t.start();
		setMode(Mode.OFF);
	}
	
	public double getSpeed() {
		return feeder.getSpeed();
	}
	
	public double getSetpoint() {
		return feeder.getSpeed();
	}
	
	@Override
	public void update(FireLogSample log) {
		Mode mode = getCurrentMode();
		log.put("hopperMode", mode.toString());
		
		log.put("feederSetpoint", feeder.getSetpoint());
		log.put("feederRPM", feeder.getSpeed());
		
		log.put("floorRPM", floor.getSpeed());
		log.put("floorCurrent", floor.getOutputCurrent());
		
		switch (mode) {
			case OFF: 
				updateOff();
				break;
			case INTAKE_AND_AGITATE:
				updateIntakeAndAgitate();
				break;
			case AGITATE:
				updateAgitate();
				break;
			case FEED_CLOSE:
				updateFeedClose();
				break;
			case FEED_FAR:
				updateFeedFar();
				break;
			case PREPSHOT:
				updatePrepShot();
				break;
			case FEED_FAR_INTAKE:
				updateFeedFarIntake();
				break;
		}
	}
	
	private void updateOff() {
		intake.set(0);
		floor.set(0);
		feeder.set(0);
	}
	
	private void updateIntakeAndAgitate() {
		intake.set(INTAKE_STRENGTH);
		floor.set(FLOOR_AGITATE_STRENGTH);
		feeder.set(0);
	}
	
	private void updateAgitate() {
		intake.set(0);
		floor.set(FLOOR_AGITATE_STRENGTH);
		feeder.set(0);
	}
	
	private void updateFeedClose() {
		floor.set(FLOOR_SHOOT_STRENGTH);
		feeder.set(FEEDER_SHOOT_STRENGTH_OPENLOOP_CLOSE);
		intake.set(0);
	}
	
	private void updateFeedFar() {
		floor.set(FLOOR_SHOOT_STRENGTH);
		feeder.set(FEEDER_SHOOT_STRENGTH_OPENLOOP);
		intake.set(0);
	}
	
	private void updateFeedFarIntake() {
		floor.set(FLOOR_SHOOT_STRENGTH);
		feeder.set(FEEDER_SHOOT_STRENGTH_OPENLOOP);
		intake.set(INTAKE_STRENGTH);
	}
	
	private void updatePrepShot() {
		intake.set(0);
		floor.set(0);
		feeder.set(FEEDER_SHOOT_STRENGTH_OPENLOOP);
	}

	@Override
	protected void initDefaultCommand() {}

	@Override
	public void refreshConstants() {}
}
