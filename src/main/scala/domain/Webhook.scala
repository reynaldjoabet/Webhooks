package domain

import io.circe.Codec

final case class Webhook(id: Long, eventType: String, path: String) derives Codec

final case class WebhookData(eventType: String, path: String) derives Codec
