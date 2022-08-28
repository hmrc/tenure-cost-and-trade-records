import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings


val plugins: Seq[Plugins] = Seq(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)

val defaultPort = 9527
val appName = "tenure-cost-and-trade-records"

val root = (project in file("."))
  .settings(name := appName)
  .enablePlugins(plugins: _*)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(publishingSettings: _*)
  .settings(PlayKeys.playDefaultPort := defaultPort)
  .settings(majorVersion := 0)
  .settings(
    libraryDependencies ++= AppDependencies.appDependencies,
    Test / fork := true, //must be true for Service Provider Interface
    routesGenerator := InjectedRoutesGenerator,
    scalaVersion := "2.13.8",
    DefaultBuildSettings.targetJvm := "jvm-11",
    maintainer := "voa.service.optimisation@digital.hmrc.gov.uk"
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(IntegrationTest / fork := true) //must be true for Service Provider Interface
  .settings(
    resolvers += Resolver.bintrayRepo("hmrc", "releases"),
    resolvers += Resolver.jcenterRepo
  )
  .disablePlugins(JUnitXmlReportPlugin)
