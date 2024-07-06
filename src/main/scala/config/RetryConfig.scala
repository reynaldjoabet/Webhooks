package config

import scala.concurrent.duration.FiniteDuration

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class RetryConfig(
  retryAfter: FiniteDuration,
  attempts: Int // PositiveInt
) derives CanEqual,
      ConfigReader
