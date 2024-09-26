package config

import pureconfig.error
import pureconfig.generic.derivation.default
import pureconfig.generic.derivation.default.*
import pureconfig.generic.derivation.ConfigReaderDerivation
import pureconfig.generic.derivation.CoproductConfigReaderDerivation
import pureconfig.generic.derivation.EnumConfigReader
import pureconfig.generic.derivation.EnumConfigReaderDerivation
import pureconfig.generic.derivation.ProductConfigReaderDerivation
import pureconfig.generic.derivation.Utils
import pureconfig.syntax.ConfigReaderOps
import pureconfig.ConfigReader
import pureconfig.ScreamingSnakeCase
import pureconfig.StringDelimitedNamingConvention

enum AppEnvironment derives EnumConfigReader {

  case Development
  case Staging // same as case PreProduction
  case Production

}

case class EtlConfig(
  inputFilePath: String,
  outputFilePath: String,
  appEnv: AppEnvironment
) derives ConfigReader
