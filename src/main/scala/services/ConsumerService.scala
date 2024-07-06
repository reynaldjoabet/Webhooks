package services

import scala.collection.immutable.Stream.Cons
import scala.concurrent.duration.DurationInt

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import cats.effect.syntax.all.*
import cats.syntax.all.*
import fs2.kafka.*
import fs2.kafka.KafkaConsumer

import config.KafkaConsumerConfig
import domain.*
import io.circe.jawn.decodeByteArray
import io.circe.parser.*
import org.http4s.circe.middleware.JsonDebugErrorHandler
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

trait ConsumerService[F[_]] {
  def consumeEvent(): F[Unit]
}

object ConsumerService {

  def resource[F[_]: Async: Logger](
    config: KafkaConsumerConfig,
    eventService: EventService[F]
  ): Resource[F, ConsumerService[F]] = make[F](config, eventService).toResource

  def make[F[_]: Async: Logger](
    config: KafkaConsumerConfig,
    eventService: EventService[F]
  ): F[ConsumerService[F]] = new ConsumerService[F] {

    private val eventDeserializer = Deserializer
      .lift[F, Event](byteArray => decodeByteArray[Event](byteArray).liftTo[F])
    // > Logger[F].error(e)(e.getMessage).as(Deserializer.fail[F,String](e)
    private val settings: ConsumerSettings[F, String, Event] =
      ConsumerSettings[F, String, Event](Deserializer[F, String], eventDeserializer)
        .withBootstrapServers(config.bootstrapServers.mkString(","))
        .withGroupId(config.groupId)
        .withClientId(config.clientId)
        .withCloseTimeout(config.closeTimeout)

    override def consumeEvent(): F[Unit] =
      consumeAndProcess[F](settings, config.topics, eventService)
  }.pure[F]

  private def consumeAndProcess[F[_]: Async](
    settings: ConsumerSettings[F, String, Event],
    subscription: List[String],
    eventService: EventService[F]
  ) =
    KafkaConsumer
      .stream(settings)
      .subscribeTo(subscription.head)
      .records
      .mapAsync(25) { committable =>
        eventService.processEvent(committable.record.value).as(committable.offset)

      }
      .through(commitBatchWithin(500, 15.seconds))
      .compile
      .drain

}
