package services

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import cats.effect.syntax.all.*
import cats.syntax.all.*

import domain.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.typelevel.log4cats.Logger
import repositories.*

trait EventService[F[_]] {

  def processEvent(event: Event): F[Unit]

  def create(eventType: String, path: String): F[Unit]

  def update(id: Long, newEventType: String, newPath: String): F[Unit]

  def delete(id: Long): F[Unit]

}

//value uri is not a member of StringContext, but could be made available as an extension method.

//One of the following imports might fix the problem:
//import org.http4s.implicits.uri
//import org.http4s.syntax.all.uri
import org.http4s.syntax.literals.uri

object EventService {

  def make[F[_]: Async: Logger](
    webhooks: WebhookRepo[F],
    client: Client[F]
  ) = new EventService[F] {
    val dsl = Http4sClientDsl[F]
    import dsl._
    override def processEvent(event: Event): F[Unit] = {
      val eventType       = event.eventType
      val webhooksByEvent = webhooks.getAllByMsgType(eventType)
      val paths           = webhooksByEvent.map(elems => elems.map(_.path))
      paths.flatMap { li =>
        Async[F]
          .parTraverseN(li.length)(li)(u =>
            sendEvent(event, u) *> Logger[F].info(s"sending $eventType to $u")
          )
          .void
      }

    }

    // /** Make a [[org.http4s.Request]] using this Method */
    // final def apply[A](body: A, uri: Uri, headers: Header.ToRaw*)(implicit w: EntityEncoder[F, A]
    // the body can be a UrlForm or a json
    private def sendEvent(event: Event, path: String): F[Unit] = {
      val request = Method
        .POST
        .apply(event, Uri.unsafeFromString(path), `Content-Type`(MediaType.application.json))
      val request2 = Request[F](
        method = Method.POST,
        uri = Uri.unsafeFromString(path) // uri"$path"
      ).withEntity(event.asJson).withContentType(`Content-Type`(MediaType.application.json))

      client
        .run(request)
        .use {
          _.status match {
            case Status.Ok | Status.Created => ().pure[F]
            case _                          => sendEvent(event, path)
          }
        }
    }

    override def create(eventType: String, path: String): F[Unit] = {
      val webhook = WebhookData(eventType, path)
      webhooks.create(webhook)
    }

    override def update(id: Long, newEventType: String, newPath: String): F[Unit] =
      webhooks.update(id, newEventType, newPath)

    override def delete(id: Long): F[Unit] =
      webhooks.remove(id)

  }.pure[F]

  def resource[F[_]: Async: Logger](
    webhooks: WebhookRepo[F],
    client: Client[F]
  ): Resource[F, EventService[F]] = make(webhooks, client).toResource

}
