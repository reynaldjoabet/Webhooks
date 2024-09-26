package http
package requests

import io.circe.Codec
import io.circe.Decoder

final case class DeleteWebhookRequest(id: Long) derives Decoder

object DeleteWebhookRequest {
  // given codec:Decoder[DeleteWebhookRequest]=Decoder[Int].map(DeleteWebhookRequest(_))
}
