# Introduction
Sample project to show how to invoke [reactive-audit](https://github.com/octo-online/reactive-audit) directly from the following build tools

* Maven
* Gradle
* SBT

# Usage
Simply replicate and adapt the build configuration of your project for your preferred build tool as done in this project. Then, depending on task type you need to invoke (Run or Audit), just launch the corresponding command line:

Builder| Run | Audit
:-- | ---| ---
Maven | $ mvn exec:java -Prun | $ mvn integration-test -Paudit
Gradle | $ gradle run | $ gradle audit
SBT | $ sbt run | $ sbt audit

    
