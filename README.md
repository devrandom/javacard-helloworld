## Compilation

1. clone `https://github.com/martinpaljak/oracle_javacard_sdks` somewhere.
1. Create a gradle.properties
1. Run `./gradlew convertJavacard -i`
1. Install with `./gradlew install -i`


gradle.properties can include something like:

    com.fidesmo.gradle.javacard.home=PATH_HERE/oracle_javacard_sdks/jc304_kit/
