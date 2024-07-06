package config

import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class StripeConfig(
  key: String,
  price: String,
  successUrl: String,
  cancelUrl: String,
  webhookSecret: String
) derives ConfigReader
