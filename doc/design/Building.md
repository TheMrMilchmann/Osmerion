Building Osmerion
=================

Osmerion makes use of Gradle with Kotlin DSL to allow for simple building.
However, since neither Java 9 nor Gradle 4.1 are stable at the point of
writing, the current version of Osmerion requires a copy of JDK 9 build 163
(with Jigsaw) located at `config/jdk9`.

If all requirements are fulfilled running the build is simply a matter of
executing:

```
gradlew build
```