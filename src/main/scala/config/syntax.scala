package config

import scala.reflect.ClassTag

import cats.implicits.*
import cats.MonadThrow

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.error.ConfigReaderException

object syntax {

  extension (source: ConfigSource) {

    def loadF[F[_], A](using reader: ConfigReader[A], F: MonadThrow[F], tag: ClassTag[A]): F[A] =
      F.pure(source.load[A]) //  F[Either[Errors, A]]
        .flatMap {
          case Left(errors) => F.raiseError[A](ConfigReaderException(errors))
          case Right(value) => F.pure(value)
        }

  }

}
