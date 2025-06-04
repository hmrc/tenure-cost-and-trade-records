import uk.gov.hmrc.DefaultBuildSettings.itSettings

val defaultPort = 9527
val appName     = "tenure-cost-and-trade-records"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.7.0"

val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin, BuildInfoPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    PlayKeys.playDefaultPort := defaultPort,
    libraryDependencies ++= AppDependencies.appDependencies,
    Test / fork := true, // must be true for Service Provider Interface
    buildInfoPackage := "uk.gov.hmrc.tctr.backend",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=Flag .* set repeatedly:s",
    scalacOptions += "-feature",
    javaOptions += "-XX:+EnableDynamicAgentLoading"
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice)
  .settings(libraryDependencies ++= AppDependencies.itDependencies)
  .settings(itSettings(forkJvmPerTest = true))

addCommandAlias("precommit", "scalafmtSbt;scalafmtAll;it/test:scalafmt;coverage;test;it/test;coverageReport")
