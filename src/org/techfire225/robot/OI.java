package org.techfire225.robot;

import org.techfire225.lib.framework.AxisButton;
import org.techfire225.lib.framework.HoldMode;
import org.techfire225.lib.framework.POVButton;
import org.techfire225.lib.framework.SetMode;
import org.techfire225.robot.commands.drivetrain.DriveDistance;
import org.techfire225.robot.commands.drivetrain.HatAlign;
import org.techfire225.robot.commands.drivetrain.Shift;
import org.techfire225.robot.commands.shooter.AdjustRPM;
import org.techfire225.robot.commands.shooter.Shoot;
import org.techfire225.robot.commands.teleop.DropGear;
import org.techfire225.robot.commands.teleop.PrepSystem;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.commands.teleop.VisionAlign;
import org.techfire225.robot.subsystems.GearHolder;
import org.techfire225.robot.subsystems.Hopper;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {
    public static final int A = 1;
    public static final int B = 2;
    public static final int X = 3;
    public static final int Y = 4;
    
    public static final int LB = 5;
    public static final int RB = 6;
    
    public static final int BACK = 7;
    public static final int START = 8;
	
	public static final int LS = 9;
	public static final int RS = 10;
	
	public static Joystick driver = new Joystick(0);
	public static Joystick operator = new Joystick(1);
	
	public static Button autoSelectUp = new JoystickButton(driver, B);
	public static Button autoSelectDown = new JoystickButton(driver, A);
	
	public static void init() {
		//new JoystickButton(driver, 1).whenPressed(new TurnTo(90, 180, 360));
		/*new JoystickButton(driver, 1).whenPressed(new DriveTwoProfile(
					new TrapezoidalMotionProfile(6, 10, 8),
					TrapezoidalMotionProfile.triangularProfileInDuration(new TrapezoidalMotionProfile(6, 10, 8), 45)
				));
		*/	
		//new JoystickButton(driver, 2).whenPressed(new CheesyDrive()); 
		
		
		// Driver Axis Buttons
		new JoystickButton(driver, B).whenPressed(new DriveDistance(-3.0/12.0, 7, 9).timeout(1));
		
		new AxisButton(driver, B, 0.5).whenPressed(new Shift(true));
    	new AxisButton(driver, X, 0.5).whenPressed(new Shift(false));
    	
    	new JoystickButton(driver, A).whileHeld(new HatAlign());
    	
    	new JoystickButton(driver, LB).whenPressed(new VisionAlign());
    	// Driver Joystick Buttons
    	new JoystickButton(driver, RB).whileHeld(new Shoot());
    			
		
		// Operator Joystick Buttons
    	new JoystickButton(operator, A).whenPressed(new PrepSystem(Preset.OFF));
    	new JoystickButton(operator, B).whileHeld(new HoldMode<Hopper.Mode>(Robot.hopper, Hopper.Mode.INTAKE_AND_AGITATE, Hopper.Mode.OFF));
    	new JoystickButton(operator, X).whenPressed(new PrepSystem(Preset.CLOSE));
    	new JoystickButton(operator, Y).whenPressed(new PrepSystem(Preset.FAR));
    	
    	new JoystickButton(operator, LB).whenPressed(new AdjustRPM(-20));
    	new JoystickButton(operator, RB).whenPressed(new AdjustRPM(20));
    	
    	new JoystickButton(operator, LS).whenPressed(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.FUEL_HOLD));
		new JoystickButton(operator, RS).whenPressed(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.FUEL_LOAD));
		
    	// Operator D-Pad Buttons
    	new POVButton(operator, 0).whenPressed(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.CLOSED));
    	new POVButton(operator, 90).whenPressed(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.PRE_DROP));
    	new POVButton(operator, 180).whenPressed(new DropGear());
    	new POVButton(operator, 270).whenPressed(new SetMode<GearHolder.Mode>(Robot.gearHolder, GearHolder.Mode.GEAR_LOAD));
	}
}