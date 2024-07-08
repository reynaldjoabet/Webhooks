package http.requests

import io.circe.Codec

final case class DeleteWebhookRequest(id: Long) derives Codec
