package domain

import java.time.OffsetDateTime
import java.util.UUID

import cats.syntax.all.*

import io.circe

object review {

  case class Review(
    userId: UUID,
    productId: UUID,
    rating: Int,
    wouldBuyAgain: Boolean,
    review: Option[String],
    imageUrls: List[String],
    created: OffsetDateTime,
    edited: Option[OffsetDateTime]
  ) derives circe.Codec.AsObject

}
