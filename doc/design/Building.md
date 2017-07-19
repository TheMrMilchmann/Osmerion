Building Osmerion
=================

In order to make the build process as easy to invoke and as simple to maintain
as possible, Osmerion makes use of Gradle Kotlin DSL.

However, since currently neither Java 9 nor Gradle support for building Jigsaw
modules are stable, Osmerion currently requires further setup as outlined below:

- Running the Gradle build from JDK 9 is currently **not** supported. Please
  ensure that a version of JDK 8 is used to run the build.
- Osmerion looks for the JDK 9 it uses to compile the actual modules in two
  places. If an environment variable called `JDK_9` is set, the build will use
  the JDK that the property is pointing to. Otherwise it will default to
  `/config/jdk-9/`
- The JDK 9 version should be at least 175.

If all requirements are fulfilled bulding the project is simply a matter of
calling `gradlew build` in the project's root dir.