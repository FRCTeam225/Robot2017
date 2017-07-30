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

public class KPAOnlyFastFar extends Autonomous {
	boolean leftSideBoiler;
	public KPAOnlyFastFar(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		// double hopperDistance = leftSideBoiler ? 2.73 : 2.73;
		
		{  /* Go to hopper */
			TrapezoidalMotionProfile distance = new TrapezoidalMotionProfile(8.5, 7, 5, 8, 4);
			TrapezoidalMotionProfile angle = new TrapezoidalMotionProfile(90*needsFlip, 95, 110);
			
			addSequential(new Shift(true));
			addSequential(new DriveDistance(2.84, 7, 5, 0, 8));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.FUEL_LOAD));
			addSequential(new DriveTwoProfile(distance, angle, 2));
			// Smash the wall
			addParallel(new DriveForTime(1.5, 0.5)); // (0.5, 0.7)
		}
		{   // collect from hopper
			addSequential(new PrepSystem(Preset.FAR));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.AGITATE));
			addSequential(new WaitCommand(2.0));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.OFF));
			
			// shoot from hopper
			addSequential(new PivotTurnTo(70*needsFlip, leftSideBoiler).timeout(0.75));
			// addSequential(new DriveDistance(1.0, 7, 9).timeout(1)); // (1.5, 7, 9)
			addSequential(new VisionAlign().timeout(1.0));
			addSequential(new Shoot());
		}
	}

	@Override
	public String getAutonomousName() {
		return "FAR Fast KPA only with boiler on "+(leftSideBoiler?"left":"right");
	}
	
}
