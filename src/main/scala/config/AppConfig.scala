package config

import ciris.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class AppConfig(
  consumerConfig: KafkaConsumerConfig,
  transactorConfig: TransactorConfig,
  serverConfig: ServerConfig,
  producerConfig: KafkaProducerConfig,
  emberConfig: EmberConfig
) derives ConfigReader
