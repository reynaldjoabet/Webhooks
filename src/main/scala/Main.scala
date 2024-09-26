import cats.effect.std.Semaphore

import config.*
import domain.*
import domain.user.*
import pureconfig.error.CannotConvert
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader
import pureconfig.ConfigSource
import skunk.codec.all.*
import skunk.syntax.all.*
import skunk.Codec
import fs2.*
object Main extends App {

  println("Hello, World!")

  final case class AuthenticationException(message: String) extends Exception(message) {
    override def toString(): String = s"${this.getClass.toString()}: '$message'"
  }

  val err = AuthenticationException("an error occured")

  println(err)

  val userCodec: Codec[User] = (uuid *: varchar(32) *: text.opt *: text.opt)
    .imap(User.apply)(u => (u.id, u.username, u.imageUrl, u.bio))

  sealed trait AuthMethod

  object AuthMethod {

    case class Login(username: String, password: String) extends AuthMethod
    case class Token(token: String)                      extends AuthMethod derives ConfigReader
    case class PrivateKey(pkFile: java.io.File)          extends AuthMethod derives ConfigReader

    given tokenReader: ConfigReader[Token] = ConfigReader[String].emap { token =>
      Some(Token(token)).toRight(
        CannotConvert(token, Token.getClass.toString, s"Invalid token: $token")
      )
    }

    given fileReader: ConfigReader[PrivateKey] = ConfigReader[java.io.File].emap { file =>
      Some(PrivateKey(file)).toRight(
        CannotConvert(file.toString, PrivateKey.getClass.toString, s"Invalid private key: $file")
      )
    }

//given loginReader:ConfigReader[Login]= ConfigReader[(String,String)]

  }
  // import pureconfig.generic.auto

// case class ServiceConf(host: String, port: Int,
//     useHttps: Boolean,
//     authMethods: List[AuthMethod]
//   ) derives  ConfigReader

  // ConfigSource.default.load[ServiceConf]

  // def otelResource[F[_]: Async: LiftIO]: Resource[F, Otel4s[F]] =
  //   Resource
  //     .eval(Sync[F].delay(GlobalOpenTelemetry.get))
  //     .evalMap(OtelJava.forAsync[F])

  // val notificationConf = ConfigSource.resources("notification.conf").load[NotificationConfig]

  // sealed trait Env extends EnumEntry
  // object Env extends Enum[Env] {
  //   case object Prod extends Env
  //   case object Test extends Env
  //   override val values = findValues
  // }
  // final case class BaseAppConfig(appName: String, baseDate: LocalDate, env: Env)

  val emb = ConfigSource.default.loadOrThrow[EmberConfig]

  println(emb)


  def runKafkaServiceAndAlwaysRestart(serviceLabel: String, service: IO[Unit]): IO[Unit] =
  Stream
    .eval {
      service.recoverWith { case NonFatal(e) =>
        e.getCause match
          case NonFatal(_) | null => logger.warn(e)(s"There was a recoverable error in the Kafka Service \"$serviceLabel\"")
      }
    }
    .evalTap(_ =>
      for
        _ <- logger.warn(s"Restarting Kafka Service \"$serviceLabel\"")
        _ <- reporter.counter("kafka.service.failure", Map(metricTag -> serviceLabel)).flatMap(_.increment)
      yield ()
    )
    .delayBy(10.seconds)
    .repeat
    .compile
    .drain

}
