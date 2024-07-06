package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class KafkaProducerConfig(bootstrapServers: List[String], topic: String)
    derives ConfigReader
