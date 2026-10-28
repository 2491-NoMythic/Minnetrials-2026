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
import com.ctre.phoenix6.signals.NeutralModeValue;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private static final String twoCookieOvenB = "Two Cookie Oven 🟦";
  private static final String twoCookieOvenR = "Two Cookie Oven 🟥";
  private static final String oneCookieOvenB = "One Cookie Oven 🟦";
  private static final String oneCookieOvenR = "One Cookie Oven 🟥";
  private static final String oneCookieLeft = "One Cookie Left ";
  private static final String oneCookieRight = "One Cookie Right";
  private static final String twoCookieRight = "Two Cookie Right";
  private static final String twoCookieLeft = "Two Cookie Left";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  TalonFX lDriveMotor1;
  TalonFX lDriveMotor2;
  TalonFX rDriveMotor1;
  TalonFX rDriveMotor2;
  TalonFX frontEndEffectorMotor;
  TalonFX backEndEffectorMotor;
  TalonFX elevatorMotor;

  private int state;
  private Timer autoTimer;

  private final XboxController driveController;
  private final XboxController operatorController;

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
    frontEndEffectorMotor = new TalonFX(2491);
    backEndEffectorMotor = new TalonFX(2491);
    elevatorMotor = new TalonFX(2491);
    
    driveController = new XboxController(0);
    operatorController = new XboxController(1);

    state = 0;
    autoTimer = new Timer();

    m_chooser.addOption("One Cookie Oven 🟦", oneCookieOvenB);
    m_chooser.addOption("One Cookie Oven 🟥", oneCookieOvenR);
    m_chooser.addOption("Two Cookie Oven 🟦", twoCookieOvenB);
    m_chooser.addOption("Two Cookie Oven 🟥", twoCookieOvenR);
    m_chooser.addOption("One Cookie Left ", oneCookieLeft);
    m_chooser.addOption("One Cookie Right", oneCookieRight);
    m_chooser.addOption("Two Cookie Left", twoCookieLeft);
    m_chooser.addOption("Two Cookie Right", twoCookieRight);
    SmartDashboard.putData("Auto choices", m_chooser);
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

      case twoCookieLeft:
        
        break;

      case twoCookieRight:
      // Put two cookie right code here
        break;
      case oneCookieLeft:
      // Put one cookie left code here
        break;
      case oneCookieRight:
      // Put one cookie right code here
        break;
      case oneCookieOvenB:
      // Put one cookie oven blue code here
        break;
      case oneCookieOvenR:
      // Put one cookie oven red code here
        break;
      case twoCookieOvenB:
        twoCookieOvenBlue();
        break;
      //auto done 
      case twoCookieOvenR:
      // Put two cookie oven red code here
        break;
      default:
        driveAuto(RobotSide.both, 2491.0);
        break;
    }
  }


  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    // Drive control using the left joystick on the drive controller. Forwad, backward, turn, or a combination
    lDriveMotor1.set(driveController.getLeftY() + driveController.getLeftX());
    lDriveMotor2.set(driveController.getLeftY() + driveController.getLeftX());
    rDriveMotor1.set(driveController.getLeftY() - driveController.getLeftX());
    rDriveMotor2.set(driveController.getLeftY() - driveController.getLeftX());

    // Front end effector control using the left bumper button on the operator controller
    if (operatorController.getLeftBumperButton()) {
      frontEndEffectorMotor.set(2491);
    } else {
      frontEndEffectorMotor.stopMotor();
    }

    // Back end effector control using the right bumper button on the operator controller
    if (operatorController.getRightBumperButton()) {
      backEndEffectorMotor.set(2491);
    } else {
      backEndEffectorMotor.stopMotor();
    }

    // Elevator control using POV (D-pad) on the operator controller
    if (operatorController.getPOV() == 0 && elevatorMotor.getPosition().getValueAsDouble() < 2491) {
      //go up if the elevator is not at the top limit
      elevatorMotor.set(2491);

    } else if (operatorController.getPOV() == 180 && elevatorMotor.getPosition().getValueAsDouble() > 0) {
      //go down if the elevator is not at the bottom limit
      elevatorMotor.set(-2491);

    } else {
      elevatorMotor.stopMotor();
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    lDriveMotor1.setNeutralMode(NeutralModeValue.Coast);
    lDriveMotor2.setNeutralMode(NeutralModeValue.Coast);
    rDriveMotor1.setNeutralMode(NeutralModeValue.Coast);
    rDriveMotor2.setNeutralMode(NeutralModeValue.Coast);
    frontEndEffectorMotor.setNeutralMode(NeutralModeValue.Coast);
    backEndEffectorMotor.setNeutralMode(NeutralModeValue.Coast);
    elevatorMotor.setNeutralMode(NeutralModeValue.Coast);
  }

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


  //Turn left, turn right, drive straight, or stop. For Autonomous.
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
        break;
      }
    }

  // Two Cookie Oven Blue Auto
  public void twoCookieOvenBlue() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, 2491.0);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(2491);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.left, -2491.0);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both,2491.0);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(2491);
          state++;
        }
        break;
      case 5:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          frontEndEffectorMotor.set(-2491);
          backEndEffectorMotor.set(-2491);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.right, -2491.0);
          state++;
        }
        break;
      case 7:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, -2491.0);
          state++;
        }
        break;
      case 8:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 9:
        break;   
      default:
        break;
    }
  }

  // Two Cookie Oven Red Auto
  public void twoCookieOvenRed() {}

  // One Cookie Oven Blue Auto
  public void oneCookieOvenBlue() {}

  // One Cookie Oven Red Auto
  public void oneCookieOvenRed() {}

  // One Cookie Left Auto
  public void oneCookieLeft() {}

  // One Cookie Right Auto
  public void oneCookieRight() {}

  // Two Cookie Left Auto
  public void twoCookieLeft() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, 2491.0);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(2491);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.right, -2491.0);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 2491.0);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(2491);
          state++;
        }
        break;
      case 5:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          frontEndEffectorMotor.set(-2491);
          backEndEffectorMotor.set(-2491);
          state++;
        }
        break;
      case 6:
        break;
      default:
        break;
    }
  }

  // Two Cookie Right Auto
  public void twoCookieRight() {}
}
