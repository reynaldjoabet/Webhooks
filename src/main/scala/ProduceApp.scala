import cats.effect.IO
import cats.effect.IOApp

import config.*
import config.syntax.*
import domain.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import pureconfig.ConfigSource
import services.ProducerService

object ProduceApp extends IOApp.Simple {

  implicit private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private val events: List[Event] = List(Event("event1"), Event("event2"), Event("event3"))

  override def run: IO[Unit] =
    ConfigSource
      .default
      .loadF[IO, AppConfig]
      .flatMap { config =>
        ProducerService.make[IO](config.producerConfig).flatMap(_.publish(events))
      }

}
