package org.sciborgs1155.robot;

import static edu.wpi.first.wpilibj2.command.button.RobotModeTriggers.*;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import monologue.Annotations.Log;
import monologue.Logged;
import monologue.Monologue;
import org.littletonrobotics.urcl.URCL;
import org.sciborgs1155.lib.CommandRobot;
import org.sciborgs1155.lib.FaultLogger;
import org.sciborgs1155.robot.Ports.OI;
import org.sciborgs1155.robot.drive.Drive;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class Robot extends CommandRobot implements Logged {

  // INPUT DEVICES
  private final CommandXboxController operator = new CommandXboxController(OI.OPERATOR);
  private final CommandXboxController driver = new CommandXboxController(OI.DRIVER);

  // SUBSYSTEMS
  private final Drive drive = Drive.create();

  // COMMANDS
  @Log.NT private final SendableChooser<Command> autos = AutoBuilder.buildAutoChooser();

  /** The robot contains subsystems, OI devices, and commands. */
  public Robot() {
    registerCommands();
    configureGameBehavior();
    configureSubsystemDefaults();
    configureBindings();
  }

  /** Configures basic behavior during different parts of the game. */
  private void configureGameBehavior() {
    // Configure logging with DataLogManager, Monologue, FailureManagement, and URCL
    DataLogManager.start();
    Monologue.setupMonologue(this, "/Robot", false, true);
    addPeriodic(Monologue::updateAll, kDefaultPeriod);
    FaultLogger.setupLogging();
    addPeriodic(FaultLogger::update, 1);

    if (isReal()) {
      URCL.start();
    } else {
      DriverStation.silenceJoystickConnectionWarning(true);
    }
  }

  /**
   * Configures subsystem default commands. Default commands are scheduled when no other command is
   * running on a subsystem.
   */
  private void configureSubsystemDefaults() {
    drive.setDefaultCommand(
        drive.drive(() -> -driver.getLeftX(), () -> -driver.getLeftY(), () -> -driver.getRightX()));
  }

  /** Registers all named commands, which will be used by pathplanner */
  private void registerCommands() {
    // EX: NamedCommands.registerCommand(name, command);
  }

  /** Configures trigger -> command bindings */
  private void configureBindings() {
    autonomous().whileTrue(new ProxyCommand(autos::getSelected));
    FaultLogger.onFailing(f -> Commands.print(f.toString()));
  }
}
