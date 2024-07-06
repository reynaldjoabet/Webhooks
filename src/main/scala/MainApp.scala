import cats.effect.*
import fs2.kafka.consumer.KafkaConsumeChunk.CommitNow

import com.comcast.ip4s
import com.comcast.ip4s.Literals.*
import config.*
import config.syntax.loadF
import doobie.util.transactor.Transactor
import org.http4s.ember.client.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import pureconfig.ConfigSource
import repositories.WebhookRepoLive
import services.*

object MainApp {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def makeServer(config: ServerConfig): Resource[IO, Server] = EmberServerBuilder
    .default[IO]
    // .withPort(ip4s.Port.fromInt(config.port).getOrElse(ip4s.port"8080"))
    .withPort(ip4s.Port.fromInt(config.port).get)
    .withHost(ip4s.Host.fromString(config.host).get)
    .build

  def makeClient = EmberClientBuilder.default[IO].build

  private def init(pathOpt: Option[String]): Resource[IO, (ConsumerService[IO], Server)] = for {
    config        <- ConfigSource.default.loadF[IO, AppConfig].toResource
    client        <- makeClient
    webhookRepo   <- WebhookRepoLive.resource[IO](makeTransactor(config.transactor))
    eventService  <- EventService.resource[IO](webhookRepo, client)
    consumerEvent <- ConsumerService.resource[IO](config.consumer, eventService)
    server        <- makeServer(config.server)
  } yield (consumerEvent, server)

  private def makeTransactor(config: TransactorConfig): Transactor[IO] { type A = Unit } =
    Transactor.fromDriverManager[IO](
      config.driver,
      config.jdbcConnection,
      "postgres", // user
      "",         // password
      None
    )

}
