import uk.gov.hmrc.DefaultBuildSettings.{itSettings, targetJvm}

val defaultPort = 9527
val appName     = "tenure-cost-and-trade-records"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.8.2"

val commonSettings = Seq(
  targetJvm := "jvm-21",
  scalacOptions += "-Wconf:msg=Flag .* set repeatedly:s"
)

val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin, BuildInfoPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(commonSettings)
  .settings(
    PlayKeys.playDefaultPort := defaultPort,
    libraryDependencies ++= AppDependencies.appDependencies,
    Test / fork := true, // must be true for Service Provider Interface
    buildInfoPackage := "uk.gov.hmrc.tctr.backend",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-feature",
    javaOptions += "-XX:+EnableDynamicAgentLoading"
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice)
  .settings(commonSettings)
  .settings(libraryDependencies ++= AppDependencies.itDependencies)
  .settings(itSettings(forkJvmPerTest = true))

addCommandAlias("precommit", "scalafmtSbt;scalafmtAll;it/test:scalafmt;coverage;test;it/test;coverageReport")
