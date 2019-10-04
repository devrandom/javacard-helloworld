package org.gitian.javacard.build;

import im.status.keycard.desktop.PCSCCardChannel;
import im.status.keycard.globalplatform.GlobalPlatformCommandSet;
import im.status.keycard.globalplatform.Load;
import im.status.keycard.globalplatform.LoadCallback;
import im.status.keycard.io.APDUException;
import im.status.keycard.io.APDUResponse;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InstallTask extends DefaultTask {

  @TaskAction
  public void install() {
    Logger logger = getLogger();

    TerminalFactory tf = TerminalFactory.getDefault();
    CardTerminal cardTerminal = null;

    try {
      for (CardTerminal t : tf.terminals().list()) {
        if (t.isCardPresent()) {
          cardTerminal = t;
          break;
        }
      }
    } catch(CardException e) {
      throw new GradleException("Error listing card terminals", e);
    }

    if (cardTerminal == null) {
      throw new GradleException("No available PC/SC terminal");
    }

    Card apduCard;

    try {
      apduCard = cardTerminal.connect("*");
    } catch(CardException e) {
      throw new GradleException("Couldn't connect to the card", e);
    }

    logger.info("Connected to " + cardTerminal.getName());
    PCSCCardChannel sdkChannel = new PCSCCardChannel(apduCard.getBasicChannel());
    GlobalPlatformCommandSet cmdSet = new GlobalPlatformCommandSet(sdkChannel);

    try {
      logger.info("Selecting the ISD");
      cmdSet.select().checkOK();
      logger.info("Opening a SecureChannel");
      cmdSet.openSecureChannel();
      logger.info("Deleting the old instances and package (if present)");
      deleteInstance(cmdSet);
      logger.info("Loading the new package");
      FileInputStream in = new FileInputStream("build/javacard/org/gitian/javacard/javacard/javacard.cap");
      loadPackage(cmdSet, in,
              (loadedBlock, blockCount) -> logger.info("Loaded block " + loadedBlock + "/" + blockCount));
      logger.info("XXXX");
      logger.info("Installing the Keycard Applet");
      installApplet(cmdSet);
    } catch (IOException e) {
      throw new GradleException("I/O error", e);
    } catch (APDUException e) {
      throw new GradleException(e.getMessage(), e);
    }
  }

  private void loadPackage(GlobalPlatformCommandSet cmdSet,
                           InputStream in, LoadCallback cb) throws IOException, APDUException {
    Logger logger = getLogger();
    logger.info("1");
    cmdSet.installForLoad(Identifiers.PACKAGE_AID).checkOK();

    logger.info("2");
    Load load = new Load(in);

    byte[] block;
    int steps = load.blocksCount();

    while((block = load.nextDataBlock()) != null) {
      cmdSet.load(block, (load.getCount() - 1), load.hasMore()).checkOK();
      logger.info("3");
      cb.blockLoaded(load.getCount(), steps);
    }
  }

  private APDUResponse installApplet(GlobalPlatformCommandSet cmdSet) throws IOException {
    return cmdSet.installForInstall(Identifiers.PACKAGE_AID, Identifiers.HELLOWORLD_AID,
            Identifiers.getInstanceAID(), new byte[0]);
  }

  private void deleteInstance(GlobalPlatformCommandSet cmdSet) throws IOException, APDUException {
    cmdSet.delete(Identifiers.getInstanceAID()).
            checkSW(APDUResponse.SW_OK, APDUResponse.SW_REFERENCED_DATA_NOT_FOUND);
    cmdSet.delete(Identifiers.PACKAGE_AID).
            checkSW(APDUResponse.SW_OK, APDUResponse.SW_REFERENCED_DATA_NOT_FOUND);
  }
}
