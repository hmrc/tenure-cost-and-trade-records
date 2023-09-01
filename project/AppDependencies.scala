import play.core.PlayVersion
import sbt._

private object AppDependencies {

  val bootstrapVersion = "7.21.0"
  val hmrcMongoVersion = "1.3.0"
  val cryptoJsonVersion = "7.3.0"
  val jodaVersion = "2.9.4"
  val playLanguageVersion = "6.2.0-play-28"


  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.15"
  val mockitoScalaVersion = "1.17.12"
  val scalaGuiceVersion = "5.1.1"
  val flexMarkVersion = "0.64.8"

  private val allTestsScope = "test,it"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "play-language" % playLanguageVersion,
    "uk.gov.hmrc" %% "crypto-json-play-28" % cryptoJsonVersion,
    "com.typesafe.play" %% "play-json-joda" % jodaVersion,
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
