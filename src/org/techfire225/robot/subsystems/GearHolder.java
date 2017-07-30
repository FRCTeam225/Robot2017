package org.techfire225.robot.subsystems;

import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.lib.framework.TFSubsystem;
import org.techfire225.robot.PortMap;

import edu.wpi.first.wpilibj.Solenoid;

public class GearHolder extends TFSubsystem<GearHolder.Mode>{

	public enum Mode {
		CLOSED,
		GEAR_LOAD,
		PRE_DROP,
		DROP,
		FUEL_LOAD,
		FUEL_HOLD
	};
	
	private Solenoid gearFlap;
	private Solenoid gearDoor;
	private Solenoid fuelFlap;
	
	public GearHolder() {
		gearFlap = new Solenoid(PortMap.GEAR_FLAP);
		gearDoor = new Solenoid(PortMap.GEAR_DOOR);
		fuelFlap = new Solenoid(PortMap.FUEL_FLAP);
		
		setMode(Mode.CLOSED);
	}
	
	@Override
	public void update(FireLogSample log) {
		Mode mode = getCurrentMode();
		log.put("gearHolderMode", mode.toString());
				
		switch (mode) {
		case CLOSED:
			updateClosed();
			break;
		case GEAR_LOAD:
			updateLoad();
			break;
		case PRE_DROP:
			updatePreDrop();
			break;
		case DROP:
			updateDrop();
			break;
		case FUEL_LOAD:
			updateFuelLoad();
			break;
		case FUEL_HOLD:
			updateFuelHold();
			break;
		}
	}
	
	private void updateClosed() {
		gearFlap.set(false);
		gearDoor.set(false);
	}
	
	private void updateLoad() {
		gearFlap.set(true);
		gearDoor.set(false);
		fuelFlap.set(false);
	}
	
	private void updatePreDrop() {
		gearFlap.set(false);
		gearDoor.set(true);
	}
	
	private void updateDrop() {
		gearFlap.set(true);
		gearDoor.set(true);
	}
	
	private void updateFuelLoad() {
		fuelFlap.set(true);
	}
	
	private void updateFuelHold() {
		fuelFlap.set(false);
	}

	@Override
	protected void initDefaultCommand() {}

	@Override
	public void refreshConstants() {}
}