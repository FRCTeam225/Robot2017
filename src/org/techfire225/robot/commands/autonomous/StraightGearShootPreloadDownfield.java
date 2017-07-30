package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.DriveTwoProfile;
import org.techfire225.robot.commands.drivetrain.PivotTurnTo;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.commands.drivetrain.TurnTo;
import org.techfire225.robot.commands.shooter.Shoot;
import org.techfire225.robot.commands.teleop.PrepSystem;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.commands.teleop.VisionAlign;
import org.techfire225.robot.subsystems.GearHolder;

import edu.wpi.first.wpilibj.command.WaitCommand;

public class StraightGearShootPreloadDownfield extends Autonomous {
	boolean leftSideBoiler;
	public StraightGearShootPreloadDownfield(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		
		addSequential(new Shift(true));
		addSequential(new DriveDistance(7.0, 8, 20).timeout(1.5));

		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
		addSequential(new WaitCommand(0.5));
		addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
		addSequential(new WaitCommand(0.25));
		
		addSequential(new DriveDistance(-4, 8, 20));
		addSequential(new TurnTo(100*needsFlip, 150, 150).timeout(1.0));
		addSequential(new PrepSystem(Preset.FAR));
		addSequential(new DriveDistance(5, 8, 20).timeout(1.5));
		
    	addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
		addSequential(new VisionAlign().timeout(1.0));
		addSequential(new Shoot().timeout(3.0));
		
		addSequential(new PivotTurnTo(-70*needsFlip, !leftSideBoiler));
		
		TrapezoidalMotionProfile distance3 = new TrapezoidalMotionProfile(15, 8, 20, 8, 8); // 9.6, 7, 5 (9.1, 7, 5, 7, 2)
		TrapezoidalMotionProfile angle3 = new TrapezoidalMotionProfile(-56*needsFlip, 25, 29.5); // -57, 25, 31 (58, 40)
		DriveTwoProfile arcTurn3 = new DriveTwoProfile(distance3, angle3);
		
		addSequential(new DriveDistance(3, 8, 20, 0, 8));
		addSequential(arcTurn3); // .timeout(2.6)
		addSequential(new DriveDistance(5, 8, 20, 8, 0));

	}

	@Override
	public String getAutonomousName() {
		return "Straight gear and shoot preload and drive downfield with boiler on "+(leftSideBoiler?"left":"right");
	}
}
