package org.techfire225.robot.subsystems;

import org.techfire225.robot.Constants;
import org.techfire225.robot.PortMap;
import org.techfire225.robot.commands.teleop.ManualClimber;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {

	Constants constants;
	CANTalon climber;
	
	
	public Climber() {
		constants = Constants.getConstants();
		climber = new CANTalon(PortMap.CLIMBER);
		
		climber.changeControlMode(TalonControlMode.PercentVbus);
		climber.set(0);
		climber.enableBrakeMode(false);
		climber.setInverted(false);
	}

	
	public void set(double speed) {
		if ( speed < 0 )
			speed = 0;
		climber.set(speed);
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ManualClimber());
	}
}
