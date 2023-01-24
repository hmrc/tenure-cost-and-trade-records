import sbt.Keys.scalacOptions
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}

val plugins: Seq[Plugins] = Seq(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)

val silencerVersion = "1.7.9"

val defaultPort = 9527
val appName = "tenure-cost-and-trade-records"

val root = (project in file("."))
  .settings(name := appName)
  .enablePlugins(plugins: _*)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(PlayKeys.playDefaultPort := defaultPort)
  .settings(majorVersion := 0)
  .settings(
    libraryDependencies ++= AppDependencies.appDependencies,
    Test / fork := true, //must be true for Service Provider Interface
    routesGenerator := InjectedRoutesGenerator,
    scalaVersion := "2.13.8",
    DefaultBuildSettings.targetJvm := "jvm-11",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk",
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(IntegrationTest / fork := true) //must be true for Service Provider Interface
  .settings(CodeCoverageSettings.settings: _*)
  .settings(
    resolvers += Resolver.jcenterRepo
  )
  .disablePlugins(JUnitXmlReportPlugin)

addCommandAlias("precommit", ";scalafmt;test:scalafmt;it:scalafmt;coverage;test;it:test;coverageReport")