## Compilation

1. clone `https://github.com/martinpaljak/oracle_javacard_sdks` somewhere
1. Create `gradle.properties` as described below
1. Run `./gradlew convertJavacard -i`
1. Install to card with `./gradlew install -i`
1. Ping applet with `./gradlew ping -i`

## Configure Java Card home

`gradle.properties` can include something like:

    com.fidesmo.gradle.javacard.home=PATH_HERE/oracle_javacard_sdks/jc304_kit/

or you could use an environment variable (name?).
