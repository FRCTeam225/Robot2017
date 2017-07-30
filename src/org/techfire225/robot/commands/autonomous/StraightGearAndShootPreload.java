package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.SetRPMFromVision;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.commands.drivetrain.TurnTo;
import org.techfire225.robot.commands.shooter.Shoot;
import org.techfire225.robot.commands.teleop.PrepSystem;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.commands.teleop.VisionAlign;
import org.techfire225.robot.subsystems.GearHolder;

import edu.wpi.first.wpilibj.command.WaitCommand;

public class StraightGearAndShootPreload extends Autonomous {
	boolean leftSideBoiler;
	public StraightGearAndShootPreload(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		
		addSequential(new Shift(true));
		addSequential(new DriveDistance(6.5, 7.0, 5.0));

		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
		addSequential(new WaitCommand(0.5));
		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
		addSequential(new WaitCommand(0.25));
		
		addSequential(new DriveDistance(-4, 7, 5));
		addSequential(new TurnTo(100*needsFlip, 150, 150).timeout(1.0));
		addSequential(new PrepSystem(Preset.FAR));
		addSequential(new DriveDistance(5, 7, 5));
		
    	addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
		addSequential(new VisionAlign());
		addSequential(new WaitCommand(0.1));
		addSequential(new SetRPMFromVision());
		addSequential(new WaitCommand(0.2));
		addSequential(new Shoot());
	}

	@Override
	public String getAutonomousName() {
		return "Straight gear and shoot preload with boiler on "+(leftSideBoiler?"left":"right");
	}
}
