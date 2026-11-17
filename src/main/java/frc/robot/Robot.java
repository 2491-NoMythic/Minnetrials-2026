// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.security.spec.ECFieldFp;

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

  private final XboxController driveController;
  private final XboxController operatorController;
  
  private int state;
  private Timer autoTimer;
  private double mulTplIR;

  //constants
  private double AUTO_DRIVE_SPEED;
  private double END_EFFECTOR_SPEED;
  private double ELEVATOR_SPEED;
  private double ELEVATOR_UP;
  private double LOW_OVEN_HEIGHT;

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

    AUTO_DRIVE_SPEED = 2491;
    END_EFFECTOR_SPEED = 2491;
    ELEVATOR_SPEED = 2491;
    ELEVATOR_UP = 2491;
    LOW_OVEN_HEIGHT = 2491;

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

    autoTimer.reset();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {

      case twoCookieLeft:
        twoCookieLeft();
        break;
      case twoCookieRight:
        twoCookieRight();
        break;
      case oneCookieLeft:
        oneCookieLeft();
        break;
      case oneCookieRight:
        oneCookieRight();
        break;
      case oneCookieOvenB:
        oneCookieOvenBlue();
        break;
      case oneCookieOvenR:
        oneCookieOvenRed();
        break;
      case twoCookieOvenB:
        twoCookieOvenBlue();
        break; 
      case twoCookieOvenR:
        twoCookieOvenRed();
        break;
      default:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        if (autoTimer.get() > 1) {
          driveAuto(RobotSide.both, 0.0);
        }
        break;
    }
  }


  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    //drive slower if the elevator is up
    if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
      mulTplIR = 2491;
    } else  {
      mulTplIR = 24.91;
    }

    // Drive control using the left joystick on the drive controller. Forwad, backward, turn, or a combination
    lDriveMotor1.set(mulTplIR * (driveController.getLeftY() + driveController.getLeftX()));
    lDriveMotor2.set(mulTplIR * (driveController.getLeftY() + driveController.getLeftX()));
    rDriveMotor1.set(mulTplIR * (driveController.getLeftY() - driveController.getLeftX()));
    rDriveMotor2.set(mulTplIR * (driveController.getLeftY() - driveController.getLeftX()));

    // Front end effector control using the left bumper button on the operator controller
    if (driveController.getLeftBumperButton()) {
      frontEndEffectorMotor.set(END_EFFECTOR_SPEED);
    } else {
      frontEndEffectorMotor.stopMotor();
    }

    // Back end effector control using the right bumper button on the operator controller
    if (driveController.getRightBumperButton()) {
      backEndEffectorMotor.set(END_EFFECTOR_SPEED);
    } else {
      backEndEffectorMotor.stopMotor();
    }

    // Elevator control
    if (operatorController.getRightTriggerAxis() > 0.5 && elevatorMotor.getPosition().getValueAsDouble() < 2491) {
      //go up if the elevator is not at the top limit
      elevatorMotor.set(ELEVATOR_SPEED);

    } else if (operatorController.getLeftTriggerAxis() > 0.5 && elevatorMotor.getPosition().getValueAsDouble() > 0) {
      //go down if the elevator is not at the bottom limit
      elevatorMotor.set(-ELEVATOR_SPEED);

    } else if (operatorController.getRightStickButton()) {
      // the elevator goes allll the way down ):
      elevatorMotor.setPosition(0);
    
    } else if (operatorController.getLeftStickButton()) {
      // the elevator goes aaaal the way up (:
      elevatorMotor.setPosition(ELEVATOR_UP);

    } else if (operatorController.getLeftBumperButton()) {
      // the elvatorrr goes aalll the way to the low oven hight set point ¯\_(ツ)_/¯
      elevatorMotor.setPosition(LOW_OVEN_HEIGHT);

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
        rDriveMotor1.set(0.0);
        rDriveMotor2.set(0.0);
        break;
      case right:
        rDriveMotor1.set(Speed);
        rDriveMotor2.set(Speed);
        lDriveMotor1.set(0.0);
        lDriveMotor2.set(0.0);
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
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.left, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 7:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 8:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.right, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 9:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 10:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 11:
        break;   
      default:
        break;
    }
  }

  // Two Cookie Oven Red Auto
  public void twoCookieOvenRed() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.right, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 7:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 8:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.left, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 9:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 10:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 11:
        break;   
      default:
        break;
    }
  }

  // One Cookie Oven Blue Auto
  public void oneCookieOvenBlue() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1: 
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.right, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both,-AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.right, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 7:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 11:
        break;   
      default:
        break;
    }
  }

  // One Cookie Oven Red Auto
  public void oneCookieOvenRed() {
     switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1: 
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.left, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.left, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 7:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 11:
        break;   
      default:
        break;
    }
  }

  // One Cookie Left Auto
  public void oneCookieLeft() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1: 
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.left, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both,0.0);
          state++;
        }
        break;
      case 5:
        break;   
      default:
        break;
    }
  }

  // One Cookie Right Auto
  public void oneCookieRight() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1: 
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.right, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          backEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.both,0.0);
          state++;
        }
        break;
      case 5:
        break;   
      default:
        break;
    }
  }

  // Two Cookie Left Auto
  public void twoCookieLeft() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.right, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.both, ELEVATOR_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 7:
        break;
      default:
        break;
    }
  }

  // Two Cookie Right Auto
  public void twoCookieRight() {
    switch (state) {
      case 0:
        autoTimer.start();
        driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
        state++;
        break;
      case 1:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          frontEndEffectorMotor.set(END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 2:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.stopMotor();
          driveAuto(RobotSide.left, -AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 3:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          elevatorMotor.set(ELEVATOR_SPEED);
          state++;
        }
        break;
      case 4:
        if (elevatorMotor.getPosition().getValueAsDouble() > 2491) {
          elevatorMotor.stopMotor();
          driveAuto(RobotSide.both, AUTO_DRIVE_SPEED);
          state++;
        }
        break;
      case 5:
        if (autoTimer.get() > 2491) {
          driveAuto(RobotSide.both, 0.0);
          state++;
        }
        break;
      case 6:
        if (autoTimer.get() > 2491) {
          frontEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          backEndEffectorMotor.set(-END_EFFECTOR_SPEED);
          state++;
        }
        break;
      case 7:
        break;
      default:
        break;
    }
  }
}
