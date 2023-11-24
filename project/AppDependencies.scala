import play.core.PlayVersion
import sbt._

private object AppDependencies {

  val bootstrapVersion = "7.23.0"
  val hmrcMongoVersion = "1.5.0"
  val cryptoJsonVersion = "7.6.0"
  val jodaVersion = "2.9.4"
  val playLanguageVersion = "7.0.0"
  val internalAuthClientVersion = "1.8.0"


  // Test dependencies
  val scalatestPlusPlayVersion = "5.1.0"
  val scalatestVersion = "3.2.17"
  val mockitoScalaVersion = "1.17.27"
  val scalaGuiceVersion = "5.1.1"
  val flexMarkVersion = "0.64.8"

  private val allTestsScope = "test,it"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "play-language-play-28" % playLanguageVersion,
    "uk.gov.hmrc" %% "crypto-json-play-28" % cryptoJsonVersion,
    "uk.gov.hmrc" %% "internal-auth-client-play-28" % internalAuthClientVersion,
    "com.typesafe.play" %% "play-json-joda" % jodaVersion,
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
