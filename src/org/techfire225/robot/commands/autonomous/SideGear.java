package org.techfire225.robot.commands.autonomous;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.lib.motion.TrapezoidalMotionProfile;
import org.techfire225.robot.Autonomous;
import org.techfire225.robot.Robot;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.DriveTwoProfile;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.subsystems.GearHolder;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class SideGear extends Autonomous {
	boolean leftAirship;
	public SideGear(boolean leftAirship) {
		this.leftAirship = leftAirship;
		double needsFlip = leftAirship ? -1 : 1;
		
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
			
			addSequential(new DriveDistance(-4, 7, 5));
		}
	}

	@Override
	public String getAutonomousName() {
		return "Side gear with robot on "+(leftAirship?"left":"right")+" of airship";
	}
}
