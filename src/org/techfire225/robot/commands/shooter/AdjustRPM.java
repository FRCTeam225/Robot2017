package org.techfire225.robot.commands.shooter;

import org.techfire225.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *Changes the shooter's RPM setpoint by a specified delta
 */
public class AdjustRPM extends InstantCommand {

	double delta;
	
    public AdjustRPM(double delta) {
       this.delta = delta;
    }

    @Override
    protected void execute() {
    	Robot.shooter.setRPMAdjustment(Robot.shooter.getRPMAdjustment() + delta);
    }
}
