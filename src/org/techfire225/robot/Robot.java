package org.techfire225.robot;


import java.io.IOException;

import org.techfire225.firevision2017.VisionClient;
import org.techfire225.lib.firelog.FireLog;
import org.techfire225.lib.framework.Looper;
import org.techfire225.lib.webapp.ErrorReporter;
import org.techfire225.lib.webapp.Webserver;
import org.techfire225.robot.commands.autonomous.DoNothing;
import org.techfire225.robot.commands.autonomous.GearAndKpaOld;
import org.techfire225.robot.commands.autonomous.GearAndKpa;
import org.techfire225.robot.commands.autonomous.SideGearAndShootPreload;
import org.techfire225.robot.commands.autonomous.SideGearShootPreloadDownfield;
import org.techfire225.robot.commands.autonomous.KPAOnly;
import org.techfire225.robot.commands.autonomous.KPAOnlyFast;
import org.techfire225.robot.commands.autonomous.KPAOnlyFastFar;
import org.techfire225.robot.commands.autonomous.KPAOnlyFastClose;
import org.techfire225.robot.commands.autonomous.KPAOnlyFastCloseDelay;
import org.techfire225.robot.commands.autonomous.SideGear;
import org.techfire225.robot.commands.autonomous.SideGearAndDriveDownfield;
import org.techfire225.robot.commands.autonomous.StraightGear;
import org.techfire225.robot.commands.autonomous.StraightGearAndShootPreload;
import org.techfire225.robot.commands.autonomous.StraightGearShootPreloadDownfield;
import org.techfire225.robot.commands.teleop.Preset;
import org.techfire225.robot.subsystems.Climber;
import org.techfire225.robot.subsystems.Drivetrain;
import org.techfire225.robot.subsystems.GearHolder;
import org.techfire225.robot.subsystems.Hopper;
import org.techfire225.robot.subsystems.Shooter;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends IterativeRobot {
	
	public static Preset currentPreset = null;
	public static Constants constants;
	public static Drivetrain drivetrain;
	public static GearHolder gearHolder;
	public static Hopper hopper;
	public static Shooter shooter;
	public static Climber climber;
	
	public static FireLog logger100hz = null;
	Looper loop100hz;
	

	public static VisionClient visionClient;
	Webserver webserver;
	
	Command autonomousCommand = null;
	
	public Robot() {
		constants = Constants.getConstants();
		
		drivetrain = new Drivetrain();
		gearHolder = new GearHolder();
		hopper = new Hopper();
		shooter = new Shooter();
		climber = new Climber();
		
		drivetrain.resetEncoders();
		
		try {
			logger100hz = new FireLog(1338);
		} catch (IOException e) {
			System.out.println("Failed to start FireLog");
			e.printStackTrace();
			ErrorReporter.report("Couldn't start FireLog");
		}
		
		loop100hz = new Looper(100, logger100hz);
		
		loop100hz.addLoop(drivetrain);
		loop100hz.addLoop(gearHolder);
		loop100hz.addLoop(hopper);
		loop100hz.addLoop(shooter);
		
		new Thread() { 
			public void run() {
				try {
					webserver = new Webserver();
				} catch (Exception e) {
					System.out.println("Failed to start webserver");
					e.printStackTrace();
				}
			}
		}.start();
		
	
		try {
			visionClient = new VisionClient(constants.VisionServerPort, constants.VisionMjpegPort);
		} catch (Exception e) {
			System.out.println("Failed to start VisionServer");
			e.printStackTrace();
			ErrorReporter.report("Couldn't start VisionServer");
		}
		
		visionClient.subscribe(drivetrain);
		visionClient.subscribe(shooter);
		
		OI.init();
		
		AutonomousChooser.getInstance().setAutonomi(
			new Autonomous[] {
    			new DoNothing(),
    			new StraightGear(),
    			new StraightGearAndShootPreload(true),
    			new StraightGearAndShootPreload(false),
    			new StraightGearShootPreloadDownfield(true),
    			new StraightGearShootPreloadDownfield(false),
    			new SideGear(true),
    			new SideGear(false),
    			new SideGearAndShootPreload(true),
    			new SideGearAndShootPreload(false),
    			new SideGearAndDriveDownfield(true),
    			new SideGearAndDriveDownfield(false),
    			new SideGearShootPreloadDownfield(true),
    			new SideGearShootPreloadDownfield(false),
    			new KPAOnlyFastClose(true),
    			new KPAOnlyFastClose(false),
    			new KPAOnly(true),
    			new KPAOnly(false),
    			new GearAndKpa(true),
    			new GearAndKpa(false),
    			new KPAOnlyFast(true),
    			new KPAOnlyFast(false),
    			new KPAOnlyFastFar(true),
    			new KPAOnlyFastFar(false),
    			new KPAOnlyFastCloseDelay(true),
    			new KPAOnlyFastCloseDelay(false),
    			new GearAndKpaOld(true),
    			new GearAndKpaOld(false)
			}
		);
	}
	
	public void disabledInit() {
		loop100hz.disable();
	}
	
	public void disabledPeriodic() {
		AutonomousChooser.getInstance().consoleSelectorUI(OI.autoSelectUp, OI.autoSelectDown);
	}

	public void autonomousInit() {
		Robot.drivetrain.setLowGear(true);
		Robot.drivetrain.resetEncoders();
		Robot.drivetrain.resetGyro();
		loop100hz.enable();
		
		autonomousCommand = AutonomousChooser.getInstance().getSelectedAutonomous();

		if (autonomousCommand != null)
			autonomousCommand.start();
	}
	
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}
	
	public void teleopInit() {
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		Robot.drivetrain.setMode(Drivetrain.Mode.VELOCITY);
		Robot.drivetrain.set(0, 0);
		Robot.drivetrain.setLowGear(false);
		loop100hz.enable();
	}
	
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
}