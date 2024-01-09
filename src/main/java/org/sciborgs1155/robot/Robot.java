package org.sciborgs1155.robot;

import static edu.wpi.first.wpilibj2.command.button.RobotModeTriggers.*;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ProxyCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import monologue.Logged;
import monologue.Monologue;
import monologue.Monologue.LogBoth;
import org.sciborgs1155.lib.CommandRobot;
import org.sciborgs1155.lib.FaultLogger;
import org.sciborgs1155.lib.FaultLogger.FaultType;
import org.sciborgs1155.robot.Ports.OI;
import org.sciborgs1155.robot.commands.Autos;

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

  // COMMANDS
  @LogBoth Autos autos = new Autos();

  /** The robot contains subsystems, OI devices, and commands. */
  public Robot() {
    super(Constants.PERIOD);

    configureGameBehavior();
    configureSubsystemDefaults();
    configureBindings();
  }

  /** Configures basic behavior during different parts of the game. */
  private void configureGameBehavior() {
    if (isSimulation()) {
      DriverStation.silenceJoystickConnectionWarning(true);
    }

    // Configure logging with DataLogManager, Monologue, and FailureManagement
    DataLogManager.start();
    Monologue.setupLogging(this, "/Robot");
    addPeriodic(Monologue::update, kDefaultPeriod);
    FaultLogger.setupLogging();
    addPeriodic(FaultLogger::update, 1);
  }

  /**
   * Configures subsystem default commands. Default commands are scheduled when no other command is
   * running on a subsystem.
   */
  private void configureSubsystemDefaults() {}

  /** Configures trigger -> command bindings */
  private void configureBindings() {
    autonomous().whileTrue(new ProxyCommand(autos::get));
    FaultLogger.failing(FaultType.ERROR)
        .debounce(0.4)
        .onTrue(Commands.print("ice cream machine broken"));
  }
}
