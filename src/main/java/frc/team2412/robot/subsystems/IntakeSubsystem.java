package frc.team2412.robot.subsystems;

import static frc.team2412.robot.Hardware.*;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Map;

public class IntakeSubsystem extends SubsystemBase {
	// Constants
	public static final double INTAKE_IN_SPEED = 0.3;
	public static final double INTAKE_REVERSE_SPEED = -0.7;

	public static final double INDEX_IN_SPEED = 0.3;
	public static final double INDEX_REVERSE_SPEED = -0.3;

	public static final double FEEDER_IN_SPEED = 0.3;
	public static final double FEEDER_REVERSE_SPEED = -0.3;

	// Motors
	private final CANSparkMax intakeMotorFront;
	private final CANSparkMax intakeMotorBack;
	private final CANSparkMax intakeMotorLeft;
	private final CANSparkMax intakeMotorRight;

	private final CANSparkFlex indexMotorLower;
	private final CANSparkFlex indexMotorUpper;

	private final CANSparkFlex feederMotor;
	private final RelativeEncoder feederEncoder;

	// Sensors
	private final DigitalInput indexSensor;
	private final DigitalInput feederSensor;

	// Shuffleboard
	private final GenericEntry setIntakeInSpeedEntry =
			Shuffleboard.getTab("Intake")
					.addPersistent("Intake in speed - ", INTAKE_IN_SPEED)
					.withSize(2, 1)
					.withProperties(Map.of("Min", -1, "Max", 1))
					.getEntry();

	private final GenericEntry setIndexInSpeedEntry =
			Shuffleboard.getTab("Intake")
					.add("Index in speed - ", INDEX_IN_SPEED)
					.withSize(1, 1)
					.getEntry();

	private final GenericEntry setFeederInSpeedEntry =
			Shuffleboard.getTab("Intake")
					.add("Feeder in speed - ", FEEDER_IN_SPEED)
					.withSize(1, 1)
					.getEntry();

	public IntakeSubsystem() {
		intakeMotorFront = new CANSparkMax(INTAKE_MOTOR_FRONT, MotorType.kBrushless);
		intakeMotorBack = new CANSparkMax(INTAKE_MOTOR_BACK, MotorType.kBrushless);
		intakeMotorLeft = new CANSparkMax(INTAKE_MOTOR_LEFT, MotorType.kBrushless);
		intakeMotorRight = new CANSparkMax(INTAKE_MOTOR_RIGHT, MotorType.kBrushless);

		indexMotorLower = new CANSparkFlex(INDEX_MOTOR_LOWER, MotorType.kBrushless);
		indexMotorUpper = new CANSparkFlex(INDEX_MOTOR_UPPER, MotorType.kBrushless);

		feederMotor = new CANSparkFlex(FEEDER_MOTOR, MotorType.kBrushless);
		feederEncoder = feederMotor.getEncoder();

		indexSensor = new DigitalInput(INDEX_SENSOR);
		feederSensor = new DigitalInput(FEEDER_SENSOR);

		resetMotors();

		ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Intake");
		shuffleboardTab.addBoolean("Index Sensor - ", indexSensor::get).withSize(1, 1);
		shuffleboardTab.addBoolean("Feeder Sensor - ", feederSensor::get).withSize(1, 1);
	}

	private void configureMotor(CANSparkBase motor) {
		motor.restoreFactoryDefaults();
		motor.setIdleMode(IdleMode.kBrake);
		motor.setSmartCurrentLimit(20);
		motor.burnFlash();
	}

	private void resetMotors() {
		configureMotor(intakeMotorFront);
		configureMotor(intakeMotorBack);
		configureMotor(intakeMotorLeft);
		configureMotor(intakeMotorRight);
		intakeMotorBack.follow(intakeMotorFront);
		intakeMotorLeft.follow(intakeMotorFront);
		intakeMotorRight.follow(intakeMotorFront);

		configureMotor(indexMotorLower);
		configureMotor(indexMotorUpper);
		indexMotorLower.follow(indexMotorUpper);

		configureMotor(feederMotor);
	}

	// intake methods
	public void intakeIn() {
		intakeMotorFront.set(setIntakeInSpeedEntry.getDouble(INTAKE_IN_SPEED));
	}

	public void intakeReverse() {
		intakeMotorFront.set(INTAKE_REVERSE_SPEED);
	}

	public void intakeStop() {
		intakeMotorFront.set(0);
	}

	// index methods
	public void indexIn() {
		indexMotorUpper.set(setIndexInSpeedEntry.getDouble(INDEX_IN_SPEED));
		indexMotorLower.set(setIndexInSpeedEntry.getDouble(INDEX_IN_SPEED));
	}

	public void indexReverse() {
		indexMotorUpper.set(INDEX_REVERSE_SPEED);
		indexMotorLower.set(INDEX_REVERSE_SPEED);
	}

	public void indexStop() {
		indexMotorUpper.set(0);
		indexMotorLower.set(0);
	}

	// feeder methods
	public void feederIn() {
		feederMotor.set(setFeederInSpeedEntry.getDouble(FEEDER_IN_SPEED));
	}

	public void feederReverse() {
		feederMotor.set(FEEDER_REVERSE_SPEED);
	}

	public void feederStop() {
		feederMotor.set(0);
	}

	// sensor methods
	public boolean getIndexSensor() {
		return indexSensor.get();
	}

	public boolean getFeederSensor() {
		return feederSensor.get();
	}
}
