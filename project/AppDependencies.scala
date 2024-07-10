import play.core.PlayVersion
import sbt.*

private object AppDependencies {

  val bootstrapVersion = "8.4.0"
  val hmrcMongoVersion = "1.7.0"
  val cryptoJsonVersion = "7.6.0"
  val playLanguageVersion = "7.0.0"
  val internalAuthClientVersion = "1.8.0"


  // Test dependencies
  val scalatestPlusPlayVersion = "7.0.0"
  val scalatestVersion = "3.2.17"
  val mockitoScalaVersion = "1.17.30"
  val scalaGuiceVersion = "6.0.0"
  val flexMarkVersion = "0.64.8"
  val pekkoTestkitVersion = "1.0.2"

  private val allTestsScope = "test"

  private val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30" % hmrcMongoVersion,
    "uk.gov.hmrc" %% "play-language-play-30" % playLanguageVersion,
    "uk.gov.hmrc" %% "crypto-json-play-30" % cryptoJsonVersion,
    "uk.gov.hmrc" %% "internal-auth-client-play-30" % internalAuthClientVersion
  )

  private val commonTests = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % allTestsScope,
    "org.playframework" %% "play-test" % PlayVersion.current % allTestsScope,
    "org.apache.pekko"       %% "pekko-testkit"      % pekkoTestkitVersion % allTestsScope,
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

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly

  val itDependencies: Seq[ModuleID] = commonTests ++ integrationTestOnly
}