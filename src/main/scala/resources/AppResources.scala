package resources

import scala.concurrent.duration.Duration

import cats.effect.{Concurrent, IO, Resource}
import cats.effect.kernel.*
import cats.effect.std.Console
import cats.syntax.all._
import cats.Monad
import fs2.io.net.Network

import config.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.otel4s.trace.Tracer
import org.typelevel.otel4s.trace.Tracer.Implicits.noop
import skunk._
import skunk.codec.text._
import skunk.implicits._

sealed abstract class AppResources[F[_]](
  val postgres: Resource[F, Session[F]]
)

object AppResources {

  def make[F[_]: Temporal: Tracer: Console: Logger: Network](
    cfg: PostgresConfig
  ): Resource[F, AppResources[F]] = mkPostgreSqlResource(cfg).map(new AppResources[F](_) {})

//So our type lambda
// parameter will match the kind (number of variable type parameters of F)
// and the type lambda will return a MonadError of our parameter fixed on VendorCreateEr(Throwable in this case)
  def checkPostgresConnection[F[_]: [g[_]] =>> MonadCancel[g, Throwable]: Logger](
    postgres: Resource[F, Session[F]]
  ): F[Unit] =
    postgres.use { session =>
      session
        .unique(sql"select version();".query(text))
        .flatMap { v =>
          Logger[F].info(s"Connected to Postgres $v")
        }
    }

  def checkPostgresConnection1[F[_]: MonadCancelThrow: Logger](
    postgres: Resource[F, Session[F]]
  ): F[Unit] =
    postgres.use { session =>
      session
        .unique(sql"select version();".query(text))
        .flatMap { v =>
          Logger[F].info(s"Connected to Postgres $v")
        }
    }

  implicit private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  val unit =
    mkPostgreSqlResource[IO](PostgresConfig("", 9090, "postgres", "password", "postgres", 12))
      .use(checkPostgresConnection(_))

  private def checkPostgresConnection2[F[_]: Logger](
    postgres: Resource[F, Session[F]]
  )(using m: MonadCancel[F, Throwable]): F[Unit] =
    postgres.use { session =>
      session
        .unique(sql"select version();".query(text))
        .flatMap { v =>
          Logger[F].info(s"Connected to Postgres $v")
        }
    }

  // type SessionPool[F[_]] = Resource[F, Resource[F, Session[F]]]

  private def mkPostgreSqlResource[F[_]: Temporal: Tracer: Console: Logger: Network](
    c: PostgresConfig
  ): SessionPool[F] =
    Session
      .pooled[F](
        host = c.host,
        port = c.port,
        user = c.user,
        password = Some(c.password),
        database = c.database,
        max = c.max
      )
      .evalTap(checkPostgresConnection)

  private def makeEmberClient[F[_]](
    config: ClientConfig
  )(using F: Async[F]): Resource[F, Client[F]] =
    EmberClientBuilder
      .default[F](using F, Network.forAsync[F])
      .withMaxTotal(256 * 10)
      .withTimeout(config.connectTimeout)
      .withIdleConnectionTime(Duration.Inf)
      .build

}
