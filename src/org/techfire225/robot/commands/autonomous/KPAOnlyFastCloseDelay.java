package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.DriveForTime;
import org.techfire225.robot.commands.drivetrain.DriveTwoProfile;
import org.techfire225.robot.commands.drivetrain.PivotTurnTo;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.commands.shooter.Shoot;
import org.techfire225.robot.commands.teleop.PrepSystem;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.commands.teleop.VisionAlign;
import org.techfire225.robot.subsystems.GearHolder;
import org.techfire225.robot.subsystems.Hopper;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class KPAOnlyFastCloseDelay extends Autonomous {
	boolean leftSideBoiler;
	public KPAOnlyFastCloseDelay(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		double hopperDistance = leftSideBoiler ? 3.75 : 3.8;
		
		{  /* Go to hopper */
			TrapezoidalMotionProfile distance = new TrapezoidalMotionProfile(7, 7, 20, 8, 4); // 8
			TrapezoidalMotionProfile angle = new TrapezoidalMotionProfile(90*needsFlip, 140, 150);
			
			addSequential(new Shift(true));
			addSequential(new DriveDistance(hopperDistance, 7, 20, 0, 8));// 3.5, 3.8 worked on blue practice
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.FUEL_LOAD));
			//addSequential(new PivotTurnTo(90*needsFlip, !leftSideBoiler));
			addSequential(new DriveTwoProfile(distance, angle, 1.5));
			// Smash the wall
			addParallel(new DriveForTime(0.5, 0.5)); // (0.5, 0.7)
		}
		{   // collect from hopper
			addSequential(new PrepSystem(Preset.FAR));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.AGITATE));
			addSequential(new WaitCommand(2.25));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.OFF));
			
			// shoot from hopper
			addSequential(new PivotTurnTo(70*needsFlip, leftSideBoiler).timeout(0.75));
			addSequential(new DriveDistance(0.75, 7, 9).timeout(1)); // (1.5, 7, 9)
			addSequential(new VisionAlign().timeout(1.0));
			addSequential(new Shoot());
		}
	}

	@Override
	public String getAutonomousName() {
		return "CLOSE Fast KPA only with delay and boiler on "+(leftSideBoiler?"left":"right");
	}
	
}