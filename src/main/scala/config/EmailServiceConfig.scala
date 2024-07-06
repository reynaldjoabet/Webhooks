package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class EmailServiceConfig(
  host: String,
  port: Int,
  user: String,
  password: String,
  frontendUrl: String
) derives ConfigReader
