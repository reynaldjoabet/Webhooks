package config

import scala.reflect.ClassTag

import cats.effect.kernel.Sync
import cats.implicits.*
import cats.MonadThrow

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.error.ConfigReaderException
import pureconfig.error.ConfigReaderFailures

object syntax {

  extension (source: ConfigSource) {

    def loadF[F[_], A](using reader: ConfigReader[A], F: MonadThrow[F], tag: ClassTag[A]): F[A] =
      F.pure(source.load[A]) //  F[Either[Errors, A]]
        .flatMap {
          case Left(errors) => F.raiseError[A](ConfigReaderException(errors))
          case Right(value) => F.pure(value)
        }

    def loadF2[F[_], A](using reader: ConfigReader[A], F: Sync[F], tag: ClassTag[A]): F[A] =
      F.blocking(source.load[A]) //  F[Either[Errors, A]]
        .flatMap {
          case Left(errors) => F.raiseError[A](ConfigReaderException(errors))
          case Right(value) => F.pure(value)
        }

    def loadF3[F[_], A](using reader: ConfigReader[A], F: Sync[F], tag: ClassTag[A]): F[A] =
      F.blocking(source.loadOrThrow[A])

  }

}
