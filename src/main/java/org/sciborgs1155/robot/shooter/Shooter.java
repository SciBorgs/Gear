package org.sciborgs1155.robot.shooter;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.DoubleSupplier;
import monologue.Logged;
import monologue.Annotations.Log;
import static org.sciborgs1155.robot.shooter.ShooterConstants.*;
import static org.sciborgs1155.lib.SparkUtils.*;

import org.sciborgs1155.robot.Robot;

public class Shooter extends SubsystemBase implements Logged, AutoCloseable {
  @Log.File private final FlywheelIO flywheel = Robot.isReal() ? new RealFlywheel() : new SimFlywheel();

  @Log.NT
  private final PIDController pidController =
      new PIDController(kp, ki, kd);
      

  private final SimpleMotorFeedforward feedForward =
      new SimpleMotorFeedforward(
          kSVolts, kVVoltSecondsPerRotation);

  private double targetRPS;

  @Log.NT
  public boolean isAtGoal() {
    return pidController.atSetpoint();
  }

  public Shooter() {
    setDefaultCommand(shoot(() -> 0.001));
  }
  
  @Log.NT
  public double getVelocity() {
    return flywheel.velocity();
  }

  public Command shoot(DoubleSupplier targetRPS) {
    return run(() ->
    flywheel.setVoltage(
        pidController.calculate(flywheel.velocity(), targetRPS.getAsDouble())
            + feedForward.calculate(targetRPS.getAsDouble())))
            // Wait until the shooter has reached the setpoint, and then run the feeder
            .withName("Shoot");
  }

  public Command shoot() {
    return shoot(this::getTargetRPS);
  }

  public Command setTargetRPS(DoubleSupplier targetRPS) {
    return runOnce(
      () -> this.targetRPS = targetRPS.getAsDouble()
    );
  }

  public Command changeTargetRPS(DoubleSupplier change) {
    return runOnce(
      () -> targetRPS += change.getAsDouble()
    );
  }

  public Command shootForDistance(DoubleSupplier distance) {
    return shoot(() -> distance.getAsDouble() * DISTANCE_CONVERSION);
  }

  @Log.NT
  public double getTargetRPS() {
      return targetRPS;
  }

  @Override
  public void close() throws Exception {
      flywheel.close();
  }
}

// inspiration from
// https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/rapidreactcommandbot/subsystems/Shooter.java
// IO inspiration by Asa and
// https://github.com/SciBorgs/ChargedUp-2023/blob/main/src/main/java/org/sciborgs1155/robot/arm/
// More IO advice from Siggy
