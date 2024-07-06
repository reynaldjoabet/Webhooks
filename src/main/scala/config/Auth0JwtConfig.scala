package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class Auth0JwtConfig(
  secret: String,
  audience: String,
  issuer: String,
  algorithms: Set[String]
) derives ConfigReader
