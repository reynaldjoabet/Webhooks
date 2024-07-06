package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class TransactorConfig(driver: String, jdbcConnection: String) derives ConfigReader
