package org.techfire225.robot.subsystems;

import org.techfire225.firevision2017.VisionMessage;
import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.lib.framework.TFSubsystem;
import org.techfire225.lib.framework.InterpolatingTree;
import org.techfire225.lib.framework.Publisher;
import org.techfire225.robot.Constants;
import org.techfire225.robot.PortMap;
import org.techfire225.robot.commands.drivetrain.CheesyDrive;
import org.techfire225.robot.drivetrain.controllers.DrivetrainController;
import org.techfire225.robot.drivetrain.controllers.HatAlignController;
import org.techfire225.robot.drivetrain.controllers.TurnPIDController;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Drivetrain extends TFSubsystem<Drivetrain.Mode> implements Publisher.Subscriber<VisionMessage> {
	
	public enum Mode {
		VELOCITY,
		VISION,
		CONTROLLED
	};
	
	public enum LoopMode {
		CLOSED,
		OPEN
	}
	
	CANTalon[] leftMotors;
	CANTalon[] rightMotors;
	
	LoopMode loopMode = LoopMode.CLOSED;
	
	boolean isLowGear = false;
	
	Solenoid shift = new Solenoid(PortMap.SHIFTER_SOLENOID);
	public Compressor compressor = new Compressor(0);
	
	ADXRS450_Gyro gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
	
	// Store 1 second of state history
	InterpolatingTree gyroHistory = new InterpolatingTree(100);
	
	Constants constants;
	
	DrivetrainController controller = null;
	
	TurnPIDController visionController = new TurnPIDController();
	
	volatile double lastVisionSetpoint = 0;
	public HatAlignController hatAlignController = new HatAlignController();
	
	PowerDistributionPanel pdp;
	
	double REMOVEME = 0;
	public Drivetrain() {
		pdp = new PowerDistributionPanel();
		
		constants = Constants.getConstants();
		
		leftMotors = new CANTalon[]{new CANTalon(PortMap.LEFT_DRIVE[0]), new CANTalon(PortMap.LEFT_DRIVE[1]), new CANTalon(PortMap.LEFT_DRIVE[2])};
		rightMotors = new CANTalon[]{new CANTalon(PortMap.RIGHT_DRIVE[0]), new CANTalon(PortMap.RIGHT_DRIVE[1]), new CANTalon(PortMap.RIGHT_DRIVE[2])};
		
		// Setup for follower mode
		for ( CANTalon left : leftMotors ) {
			left.changeControlMode(TalonControlMode.Follower);
			left.enableBrakeMode(false);
		}
		
		for ( CANTalon right : rightMotors ) {
			right.changeControlMode(TalonControlMode.Follower);
			right.enableBrakeMode(false);
		}
		
		leftMotors[0].enableBrakeMode(true);
		rightMotors[0].enableBrakeMode(true);

		leftMotors[1].enableBrakeMode(true);
		rightMotors[1].enableBrakeMode(true);
		
		leftMotors[1].set(PortMap.LEFT_DRIVE[0]);
		leftMotors[2].set(PortMap.LEFT_DRIVE[0]);
		
		rightMotors[1].set(PortMap.RIGHT_DRIVE[0]);
		rightMotors[2].set(PortMap.RIGHT_DRIVE[0]);
		// End setup for follower mode
		
		leftMotors[0].setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 10);
		rightMotors[0].setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 10);
		
		leftMotors[0].setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rightMotors[0].setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		
		//leftMotors[0].configEncoderCodesPerRev(codesPerRev);
		
		// Flip outputs here
		rightMotors[0].reverseSensor(false);
		leftMotors[0].reverseSensor(false);
		
		leftMotors[0].setInverted(false);
		rightMotors[0].setInverted(false);
		
		leftMotors[0].changeControlMode(TalonControlMode.PercentVbus);
		rightMotors[0].changeControlMode(TalonControlMode.PercentVbus);
		
		resetEncoders();
		setMode(Mode.VELOCITY);
		setLowGear(true);
		set(0, 0);
		
		updateDriveVelocityPID();
	}
	
	
	public double getDistance() {
		return ((-leftMotors[0].getPosition() + rightMotors[0].getPosition())/2.0)*constants.DriveEncoder_TickConversion;
	}
	
	public double getLeftDistance() {
		return -leftMotors[0].getPosition()*constants.DriveEncoder_TickConversion;
	}
	
	public double getRightDistance() {
		return rightMotors[0].getPosition()*constants.DriveEncoder_TickConversion;
	}
	
	public void resetEncoders() {
		leftMotors[0].setPosition(0);
		rightMotors[0].setPosition(0);
	}
	
	public double getAngle() {
		return gyro.getAngle();
	}
	
	public void resetGyro() {
		gyro.reset();
	}
	
	public void setNormalizedVelocity(double left, double right) {
		double maxVel = constants.MaxDriveVelocity;
		setVelocity(left*maxVel, right*maxVel);
	}
	
	public void setVelocity(double left, double right) {
		if ( loopMode == LoopMode.OPEN ) {
			leftMotors[0].changeControlMode(TalonControlMode.Speed);
			rightMotors[0].changeControlMode(TalonControlMode.Speed);
			updateDriveVelocityPID();
			loopMode = LoopMode.CLOSED;
		}
		REMOVEME = right;
		leftMotors[0].set(-left);
		rightMotors[0].set(right);
	}
	
	public void setVelocityComponents(double linearV, double omega) {
		double wheelbase = 19.25/12.0;
		
		double left = (linearV-(omega*wheelbase));
		double right = (linearV+(omega*wheelbase));
		
		left *= 1.0 / constants.DriveEncoder_VelocityConversion;
		right *= 1.0 / constants.DriveEncoder_VelocityConversion;
		System.out.println("Commanded vel "+linearV+" omega "+omega);
		setVelocity(left, right);
	}
	
	public void set(double left, double right) {
		if ( loopMode == LoopMode.CLOSED ) {
			leftMotors[0].changeControlMode(TalonControlMode.PercentVbus);
			rightMotors[0].changeControlMode(TalonControlMode.PercentVbus);
			loopMode = LoopMode.OPEN;
		}
		leftMotors[0].set(-left);
		rightMotors[0].set(right);
	}
	
	public void setLowGear(boolean lowGear) {
		isLowGear = lowGear;
		shift.set(!lowGear);
		updateDriveVelocityPID();
	}

	public boolean isLowGear() {
		return isLowGear;
	}

	// If the user asks for vision mode, automatically set controller
	public synchronized void setMode(Mode mode) {
		if ( mode == Mode.VISION ) {
			// Reset controller & set it to our current position
			visionController.reset();
			visionController.setAngle(getAngle());
			
			setController(visionController);
		}
		
		super.setMode(mode);
	}

	@Override
	public synchronized void update(FireLogSample log) {
		gyroHistory.put(Timer.getFPGATimestamp(), getAngle());
		
		Mode mode = getCurrentMode();
		log.put("drivetrainMode", mode.toString());
		
		//log.put("drivetrainDistance", getDistance());
		log.put("drivetrainLeftDistance", getLeftDistance());
		log.put("drivetrainRightDistance", getRightDistance());
		log.put("drivetrainAngle", getAngle());
		
		/*
		// Log battery voltage at each talon
		log.put("drivetrainLeftVBus", leftMotors[0].getBusVoltage());
		log.put("drivetrainLeftSlaveVBus", leftMotors[1].getBusVoltage());
		
		log.put("drivetrainRightVBus", rightMotors[0].getBusVoltage());
		log.put("drivetrainRightSlaveVBus", rightMotors[1].getBusVoltage());
		
		// Log current
		log.put("drivetrainLeftAmps", leftMotors[0].getOutputCurrent());
		log.put("drivetrainLeftSlaveAmps", leftMotors[1].getOutputCurrent());
		
		log.put("drivetrainRightAmps", rightMotors[0].getOutputCurrent());
		log.put("drivetrainRightSlaveAmps", rightMotors[1].getOutputCurrent());
		
		// Log commanded %Vbus
		log.put("drivetrainLeftCommand", leftMotors[0].get());
		log.put("drivetrainRightCommand", rightMotors[0].get());
		*/
		
		switch (mode) {
			case VELOCITY:
				break;
			case CONTROLLED:
			case VISION:
				if ( controller != null )
					onTarget = controller.update();
				break;
		}

		log.put("drivetrainLinearPos", Math.abs(REMOVEME));
		log.put("drivetrainLinearActual", Math.abs(rightMotors[0].getSpeed()));
		//log.put("drivetrainLinearError", rightMotors[0].getError());
		
		log.put("drivetrainAngularPos", 0);
		log.put("drivetrainAngularActual", 0);
		log.put("drivetrainAngularError", 0);
/*
		if ( controller != null ) {
			log.put("drivetrainLinearPos", controller.getLinearSetpoint());
			log.put("drivetrainLinearActual", controller.getLinearActual());
			log.put("drivetrainLinearError", controller.getLinearError());
			
			log.put("drivetrainAngularPos", controller.getAngularSetpoint());
			log.put("drivetrainAngularActual", controller.getAngularActual());
			log.put("drivetrainAngularError", controller.getAngularError());
		}
		else {
			log.put("drivetrainLinearPos", 0);
			log.put("drivetrainLinearActual", 0);
			log.put("drivetrainLinearError", 0);
		}
*/
	}
	
	public synchronized void setController(DrivetrainController controller) {
		this.controller = controller;
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new CheesyDrive());
	}

	@Override
	public synchronized void receiveMessage(VisionMessage target) {
		if ( target.found ) {
			Double setPoint = gyroHistory.getInterpolated(target.timestamp);
			if ( setPoint == null )
				setPoint = getAngle();
			
			if ( target.found ) {
				setPoint += target.theta;
				visionController.setAngle(setPoint);
				lastVisionSetpoint = setPoint;
			}
			
		}
	}
	
	public double getLastVisionSetpoint() {
		return lastVisionSetpoint;
	}

	@Override
	public void refreshConstants() {
		if ( controller != null )
			controller.refreshConstants();
		updateDriveVelocityPID();
	}
	
	private void updateDriveVelocityPID() {
		leftMotors[0].setVoltageRampRate(constants.DriveRampRate);
		rightMotors[0].setVoltageRampRate(constants.DriveRampRate);
		
		if ( !isLowGear() ) {
			leftMotors[0].setPID(constants.LeftDriveVelocityP, 0, constants.LeftDriveVelocityD, constants.LeftDriveVelocityF, 0, 200, 0);
			rightMotors[0].setPID(constants.RightDriveVelocityP, 0, constants.RightDriveVelocityD, constants.RightDriveVelocityF, 0, 200, 0);
		}
		else {
			leftMotors[0].setPID(constants.LeftDriveVelocityPLow, 0, constants.LeftDriveVelocityDLow, constants.LeftDriveVelocityFLow, 0, 0, 0);
			rightMotors[0].setPID(constants.RightDriveVelocityPLow, 0, constants.RightDriveVelocityDLow, constants.RightDriveVelocityFLow, 0, 0, 0);
		}
	}
}
