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

import edu.wpi.first.wpilibj.command.WaitCommand;

public class SideGearShootPreloadDownfield extends Autonomous {
	boolean leftSideBoiler;
	public SideGearShootPreloadDownfield(boolean leftSideBoiler) {
		this.leftSideBoiler = leftSideBoiler;
		double needsFlip = leftSideBoiler ? -1 : 1;
		
		/* Gear */
		{  /* Put gear */
			TrapezoidalMotionProfile distance = new TrapezoidalMotionProfile(10.5, 8, 20); // 9.6, 7, 5 (9.1, 7, 5, 7, 2)
			TrapezoidalMotionProfile angle = new TrapezoidalMotionProfile(-56*needsFlip, 25, 29.5); // -57, 25, 31 (58, 40)
			DriveTwoProfile arcTurn = new DriveTwoProfile(distance, angle);
			
			addSequential(new Shift(true));

			//addSequential(new DriveDistance(0.3, 7, 5, 0, 7));
			addSequential(arcTurn.timeout(2.6)); // .timeout(2.6)
		    //addSequential(new DriveDistance(0.2, 7, 5, 2, 0).timeout(0.5));
			
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
			addSequential(new WaitCommand(0.5));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
			addSequential(new WaitCommand(0.25));
		}
		/* Shoot */
		{
			addSequential(new DriveDistance(-3.5, 6, 7, 0, 6));
			TrapezoidalMotionProfile distance2 = new TrapezoidalMotionProfile(-3, 7, 7, 6, 0);
			TrapezoidalMotionProfile angle2 = new TrapezoidalMotionProfile(-150, 180, 180);
			DriveTwoProfile arcTurn2 = new DriveTwoProfile(distance2, angle2);
			addSequential(new PrepSystem(Preset.FAR));
			addSequential(arcTurn2);
	    	addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
			addSequential(new VisionAlign().timeout(1.0));
			addSequential(new Shoot().timeout(4.0));
			
			addSequential(new PivotTurnTo(-120*needsFlip, !leftSideBoiler));
			
			TrapezoidalMotionProfile distance3 = new TrapezoidalMotionProfile(15, 8, 20, 7, 0); // 9.6, 7, 5 (9.1, 7, 5, 7, 2)
			TrapezoidalMotionProfile angle3 = new TrapezoidalMotionProfile(-56*needsFlip, 25, 29.5); // -57, 25, 31 (58, 40)
			DriveTwoProfile arcTurn3 = new DriveTwoProfile(distance3, angle3);
			
			addSequential(new DriveDistance(10, 7, 5, 0, 7));
			addSequential(arcTurn3.timeout(2.6)); // .timeout(2.6)
		}	
	}

	@Override
	public String getAutonomousName() {
		return "Side gear and shoot preload and drive downfield with boiler on "+(leftSideBoiler?"left":"right");
	}
}
