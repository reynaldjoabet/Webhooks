import cats.effect.std.Semaphore

import config.*
import config._
import domain.*
import domain.user.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader
import pureconfig.ConfigSource
import skunk.codec.all.*
import skunk.syntax.all.*
import skunk.Codec

object Main extends App {

  println("Hello, World!")

  val userCodec: Codec[User] = (uuid *: varchar(32) *: text.opt *: text.opt)
    .imap(User.apply)(u => (u.id, u.username, u.imageUrl, u.bio))

  // val k = sys.env("PORT")
  val c = ConfigSource.default.loadOrThrow[AppConfig]
  case class ServerConfige(host: Host, port: Port) derives ConfigReader // Ordering

  val conf = ConfigSource.default.at("server").loadOrThrow[ServerConfige]

  println(c)
  println(conf)

}
