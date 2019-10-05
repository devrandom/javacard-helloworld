package org.gitian.javacard.build;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import pro.javacard.CAPFile;

import javax.smartcardio.CardException;
import java.io.FileInputStream;
import java.io.IOException;

public class InstallTask extends DefaultTask {
  private final Logger logger;
  private Card card;
  private boolean skipInstall = false;

  public InstallTask() {
    logger = getLogger();
  }

  public void setSkipInstall(boolean skipInstall) {
    this.skipInstall = skipInstall;
  }

  @TaskAction
  public void install() {
    openCard();

    try {
      card.openSecureChannel();
      CAPFile capFile = loadCap(logger);
      logger.info("Uninstall if exists");
      card.uninstall(capFile);
      if (!skipInstall) {
        logger.info("Install");
        card.install(capFile);
      }
      logger.info("Success");
    } catch (IOException e) {
      throw new GradleException("I/O error", e);
    } finally {
      card.close();
      card = null;
    }
  }

  private void openCard() {
    if (card != null) {
      throw new GradleException("leaked card handle");
    }
    card = new Card();
    try {
      card.open();
      logger.info("Opening a SecureChannel");
    } catch (CardException e) {
      throw new GradleException("Error opening card", e);
    }
  }

  private CAPFile loadCap(Logger logger) throws IOException {
    logger.info("Loading cap file");
    FileInputStream in = new FileInputStream("build/javacard/org/gitian/javacard/javacard/javacard.cap");
    CAPFile capFile = CAPFile.fromStream(in);
    in.close();
    return capFile;
  }
}
