package config

import scala.concurrent.duration.FiniteDuration

import ciris.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class ConsumerConfig(
  bootstrapServers: List[String],
  groupId: String,
  clientId: String,
  closeTimeout: FiniteDuration,
  topicIds: List[String]
) derives ConfigReader

object ConsumerConfig {
  // implicit val configReader: ConfigReader[ConsumerConfig] = deriveReader[ConsumerConfig]
}
