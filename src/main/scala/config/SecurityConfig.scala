package config

import scala.concurrent.duration.FiniteDuration

import pureconfig.*
import pureconfig.generic.derivation.default.*

final case class SecurityConfig(secret: String, jwtExpiryDuration: FiniteDuration)
    derives ConfigReader
