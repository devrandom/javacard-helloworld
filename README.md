## Compilation

1. clone `https://github.com/martinpaljak/oracle_javacard_sdks` somewhere
1. Create `gradle.properties` as described below
1. Run `./gradlew convertJavacard -i`

## Configure Java Card home

`gradle.properties` can include something like:

    com.fidesmo.gradle.javacard.home=PATH_HERE/oracle_javacard_sdks/jc304_kit/

or you could use an environment variable (name?).

## Install and Test

1. If you have a Status Keycard, it has a **non-default development key**.
   
   In that case: `export CARD_KEY=c212e073ff8b4bbfaff4de8ab655221f`

1. Install to card with `./gradlew install -i`
1. Ping applet with `./gradlew ping -i`

You can also run unit tests, after cloning this repo in the parent directory:

    (cd .. && git clone https://github.com/status-im/jcardsim.git jcardsim-status)
    ./gradlew test -i

## Utilities

Recommend building https://github.com/martinpaljak/GlobalPlatformPro and using
the gp.jar command.  For example `gp -list` (with `-key $CARD_KEY` if using Status Keycard)
will list the installed applets.  This library is also used for our install and uninstall
targets.
