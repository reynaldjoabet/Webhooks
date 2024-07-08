import cats.effect.*
import fs2.kafka.consumer.KafkaConsumeChunk.CommitNow

import com.comcast.ip4s
import com.comcast.ip4s.Literals.*
import config.*
import config.syntax.*
import doobie.util.transactor.Transactor
import http.routes.WebhookRoutes
import org.http4s.client.Client
import org.http4s.ember.client.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.syntax.KleisliSyntax
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import pureconfig.ConfigSource
import repositories.WebhookRepoLive
import services.*

object MainApp extends IOApp.Simple {

  implicit private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def makeServer(config: ServerConfig, service: EventService[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      // .withPort(ip4s.Port.fromInt(config.port).getOrElse(ip4s.port"8080"))
      // .withPort(ip4s.Port.fromInt(config.port).get)
      .withHost(ip4s.Host.fromString(config.host).get)
      .withHttpApp(WebhookRoutes(service).routes.orNotFound)
      .build

  def makeServer2(config: EmberConfig, service: EventService[IO]): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(config.host)
      .withPort(config.port)
      .withHttpApp(WebhookRoutes(service).routes.orNotFound)
      .build

  def makeClient: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build

  private def init(pathOpt: Option[String]): Resource[IO, (ConsumerService[IO], Server)] = for {
    config        <- ConfigSource.default.loadF[IO, AppConfig].toResource
    client        <- makeClient
    webhookRepo   <- WebhookRepoLive.resource[IO](makeTransactor(config.transactorConfig))
    eventService  <- EventService.resource[IO](webhookRepo, client)
    consumerEvent <- ConsumerService.resource[IO](config.consumerConfig, eventService)
    server        <- makeServer(config.serverConfig, eventService)
  } yield (consumerEvent, server)

  private def makeTransactor(config: TransactorConfig): Transactor[IO] { type A = Unit } =
    Transactor.fromDriverManager[IO](
      config.driver,
      config.jdbcConnection,
      "postgres", // user
      "",         // password
      None
    )

  override def run: IO[Unit] = init(None).use { case (service, server) => service.consumeEvent() }

  import resources.*

  MkHttpServer[IO] // .newEmber()

}
