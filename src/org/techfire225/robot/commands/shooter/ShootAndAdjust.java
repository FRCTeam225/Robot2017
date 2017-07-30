package org.techfire225.robot.commands.shooter;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.Hopper;
import org.techfire225.robot.subsystems.Hopper.Mode;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ShootAndAdjust extends CommandGroup {

	public ShootAndAdjust() {
		addSequential(new SetMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.PREPSHOT));
		addSequential(new WaitCommand(0.25));
		addParallel(new DoShoot());
		addParallel(new DoRPMUpdate());
	}
	
	@Override
	protected void end() {
		Robot.hopper.setMode(Mode.OFF);
	}
	
	@Override
	protected void interrupted() {
		Robot.hopper.setMode(Mode.OFF);
	}
	
	public static class DoRPMUpdate extends Command {

		public DoRPMUpdate() {
			requires(Robot.shooter);
		}
		
		public void execute() {
			
		}
		
		@Override
		protected boolean isFinished() {
			return true;
		}
		
	}
	
	public static class DoShoot extends Command {
		double startAngle = 0;
		
		public DoShoot() {
			requires(Robot.hopper);
		}
		
		protected void initialize() {
			if ( Robot.currentPreset == null || Robot.shooter.getCurrentMode() == Shooter.Mode.OFF )
				return;
			
			startAngle = Robot.drivetrain.getAngle();
		}
		
		protected void execute() {
			if ( Robot.currentPreset == null )
				return;
			
			Robot.hopper.setMode(Robot.currentPreset.hopperShootMode);
			/*
			if ( Robot.shooter.atSetpoint() )
				Robot.hopper.setMode(Robot.currentPreset.hopperShootMode);
			else
				Robot.hopper.setMode(Hopper.Mode.OFF);
			*/
		}
	
		@Override
		protected boolean isFinished() {
			// Bail out if:
			// timed out
			// No preset
			// Shooter isn't running
			// Robot is turned more than 10 degrees from start
			return isTimedOut() || 
					Robot.currentPreset == null || 
					Robot.shooter.getCurrentMode() == Shooter.Mode.OFF;
		}
		
		protected void end() {
			Robot.hopper.setMode(Mode.OFF);
		}
	}
}
