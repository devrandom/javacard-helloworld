package org.gitian.javacard.build;

import im.status.keycard.applet.ApplicationInfo;
import im.status.keycard.applet.SecureChannelSession;
import im.status.keycard.desktop.PCSCCardChannel;
import im.status.keycard.io.APDUCommand;
import im.status.keycard.io.APDUException;
import im.status.keycard.io.APDUResponse;
import im.status.keycard.io.CardChannel;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.io.IOException;
import java.util.Arrays;

public class PingTask extends DefaultTask {
  private CardChannel apduChannel;
  private SecureChannelSession secureChannel = new SecureChannelSession();
  private ApplicationInfo info;

  @TaskAction
  public void ping() {
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
    apduChannel = new PCSCCardChannel(apduCard.getBasicChannel());

    try {
      logger.info("Selecting the applet");
      select().checkOK();
      do_ping();
    } catch (IOException e) {
      throw new GradleException("I/O error", e);
    } catch (APDUException e) {
      throw new GradleException(e.getMessage(), e);
    }
  }

  private APDUResponse select() throws IOException {
    APDUCommand selectApplet = new APDUCommand(0x00, 0xA4, 4, 0, Identifiers.getInstanceAID());
    return apduChannel.send(selectApplet);
  }

  private void do_ping() throws IOException, APDUException {
    APDUCommand selectApplet = new APDUCommand(0x80, 0x00, 0, 0, new byte[0]);
    APDUResponse result = apduChannel.send(selectApplet);
    result.checkOK();
    if (!Arrays.equals(result.getData(), "Hello".getBytes())) {
      throw new IOException("did not get Hello");
    }
  }
}
