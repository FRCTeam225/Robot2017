package org.techfire225.robot.commands.teleop;

import org.techfire225.lib.framework.SetMode;
import org.techfire225.robot.Robot;
import org.techfire225.robot.subsystems.GearHolder;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class DropGear extends CommandGroup {

    public DropGear() {
        addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
        addSequential(new WaitCommand(0.25));
        addSequential(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.DROP));
    }
}
