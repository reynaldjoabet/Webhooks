package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class PostgresConfig(nThreads: Int, url: String, user: String, password: String)
    derives ConfigReader
