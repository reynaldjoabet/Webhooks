package domain

import io.circe.Codec

final case class Event(eventType: String) derives Codec
