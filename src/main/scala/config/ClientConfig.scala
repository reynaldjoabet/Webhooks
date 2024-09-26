package config

import scala.concurrent.duration.FiniteDuration

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class ClientConfig(
  connectTimeout: FiniteDuration,
  proxyHost: Option[String],
  proxyPort: Option[Int]
) derives ConfigReader
