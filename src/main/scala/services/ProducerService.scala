package services

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.all.*
import fs2.kafka.*

import config.KafkaProducerConfig
import domain.Event
import io.circe.syntax.*
import org.typelevel.log4cats.Logger

trait ProducerService[F[_]] {

  def publish(event: Event): F[Unit]
  def publish(events: List[Event]): F[Unit]

}

object ProducerService {

  def make[F[_]: Async: Logger: Console](config: KafkaProducerConfig) = new ProducerService[F] {
    val valueSerializer: Serializer[F, Event] = Serializer[F, String]
      .contramap[Event](_.asJson.noSpaces)

    val producerSettings = ProducerSettings(
      keySerializer = Serializer[F, String],
      valueSerializer = valueSerializer
    ).withBootstrapServers(config.bootstrapServers.mkString(",")).withAcks(Acks.One)

    // type ProducerRecords[K, V] = Chunk[ProducerRecord[K, V]]
    // hence why  def one[K, V](record: ProducerRecord[K, V]): ProducerRecords[K, V] =Chunk.singleton(record)

    // def produce(records: ProducerRecords[K, V]): F[F[ProducerResult[K, V]]]
    // which is basically a chunk of producer records
    override def publish(event: Event): F[Unit] =
      KafkaProducer
        .stream(producerSettings)
        .evalTapChunk { producer =>
          val record: ProducerRecord[String, Event] =
            ProducerRecord(config.topic, event.hashCode().toString(), event)
          producer.produce(ProducerRecords.one(record)).flatten

        }
        .evalMapChunk(_ => Console[F].println(s"The event has been published"))
        // .evalTapChunk(_ => Console[F].println(s"The event has been published"))
        .compile.drain

    override def publish(events: List[Event]): F[Unit] =
      KafkaProducer
        .stream(producerSettings)
        .evalTapChunk { producer =>
          val records: List[ProducerRecord[String, Event]] =
            events.map(event => ProducerRecord(config.topic, event.hashCode().toString(), event))
          producer.produce(ProducerRecords(records)).flatten

        }
        .evalMapChunk(_ => Console[F].println(s"The event has been published"))
        // .evalTapChunk(_ => Console[F].println(s"The event has been published"))
        .compile.drain
  }.pure[F]

}
