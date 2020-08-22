## Lightbend Akka for Java - Professional

---

### Introduction

This document describes how to setup:

- The development environment
- The case study project

We recommend using the following tools:

- Eclipse or IntelliJ
- sbt build tool

---

## Prerequisites

---

### Required Knowledge and Software

This course is best suited for individuals that have knowledge of Java. Also, we need access to the internet and a computer with the following software installed:

- Unix compatible shell
- JVM 1.8 or higher
- Scala 2.11 or higher
- Sbt 0.13.8 or higher

---

### Unix Compatible Shell

If you are running OSX, then you are on a **nix** system already. Otherwise install a Unix compatible shell like [Cygwin](https://www.cygwin.com/).

---

### JVM 1.8 or Higher

If you are running OSX and a [Homebrew Cask](https://github.com/caskroom/homebrew-cask) user, from a terminal run:

```bash
$ brew cask install java
```

Otherwise follow the [setup instructions](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) to download and install. Once the installation is complete, very the installation by running the following command in a terminal session:

```bash
$ java -version
java version "1.8.0_45"
Java(TM) SE Runtime Environment (build 1.8.0_45-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02, mixed mode)
```

---

### Scala 2.11 or Higher

If you are running OSX and a [Homebrew](http://brew.sh/) user, from a terminal run:

```bash
$ brew install scala
```

Otherwise follow the [setup instruction](http://www.scala-lang.org/documentation/) to download and install. Once the installation is complete, verify the installation by running the following command in a terminal session:

```bash
$ scala -version
Scala code runner version 2.11.6 -- Copyright 2002-2013, LAMP/EPFL
```

---

### Sbt 0.13.8 or Higher

If you are running OSX and a [Homebrew](http://brew.sh/) user, from a terminal run:

```bash
$ brew install sbt
```

Otherwise follow the [setup instruction](http://www.scala-sbt.org/0.13/docs/index.html) to download and install. Once the installation is complete, verify the installation by running the following command in a terminal session:

```bash
$ sbt -version
sbt launcher 0.13.8
```

---

## Simple Build Tool

---

### Make Yourself Familiar with Sbt

- Read the first chapters of the [Getting Started Guide](http://www.scala-sbt.org/release/tutorial/index.html)
- Starting `sbt` takes you to a **interactive session**
- Take a look at `build.sbt` and the other `.sbt` files for Coffee House
- Change directory to the `LAJ-P-lightbend-akka-for-java-professional-exercises-<version>` directory and start `sbt` as follows:

```scala
$ sbt
man [e] > coffee-house > initial-state >
```

---

### man

The `man` command, short for manual, displays the setup instructions (what you are reading now) for the courseware. To view the instructions for the current exercise, use the `e` option. If you are using an IDE, you can also open up the setup instructions (`README.md`) file or the current exercises instructions (`src/test/resources/README.md`) file in your workspace.

```scala
// display the setup instructions
man [e] > coffee-house > initial-state > man

// display the instructions for the current exercise
man [e] > coffee-house > initial-state > man e
```

---

### run

As part of each exercise, we use the `run` command to bootstrap the main class `CoffeeHouseApp`. This command starts the application for the **current** exercise that we interact with and verify our solution.

```scala
man [e] > coffee-house > initial-state > run
```

---

### course navigation and testing

Navigation through the courseware is possibile with a few `sbt` commands. Also, tests are provided to confirm our solution is accurate. It is important to note that the tests make some assumptions about the code, in particular, naming and scope; please adjust your source accordingly. Following are the available `navigation` commands:

```scala
// show the current exercise
man [e] > coffee-house > initial-state > showExerciseId
[INFO] Currently at exercise_000_initial_state

// move to the next exercise
man [e] > coffee-house > initial-state > nextExercise
[INFO] Moved to exercise_001_implement_actor

// move to the previous exercise
man [e] > coffee-house > implement-actor > prevExercise
[INFO] Moved to exercise_000_initial_state

// save the current state of an exercise for later retrieval and study
man [e] > coffee-house > initial-state > saveState
[INFO] State for exercise exercise_000_initial_state saved successfully

// List previously saved states
man [e] > coffee-house > top-level-actor > savedStates
[INFO] Saved exercise states are available for the following exercise(s):
        exercise_000_initial_state
        exercise_002_top_level_actor

// Restore a previously saved exercise state
man [e] > coffee-house > message-actor > restoreState exercise_002_top_level_actor
[INFO] Exercise exercise_002_top_level_actor restored
```

---

### clean

To clean your current exercise, use the `clean` command from your `sbt` session. Clean deletes all generated files in the `target` directory.

```scala
man [e] > coffee-house > initial-state > clean
```

---

### compile

To compile your current exercise, use the `compile` command from your `sbt` session. This command compiles the source in the `src/main/scala` directory.

```scala
man [e] > coffee-house > initial-state > compile
```

---

### reload

To reload `sbt`, use the `reload` command from your `sbt` session. This command reloads the build definitions, `build.sbt`, `project/.scala` and `project/.sbt` files. Reloading is a **requirement** if you change the build definition files.

```scala
man [e] > coffee-house > initial-state > reload
```

---

### test

To test your current exercise, use the `test` command from your `sbt` session. Test compiles and runs all tests for the current exercise. Automated tests are your safeguard and validate whether or not you have completed the exercise successfully and are ready to move on.

```scala
man [e] > coffee-house > initial-state > test
```

---

## Eclipse

---

### Install the Scala IDE for Eclipse

Follow these instructions if you want to use Eclipse:

- **Attention**: Make sure you pick the right packages for Scala 2.11!
- You can download the prepackaged and preconfigured [Scala IDE](http://scala-ide.org/download/sdk.html) for your platform
- You can also use your already installed Eclipse:
    - Install the latest Scala IDE plugin
    - Use the following [update site](http://scala-ide.org/download/current.html) for Eclipse 4.3 and 4.4 (Kepler and Luna)
- In Eclipse import the `coffee-house` project with `Import... > Existing Projects into Workspace`

---

## IntelliJ

---

### Install the IntelliJ IDEA IDE

Follow these instructions if you want to use IntelliJ IDEA:

- Download and install the latest version of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) for your platform
- In Intellij, import the `coffee-house` project
---

## Case Study

---

### Coffee House

Welcome to the Akka Coffee House where we work through a series of exercises organized by topic as laid out in the Lightbend Akka for Java - Professional slide deck and experience:

- Yummy caffeinated concoctions like `Akkaccino`, `MochaPlay`, and `CaffeJava`
- Guests becoming **caffeinated**, and waiters can getting **frustrated**
- Barista's becoming **bottlenecks**

Our mission is to keep the Akka Coffee House healthy, so make sure you have the deck handy as it is a useful reference for guidance.

---

### Exercise Outline

0. Exercise 0 > Initial State
1. Exercise 1 > Implement Actor
2. Exercise 2 > Top Level Actor
3. Exercise 3 > Message Actor
4. Exercise 4 > Use Sender
5. Exercise 5 > Child Actors
6. Exercise 6 > Actor State
7. Exercise 7 > Use Scheduler
8. Exercise 8 > Busy Actor
9. Exercise 9 > Stop Actor
10. Exercise 10 > Lifecycle Monitoring
11. Exercise 11 > Faulty Guest
12. Exercise 12 > Custom Supervision
13. Exercise 13 > Faulty Waiter
14. Exercise 14 > Self Healing
15. Exercise 15 > Detect Bottleneck
16. Exercise 16 > Implement Router
17. Exercise 17 > Configure Dispatcher
18. Exercise 18 > Modify Behavior
19. Exercise 19 > Ask Pattern
20. Exercise 20 > Akka Extensions
21. Exercise 21 > Fsm
