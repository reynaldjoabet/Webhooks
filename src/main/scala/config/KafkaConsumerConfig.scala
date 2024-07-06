package config

import scala.concurrent.duration.FiniteDuration

import ciris.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class KafkaConsumerConfig(
  bootstrapServers: List[String],
  groupId: String,
  clientId: String,
  closeTimeout: FiniteDuration,
  topics: List[String]
) derives ConfigReader

object ConsumerConfig {
  // implicit val configReader: ConfigReader[ConsumerConfig] = deriveReader[ConsumerConfig]
}
