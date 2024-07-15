import play.core.PlayVersion
import sbt.*

private object AppDependencies {

  val bootstrapVersion          = "9.0.0"
  val hmrcMongoVersion          = "2.1.0"
  val cryptoJsonVersion         = "8.0.0"
  val playLanguageVersion       = "8.0.0"
  val internalAuthClientVersion = "3.0.0"

  // Test dependencies
  val scalatestPlusPlayVersion    = "7.0.1"
  val scalatestVersion            = "3.2.19"
  val scalaTestPlusMockitoVersion = "3.2.19.0"
  val scalaGuiceVersion           = "6.0.0"
  val flexMarkVersion             = "0.64.8"
  val wiremockVersion             = "3.8.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"           % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-language-play-30"        % playLanguageVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"          % cryptoJsonVersion,
    "uk.gov.hmrc"       %% "internal-auth-client-play-30" % internalAuthClientVersion
  )

  private val commonTests = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion         % Test exclude ("com.github.tomakehurst", "wiremock"),
    "org.playframework"      %% "play-test"              % PlayVersion.current      % Test,
    "org.apache.pekko"       %% "pekko-testkit"          % PlayVersion.pekkoVersion % Test,
    "org.scalatest"          %% "scalatest"              % scalatestVersion         % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"     % scalatestPlusPlayVersion % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % flexMarkVersion          % Test // for scalatest 3.2.x
  )

  private val testOnly = Seq(
    "org.scalatestplus" %% "mockito-5-12" % scalaTestPlusMockitoVersion % Test
  )

  private val integrationTestOnly = Seq(
    "org.wiremock" % "wiremock" % wiremockVersion % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly

  val itDependencies: Seq[ModuleID] = commonTests ++ integrationTestOnly
}
