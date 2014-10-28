

name := "integration"

version := "0.8"

publishMavenStyle := true

resolvers += Resolver.mavenLocal

//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/comoctoreactive-1022"

//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// for debugging sbt problems
//logLevel := Level.Debug

classpathTypes += "zip"

libraryDependencies ++= Seq(
  "com.octo.reactive.audit" % "reactive-audit-lib" % "0.8",
  "com.octo.reactive.audit" % "reactive-audit-agent" % "0.8" % "test",
  "com.octo.reactive.audit" % "reactive-audit-agent" % "0.8.zip" % "test",
  "org.aspectj" % "aspectjweaver" % "1.8.2" % "test")


// https://stackoverflow.com/questions/26301364/how-to-declare-zip-dependency-and-know-its-path-in-file-system/26308329#26308329
//libraryDependencies += "com.octo.reactive.audit" % "reactive-audit-agent" % "0.8" from "https://oss.sonatype.org/content/groups/staging/com/octo/reactive/audit/reactive-audit-agent/0.8/reactive-audit-agent-0.8.zip"


framework in Audit:= "play"


val framework = settingKey[String]("The framework to use with reactive-audit.")

val reactiveaudit = settingKey[String]("The reactive-audit zip file.")

//reactiveaudit := ((fullClasspath in Test value) filter (_.data.getName.startsWith("reactive-audit") && _.data.getName.endWith("zip"))).head.data

lazy val Audit = config("audit") extend Runtime

inConfig(Audit)(Defaults.configSettings)

mainClass in (Compile) := Some("com.octo.reactive.sample.TestApp")

mainClass in (Audit) := Some("com.octo.reactive.sample.TestApp")


sourceDirectory in Audit <<= sourceDirectory in Compile

ivyConfigurations += Audit

fork in Audit := true

resourceGenerators in Audit += Def.task {
  val targetLibs   = target.value / "reactive-audit-libs"
  IO.copyFile(
    ((fullClasspath in Test value) filter (_.data.getName.startsWith("reactive-audit-agent"))).head.data,
    targetLibs / "reactive-audit-agent.jar")
  IO.copyFile(
    ((fullClasspath in Runtime value) filter (_.data.getName.startsWith("reactive-audit-lib"))).head.data,
    targetLibs / "reactive-audit-lib.jar")
  IO.copyFile(
    ((fullClasspath in Test value) filter (_.data.getName.startsWith("aspectjweaver"))).head.data,
    targetLibs / "aspecjweaver.jar")
  file(target.value+ "/reports/audit").mkdirs()
  Seq[File]()
}.taskValue


javaOptions in Audit += "-javaagent:"+((fullClasspath in Test value) filter (_.data.getName.startsWith("aspectjweaver"))).head.data.getAbsolutePath

javaOptions in Audit += "-Djava.ext.dirs=."+System.getenv("JAVA_HOME") + "/ext/lib" +
  java.io.File.pathSeparator + target.value / "reactive-audit-libs/"

javaOptions in Audit += "-DreactiveAudit_logOutput="+target.value / "reports" / "audit" / "reactive-audit.log"

//"-DreactiveAudit=${agentConf}!/reactive-audit-agent/etc/${framework}.properties"
//javaOptions in audit += "-DreactiveAudit=src/test/resources/reactiveAudit.properties"

javaOptions in Audit += "-DreactiveAudit=src/test/resources/reactiveAudit.properties"

addCommandAlias("audit", "audit:run")
