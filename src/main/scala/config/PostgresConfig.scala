package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

// final case class PostgresConfig(nThreads: Int, url: String, user: String, password: String)
//     derives ConfigReader

// case class PostgresConfig(
//     host: NonEmptyString,
//     port: Port,
//     user: NonEmptyString,
//     password: Secret[NonEmptyString],
//     database: NonEmptyString,
//     max: PosInt
// )

case class PostgresConfig(
  host: String,
  port: Int,
  user: String,
  password: String,
  database: String,
  max: Int
) derives ConfigReader
