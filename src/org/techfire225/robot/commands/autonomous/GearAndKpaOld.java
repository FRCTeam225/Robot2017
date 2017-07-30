package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
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

public class GearAndKpaOld extends Autonomous {
	boolean leftSideBoiler;
	public GearAndKpaOld(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		
		{  /* Put gear */
			TrapezoidalMotionProfile distance = new TrapezoidalMotionProfile(10.5, 8, 20); // 9.6, 7, 5 (9.1, 7, 5, 7, 2)
			TrapezoidalMotionProfile angle = new TrapezoidalMotionProfile(-56*needsFlip, 25, 29.5); // -57, 25, 31 (58, 40)
			DriveTwoProfile arcTurn = new DriveTwoProfile(distance, angle);
			
			addSequential(new Shift(true));

			//addSequential(new DriveDistance(0.3, 7, 5, 0, 7));
			addSequential(arcTurn.timeout(2.6)); // .timeout(2.6)
		    //addSequential(new DriveDistance(0.2, 7, 5, 2, 0).timeout(0.5));
			
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
			addSequential(new WaitCommand(0.25));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
			addSequential(new WaitCommand(0.1));
		}
		{ /* Back up and go to hopper */
			TrapezoidalMotionProfile linear1 = new TrapezoidalMotionProfile(-5, 8, 20, 8, 0); // -5.7, 7, 10
			TrapezoidalMotionProfile angular1 = new TrapezoidalMotionProfile(50*needsFlip, 140, 140);
			
			TrapezoidalMotionProfile linear2 = new TrapezoidalMotionProfile(9.1, 8, 20, 0, 4);
			TrapezoidalMotionProfile angular2 = new TrapezoidalMotionProfile(85*needsFlip, 140, 140); // (90*needsFlip, 80, 100)
			
			addSequential(new DriveDistance(-1.9, 8, 20, 0, 8));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
			addSequential(new DriveTwoProfile(linear1, angular1));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.FUEL_LOAD));
			addSequential(new DriveTwoProfile(linear2, angular2, 2.5)); // .timeout(2)
			// Smash the wall
			//addParallel(new DriveForTime(0.5, 0.7));
		}
		{  /* Shoot from hopper */
			addSequential(new PrepSystem(Preset.FAR));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.AGITATE));
			addSequential(new WaitCommand(1.25));
			addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.OFF));
			addSequential(new PivotTurnTo(70*needsFlip, leftSideBoiler));
			//addSequential(new DriveDistance(1.5, 7, 9).timeout(1));
			addSequential(new VisionAlign().timeout(1.0));
			addSequential(new Shoot());
		}
	}
	
	/*
	public static class ShootAfter extends CommandGroup {
		public ShootAfter() {
			addSequential(new WaitCommand(0.5));
			addSequential(new Shoot());
		}
	}
	*/


	@Override
	public String getAutonomousName() {
		return "OLD Gear and KPA with boiler on "+(leftSideBoiler?"left":"right");
	}
}
