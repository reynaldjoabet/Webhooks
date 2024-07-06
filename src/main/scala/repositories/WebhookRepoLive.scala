package repositories

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import cats.effect.syntax.all.*
import cats.implicits.toFunctorOps
import cats.syntax.all.*

import domain.{Webhook, WebhookData}
import doobie.implicits.toSqlInterpolator
import doobie.syntax.all.*
import doobie.Transactor

final case class WebhookRepoLive[F[_]: Async](xa: Transactor[F]) extends WebhookRepo[F] {

  def get(id: Long): F[Option[Webhook]] =
    sql"SELECT * FROM Webhooks WHERE id = $id".query[Webhook].option.transact(xa)

  def put(webhookData: WebhookData): F[Unit] =
    sql"INSERT INTO Webhooks (eventtype, path) VALUES (${webhookData.eventType}, ${webhookData.path})"
      .update
      .run
      .void
      .transact(xa)

  def getAllByMsgType(eventType: String): F[List[Webhook]] =
    sql"SELECT * FROM Webhooks WHERE eventtype = $eventType".query[Webhook].to[List].transact(xa)

  def updateWebhook(id: Long, newEventType: String, newPath: String): F[Unit] =
    sql"UPDATE Webhooks SET eventtype = $newEventType, path = $newPath WHERE id = $id"
      .update
      .run
      .void
      .transact(xa)

  def remove(id: Long): F[Unit] =
    sql"DELETE FROM Webhooks WHERE id = $id".update.run.void.transact(xa)

}

object WebhookRepoLive {

  def resource[F[_]: Async](xa: Transactor[F]): Resource[F, WebhookRepoLive[F]] = make(xa)
    .toResource

  def make[F[_]: Async](xa: Transactor[F]): F[WebhookRepoLive[F]] = Async[F]
    .pure(WebhookRepoLive[F](xa))

}
