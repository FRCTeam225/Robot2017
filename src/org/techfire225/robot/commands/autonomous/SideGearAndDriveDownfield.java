package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.DriveTwoProfile;
import org.techfire225.robot.subsystems.GearHolder;

public class SideGearAndDriveDownfield extends Autonomous {
	boolean leftAirship;
	public SideGearAndDriveDownfield(boolean leftAirship) {
		this.leftAirship = leftAirship;
		double needsFlip = leftAirship ? -1 : 1;
		
		{  /* Put gear */
			
			addSequential(new SideGear(leftAirship));
			
			TrapezoidalMotionProfile linear1 = new TrapezoidalMotionProfile(-5, 7, 20, 7, 0); // -5.7, 7, 10
			TrapezoidalMotionProfile angular1 = new TrapezoidalMotionProfile(50*needsFlip, 140, 140);
			addSequential(new DriveTwoProfile(linear1, angular1));
			addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
			
			TrapezoidalMotionProfile distance2 = new TrapezoidalMotionProfile(15, 8, 20, 7, 0); // 9.6, 7, 5 (9.1, 7, 5, 7, 2)
			TrapezoidalMotionProfile angle2 = new TrapezoidalMotionProfile(-56*needsFlip, 25, 29.5); // -57, 25, 31 (58, 40)
			DriveTwoProfile arcTurn2 = new DriveTwoProfile(distance2, angle2);
			
			if (!leftAirship) {
				addSequential(new DriveDistance(10, 7, 5, 0, 7));
				addSequential(arcTurn2.timeout(2.6)); // .timeout(2.6)
			} else {
				addSequential(new DriveDistance(20, 7, 5));
			}
		}
	}

	@Override
	public String getAutonomousName() {
		return "Side gear with robot on "+(leftAirship?"left":"right")+" of airship and drive downfield";
	}
}
