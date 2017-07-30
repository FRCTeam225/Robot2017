package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.subsystems.GearHolder;

import edu.wpi.first.wpilibj.command.WaitCommand;

public class StraightGear extends Autonomous {

	public StraightGear() {
		addSequential(new Shift(true));
		addSequential(new DriveDistance(6.5, 7.0, 5.0));
		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
		addSequential(new WaitCommand(0.5));
		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
		addSequential(new WaitCommand(0.25));
		addSequential(new DriveDistance(-3.0, 7.0, 7.0));
    	addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
	}

	@Override
	public String getAutonomousName() {
		return "Straight gear";
	}
}
