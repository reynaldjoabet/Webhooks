package http.routes

import cats.effect.kernel.Async
import cats.syntax.all.*

import domain.*
import http.requests.*
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe.jsonOf
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.*

final case class WebhookRoutes[F[_]: Async: Logger](val service: EventService[F])
    extends Http4sDsl[F] {

  implicit val createWhDecoder: EntityDecoder[F, CreateWebhookRequest] =
    jsonOf[F, CreateWebhookRequest]

  implicit val updateWhDecoder: EntityDecoder[F, UpdateWebhookRequest] =
    jsonOf[F, UpdateWebhookRequest]

//   implicit val deleteWhDecoder: EntityDecoder[F, DeleteWebhookRequest] =
//     jsonOf[F, DeleteWebhookRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req @ POST -> Root / "create" =>
      for {
        webhook <- req.as[CreateWebhookRequest]
        resp    <- Ok(service.create(webhook.eventType, webhook.path))
      } yield resp

    case req @ POST -> Root / "update" =>
      for {
        request <- req.as[UpdateWebhookRequest]
        resp    <- Ok(service.update(request.id, request.eventType, request.path))
      } yield resp

    case req @ POST -> Root / "delete" =>
      for {
        request <- req.as[DeleteWebhookRequest]
        resp    <- Ok(service.delete(request.id))
      } yield resp
  }

}
