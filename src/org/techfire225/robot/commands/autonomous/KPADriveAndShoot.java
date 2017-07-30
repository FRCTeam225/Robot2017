package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.DriveForTime;
import org.techfire225.robot.commands.drivetrain.DriveForwardAndShoot;
import org.techfire225.robot.commands.drivetrain.DriveTwoProfile;
import org.techfire225.robot.commands.drivetrain.PivotTurnTo;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.commands.teleop.PrepSystem;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.commands.teleop.VisionAlign;
import org.techfire225.robot.subsystems.Hopper;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class KPADriveAndShoot extends Autonomous {
	boolean leftSideBoiler;
	public KPADriveAndShoot(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		
		/* Tuning step one:
		 * Increase the initial drive distance until you hit the far hopper
		 */
		
		
		{  // Go to hopper
			// Don't touch these profiles
			TrapezoidalMotionProfile distance = new TrapezoidalMotionProfile(8.5, 7, 5, 7, 0);
			TrapezoidalMotionProfile angle = new TrapezoidalMotionProfile(90*needsFlip, 95, 110);
			
			addSequential(new Shift(true));
			addSequential(new DriveDistance(2.6+4.5, 7, 5, 0, 7)); // <--- Initial drive distance
			addSequential(new DriveTwoProfile(distance, angle, 2));
			// Smash the wall
			addParallel(new DriveForTime(0.5, 0.7));
		}
		
		
		/*
		 * Step two
		 * Adjust forward speed so that it's the speed you want
		 * Adjust DriveAndShoot_DistanceToRPMOffsetSlope until the balls are falling in a fairly consistant location
		 * Adjust DriveAndShoot_ConstantRPMOffset to shift the whole curve forward/backwards
		 * 
		 * Adjust the time to wait at hopper as needed
		 */
		
		{ // Shoot from far hopper
			addSequential(new PrepSystem(Preset.FAR));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.AGITATE));
			addSequential(new WaitCommand(1.5)); // <--- Time to wait at hopper
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.OFF));
			
			addSequential(new PivotTurnTo(70*needsFlip, leftSideBoiler));
			addSequential(new VisionAlign().timeout(1.5));
			addSequential(new DriveForwardAndShoot(0.5)); // <--- Drive speed
		}
	}

	@Override
	public String getAutonomousName() {
		return "KPA Drive and Shoot with boiler on "+(leftSideBoiler?"left":"right");
	}
	
}
