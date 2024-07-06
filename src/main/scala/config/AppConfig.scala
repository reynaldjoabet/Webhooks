package config

import ciris.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class AppConfig(
  consumer: ConsumerConfig,
  transactor: TransactorConfig,
  server: ServerConfig
) derives ConfigReader

object AppConfig {}
