package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class KafkaProducerConfig(
  bootstrapServers: List[String],
  topic: String,
  compressionType: String,
  inFlightRequests: Int,
  lingerMs: Int,
  maxBatchSizeBytes: Int,
  maxRequestSizeBytes: Int
) derives ConfigReader
