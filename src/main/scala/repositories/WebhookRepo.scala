package repositories

import cats.effect.kernel.Async
import cats.implicits.toFunctorOps

import domain.{Webhook, WebhookData}
import doobie.implicits.toSqlInterpolator
import doobie.syntax.all.*
import doobie.Transactor

trait WebhookRepo[F[_]] {

  def get(id: Long): F[Option[Webhook]] // read

  def create(webhookData: WebhookData): F[Unit]

  def remove(id: Long): F[Unit]

  def update(id: Long, newEventType: String, newPath: String): F[Unit]

  def getAllByMsgType(msgType: String): F[List[Webhook]]

}
