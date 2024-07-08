package http.requests

import io.circe.Codec

final case class UpdateWebhookRequest(id: Long, eventType: String, path: String) derives Codec
