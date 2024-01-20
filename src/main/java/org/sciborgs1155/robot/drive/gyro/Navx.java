package org.sciborgs1155.robot.drive.gyro;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.geometry.Rotation3d;

public class Navx implements GyroIO {
  private final AHRS ahrs = new AHRS();

  public double getRate() {
    return ahrs.getRate();
  }

  @Override
  public Rotation3d getRotation3d() {
    return ahrs.getRotation3d();
  }

  public void reset() {
    ahrs.reset();
  }

  @Override
  public void close() throws Exception {}
}
