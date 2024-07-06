package domain

import java.util.UUID

import cats.syntax.all.*

//import co.beautybard.domain.error.BadRequestError
import io.circe.*

object brand {

  case class Brand(
    id: UUID,
    name: String,
    quality: Brand.Quality,
    imageUrl: Option[String] = None,
    description: Option[String] = None
  ) derives Codec.AsObject

  object Brand {

    enum Quality(val value: String) {

      case Luxury    extends Quality("luxury")
      case MidRange  extends Quality("mid_range")
      case DrugStore extends Quality("drug_store")

    }

    given qualityCodec: Codec[Quality] = Codec.from(
      Decoder[String].emap { value =>
        Quality.values.find(_.value == value).toRight(s"$value is not a valid quality")
      },
      Encoder[String].contramap[Quality](_.value)
    )

  }

  enum BrandOrder(val value: String) {

    case Id   extends BrandOrder("id")
    case Name extends BrandOrder("name")

  }

  // object BrandOrder{
  //   def of(s: String): Either[BadRequestError, BrandOrder] =
  //     values.find(_.value == s).toRight(BadRequestError(s"Invalid brand ordering: $s"))
  // }
  case class BrandFilter(
    name: Option[String] = None,
    quality: Option[Brand.Quality] = None
  ) derives Codec.AsObject

}
