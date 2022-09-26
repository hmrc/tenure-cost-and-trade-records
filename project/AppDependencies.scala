import play.core.PlayVersion
import sbt._

private object AppDependencies {

  val bootstrapVersion = "7.1.0"
  val playLanguageVersion = "5.3.0-play-28"
  val hmrcMongoVersion = "0.68.0"

  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.12"
  val mockitoScalaVersion = "1.17.12"
  val scalaGuiceVersion = "5.1.0"
  val flexMarkVersion = "0.64.0"

  private val allTestsScope = "test,it"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
//    temp as quick fix
    "com.softwaremill.sttp.client3" %% "core" % "3.7.5"
  )

  private val commonTests = Seq(
    "com.typesafe.play" %% "play-test" % PlayVersion.current % allTestsScope,
    "com.typesafe.akka" %% "akka-testkit" % PlayVersion.akkaVersion % allTestsScope,
    "org.scalatest" %% "scalatest" % scalatestVersion % allTestsScope,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % allTestsScope,
    "net.codingwell" %% "scala-guice" % scalaGuiceVersion % allTestsScope,
    "com.vladsch.flexmark" % "flexmark-all" % flexMarkVersion % allTestsScope // for scalatest 3.2.x
  )

  private val testOnly = Seq(
    "org.mockito" %% "mockito-scala-scalatest" % mockitoScalaVersion % Test
  )

  private val integrationTestOnly = Seq(
//    "com.github.tomakehurst" % "wiremock-jre8-standalone" % wiremockVersion % IntegrationTest
  )

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly ++ integrationTestOnly

}
