package domain

import io.circe.Codec

final case class CreateWebhookRequest(eventType: String, path: String) derives Codec
