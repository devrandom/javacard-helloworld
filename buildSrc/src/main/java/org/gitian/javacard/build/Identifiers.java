package org.gitian.javacard.build;

import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class Identifiers {
  public static final byte[] PACKAGE_AID =    Hex.decode("0F00BA00000001");
  public static final byte[] HELLOWORLD_AID = Hex.decode("0F00BA0000000101");
  public static final int DEFAULT_INSTANCE_IDX = 1;

  /**
   * Gets the instance AID of the default instance of the Keycard applet.
   *
   * @return the instance AID of the Keycard applet
   */
  public static byte[] getInstanceAID() {
    return getInstanceAID(DEFAULT_INSTANCE_IDX);
  }

  /**
   * Gets the instance AID of the Keycard applet with the given index. Since multiple instances of the Keycard applet
   * could be installed in parallel, this method allows selecting a specific instance. The index is between 01 and ff
   *
   * @return the instance AID of the Keycard applet
   */
  public static byte[] getInstanceAID(int instanceIdx) {
    if (instanceIdx < 0x01 || instanceIdx > 0xff) {
      throw new IllegalArgumentException("The instance index must be between 1 and 255");
    }

    byte[] instanceAID = Arrays.copyOf(HELLOWORLD_AID, HELLOWORLD_AID.length + 1);
    instanceAID[HELLOWORLD_AID.length] = (byte) instanceIdx;
    return instanceAID;
  }
}
