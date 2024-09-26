package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class MongoConfig(
  connectionUri: String,
  dbName: String
) derives ConfigReader

// given Conversion[ServerConfig, Server.Config] =
//      (sc: ServerConfig) => Server.Config(sc.host, sc.port)
