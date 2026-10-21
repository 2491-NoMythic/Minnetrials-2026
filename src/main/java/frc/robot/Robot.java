// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix6.hardware.TalonFX;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  TalonFX lDriveMotor1;
  TalonFX lDriveMotor2;
  TalonFX rDriveMotor1;
  TalonFX rDriveMotor2;
  TalonFX frontEndEffector;
  TalonFX backEndEffector;

  private final XboxController driveController;
  private final XboxController operatorController;

  Timer autoTimer;

  public enum RobotSide{
    left,
    right,
    both,
  }

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    lDriveMotor1 = new TalonFX(2491);
    lDriveMotor2 = new TalonFX(2491);
    rDriveMotor1 = new TalonFX(2491);
    rDriveMotor2 = new TalonFX(2491);
    frontEndEffector = new TalonFX(2491);
    backEndEffector = new TalonFX(2491);
    
    driveController = new XboxController(0);
    operatorController = new XboxController(1);

    autoTimer = new Timer();

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  

  //Turn left, turn right, drive straight, stop. For Autonomous.
  public void driveAuto(RobotSide robotSide, Double Speed) {
    switch (robotSide) {
      case left:
        lDriveMotor1.set(Speed);
        lDriveMotor2.set(Speed);
        break;
      case right:
        rDriveMotor1.set(Speed);
        rDriveMotor2.set(Speed);
        break;
      case both:
        rDriveMotor2.set(Speed);
        lDriveMotor1.set(Speed);
        lDriveMotor2.set(Speed);
        rDriveMotor1.set(Speed);
        break;
      default:
        rDriveMotor2.stopMotor();
        lDriveMotor1.stopMotor();
        lDriveMotor2.stopMotor();
        rDriveMotor1.stopMotor();
        System.out.println("😊");
        break;
      }
    }
   

  /** ---------------------------------------------------------------------------------------------------------
   * ⬇️This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating. 
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {

      //temporary auto program: straight, stop then turn 90deg right, straight.
      case kCustomAuto:
        autoTimer.start();
        driveAuto(RobotSide.both, 2491.0);
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffector.set(2491);

        } else if (autoTimer.get() > 2491) {
          frontEndEffector.stopMotor();
          driveAuto(RobotSide.right, -2491.0);

        } else if (autoTimer.get() > 2491){
          driveAuto(RobotSide.both, 2491.0);

        } else if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffector.set(-2491);
          backEndEffector.set(-2491);
        }
        break;

      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    lDriveMotor1.set(driveController.getLeftY() + driveController.getLeftX());
    lDriveMotor2.set(driveController.getLeftY() + driveController.getLeftX());
    rDriveMotor1.set(driveController.getLeftY() - driveController.getLeftX());
    rDriveMotor2.set(driveController.getLeftY() - driveController.getLeftX());

    if (operatorController.getLeftBumperButton()) {
      frontEndEffector.set(2491);
    } else {
      frontEndEffector.stopMotor();
    };

    if (operatorController.getRightBumperButton()) {
      backEndEffector.set(2491);
    } else {
      backEndEffector.stopMotor();
    };
  };

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
