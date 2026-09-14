import sbt.*

private object AppDependencies {

  private val bootstrapVersion          = "10.8.0"
  private val voServiceVersion          = "0.12.0"
  private val hmrcMongoVersion          = "2.13.0"
  private val cryptoJsonVersion         = "8.4.0"
  private val internalAuthClientVersion = "3.1.0"
  private val ibmICUVersion             = "78.3"

  // Test dependencies
  private val voTestVersion = "0.6.0"

  private val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30"    % bootstrapVersion,
    "uk.gov.hmrc"       %% "vo-backend-service"           % voServiceVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"           % hmrcMongoVersion,
    "uk.gov.hmrc"       %% "crypto-json-play-30"          % cryptoJsonVersion,
    "uk.gov.hmrc"       %% "internal-auth-client-play-30" % internalAuthClientVersion,
    "com.ibm.icu"        % "icu4j"                        % ibmICUVersion
  )

  private val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,
    "uk.gov.hmrc" %% "vo-unit-test"           % voTestVersion    % Test
  )

  private val integrationTestOnly = Seq(
    "uk.gov.hmrc" %% "vo-integration-test" % voTestVersion % Test
  )

  val appDependencies: Seq[ModuleID] = compile ++ test

  val itDependencies: Seq[ModuleID] = appDependencies ++ integrationTestOnly

}
