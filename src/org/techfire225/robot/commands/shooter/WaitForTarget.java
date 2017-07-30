package org.techfire225.robot.commands.shooter;

import org.techfire225.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class WaitForTarget extends Command {

	@Override
	protected boolean isFinished() {
		return Robot.shooter.hasTarget();
	}

}
