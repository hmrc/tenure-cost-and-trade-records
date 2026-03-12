import play.core.PlayVersion
import sbt.*

private object AppDependencies {

  val bootstrapVersion          = "10.7.0"
  val hmrcMongoVersion          = "2.12.0"
  val cryptoJsonVersion         = "8.4.0"
  val playLanguageVersion       = "9.6.0"
  val internalAuthClientVersion = "3.1.0"

  // Test dependencies
  val scalaTestPlusMockitoVersion = "3.2.19.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"           % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "play-language-play-30"        % playLanguageVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"          % cryptoJsonVersion,
    "uk.gov.hmrc"       %% "internal-auth-client-play-30" % internalAuthClientVersion
  )

  private val commonTests = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion         % Test,
    "org.apache.pekko"       %% "pekko-testkit"          % PlayVersion.pekkoVersion % Test
  )

  private val testOnly = Seq(
    "org.scalatestplus" %% "mockito-5-21" % scalaTestPlusMockitoVersion % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ commonTests ++ testOnly

  val itDependencies: Seq[ModuleID] = commonTests

}
