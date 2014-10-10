

name := "integration"

version := "0.7-SNAPSHOT"

publishMavenStyle := true

resolvers += Resolver.mavenLocal

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/comoctoreactive-1015"

//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

// for debugging sbt problems
//logLevel := Level.Debug

classpathTypes += "zip"

libraryDependencies ++= Seq(
  "com.octo.reactive.audit" % "reactive-audit-lib" % "0.7",
  "com.octo.reactive.audit" % "reactive-audit-agent" % "0.7" % "test",
  "com.octo.reactive.audit" % "reactive-audit-agent" % "0.7.zip" % "test",
  "org.aspectj" % "aspectjweaver" % "1.8.2" % "test")


framework in audit:= "play"


val framework = settingKey[String]("The framework to use with reactive-audit.")

val reactiveaudit = settingKey[String]("The reactive-audit zip file.")

//reactiveaudit := ((fullClasspath in Test value) filter (_.data.getName.startsWith("reactive-audit") && _.data.getName.endWith("zip"))).head.data

lazy val audit = config("audit") extend Runtime

inConfig(audit)(Defaults.configSettings)

sourceDirectory in audit <<= sourceDirectory in Compile

ivyConfigurations += audit

fork in audit := true

resourceGenerators in audit += Def.task {
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


javaOptions in audit += "-javaagent:"+((fullClasspath in Test value) filter (_.data.getName.startsWith("aspectjweaver"))).head.data.getAbsolutePath

javaOptions in audit += "-Djava.ext.dirs=."+System.getenv("JAVA_HOME") + "/ext/lib" +
  java.io.File.pathSeparator + target.value / "reactive-audit-libs/"

javaOptions in audit += "-DreactiveAudit_logOutput="+target.value / "reports" / "audit" / "reactive-audit.log"

//"-DreactiveAudit=${agentConf}!/reactive-audit-agent/etc/${framework}.properties"
//javaOptions in audit += "-DreactiveAudit=src/test/resources/reactiveAudit.properties"

javaOptions in audit += "-DreactiveAudit=src/test/resources/reactiveAudit.properties"

addCommandAlias("audit", "audit:run")
