import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.UUID

import scala.concurrent.duration.*
import scala.concurrent.duration.Duration
import scala.deriving.Mirror

import cats.effect.syntax._
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntimeConfig

import domain.user.*
import math.Ordering
import org.typelevel.twiddles.syntax.*
import pureconfig.error.CannotParse
import pureconfig.error.ConfigReaderException
import pureconfig.error.FailureReason
import skunk.codec.all.*
import skunk.syntax.all.*
import skunk.AppliedFragment

/**
  * Ordering is a trait whose instances each represent a strategy for sorting instances of a type.
  * To sort instances by one or more member variables, you can take advantage of these built-in
  * orderings using [[Ordering.by]] and [[Ordering.on]]:
  * {{{
  * import scala.util.Sorting
  * val pairs = Array(("a", 5, 2), ("c", 3, 1), ("b", 1, 3))
  *
  * // sort by 2nd element
  * Sorting.quickSort(pairs)(Ordering.by[(String, Int, Int), Int](_._2))
  *
  * // sort by the 3rd element, then 1st
  * Sorting.quickSort(pairs)(Ordering[(Int, String)].on(x => (x._3, x._1)))
  * }}}
  *
  * An `Ordering[T]` is implemented by specifying the [[compare]] method, `compare(a: T, b: T):
  * Int`, which decides how to order two instances `a` and `b`. Instances of `Ordering[T]` can be
  * used by things like `scala.util.Sorting` to sort collections like `Array[T]`. For example:
  *
  * {{{
  * import scala.util.Sorting
  *
  * case class Person(name:String, age:Int)
  * val people = Array(Person("bob", 30), Person("ann", 32), Person("carl", 19))
  *
  * // sort by age
  * object AgeOrdering extends Ordering[Person] {
  *   def compare(a:Person, b:Person) = a.age.compare(b.age)
  * }
  * Sorting.quickSort(people)(AgeOrdering)
  * }}}
  *
  * This trait and [[scala.math.Ordered]] both provide this same functionality, but in different
  * ways. A type `T` can be given a single way to order itself by extending `Ordered`. Using
  * `Ordering`, this same type may be sorted in many other ways. `Ordered` and `Ordering` both
  * provide implicits allowing them to be used interchangeably.
  */

(varchar(32) *: text.opt *: text.opt)

(uuid *: varchar(32) *: text.opt *: text.opt)
  .imap(User.apply)(u => (u.id, u.username, u.imageUrl, u.bio))

//contramap produces an Encoder
(uuid *: varchar(32) *: text.opt *: text.opt)
  .contramap[User](u => (u.id, u.username, u.imageUrl, u.bio))

//map produces a Decoder

(uuid *: varchar(32) *: text.opt *: text.opt).map(User.apply)

// any F[A] with an implicit instance of InvariantSemigroupal
//(IO(2) *: IO(2))

Tuple4

(1, 2, 3, 4)

// (1*:2*:3*:4*:EmptyTuple).asInstanceOf[Int*:Int*:Int*:Int*:EmptyTuple] match {
//     case (hd,tail) =>

// }

val f = text *: text.opt

(uuid *: varchar(32) *: text.opt *: text.opt)
import scala.deriving.*
def to[A <: Product](value: A)(using
  mirror: Mirror.ProductOf[A]
): mirror.MirroredElemTypes = Tuple.fromProductTyped(value)

def from[A](value: Product)(using
  mirror: Mirror.ProductOf[A],
  ev: value.type <:< mirror.MirroredElemTypes
): A = mirror.fromProduct(value)

final case class Vehicle(manufacturer: String, wheels: Int)

to(Vehicle(manufacturer = "Lada", wheels = 4))
// ("Lada", 4)
from[Vehicle](("Simson", 2))
// Vehicle("Simson", 2)

val vehicle = Vehicle("Lada", 4)

vehicle.productElementNames.toList

vehicle

Tuple.fromProductTyped(vehicle)

val intToOption: [T] => T => Option[T] = [T] => (t: T) => Option(t)

intToOption[Int]

val mirror = summon[Mirror.Of[User]]

mirror

type Map[T <: Tuple, F[_]] <: Tuple = // 1
  T match { // 2
    case EmptyTuple => EmptyTuple        // 3
    case h *: t     => F[h] *: Map[t, F] // 4
  }

// Map[String *: Int *: Double *: EmptyTuple, Decoder] =
// // T is not empty, `h = String` and `t = Int *: Double *: EmptyTuple`
// F[String] *: Map[Int *: Double *: EmptyTuple, Decoder] =
// // T is not empty, `h = Int` and `t = Double *: EmptyTuple`
// F[String] *: F[Int] *: Map[Double *: EmptyTuple, Decoder] =
// // T is not empty, `h = Double` and `t = EmptyTuple`
// F[String] *: F[Int] *: F[Double] *: Map[EmptyTuple, Decoder] =
// // T is empty
// F[String] *: F[Int] *: F[Double] *: EmptyTuple =
// // With the regular syntax
// (F[String], F[Int], F[Double])

opaque type Digest = String

object Digest {
  def apply(s: String): Digest = {
    validateString(s)
    s
  }
  def validateString(s: String): Unit = ()
}
IORuntime.global

IORuntime.global.compute

IORuntime.global.config

IORuntime.global.scheduler

IORuntimeConfig(
  cancelationCheckThreshold = 512,
  autoYieldThreshold = 1024,
  enhancedExceptions = true,
  traceBufferSize = 16,
  shutdownHookTimeout = Duration.Inf,
  reportUnhandledFiberErrors = true,
  cpuStarvationCheckInterval = 1.second,
  cpuStarvationCheckInitialDelay = 10.seconds,
  cpuStarvationCheckThreshold = 0.1
)

Math.max(2, Runtime.getRuntime().availableProcessors())

import config.*
import pureconfig.*

ServerConfig

//A config source for the default reference config in Typesafe Config (`reference.conf` resources provided by libraries
ConfigSource.defaultReference

//A config source for the default application config in Typesafe Config (by default `application.conf` in resources
ConfigSource.defaultApplication.at("server").load[ServerConfig].merge //ConfigReaderFailures | ServerConfig

sys.env("JAVA_HOME")
//sys.env("HOST")

//sys.env("TERM")
ConfigSource.defaultApplication.load[AppConfig]

ConfigSource.defaultApplication.at("consumer").load[KafkaConsumerConfig].merge // make sure the names are in kebab case (bootstrap-servers)in the application.conf file

ConfigSource.defaultApplication.at("transactor").load[TransactorConfig]

List(7) *: List(7)

import config.syntax.*
//ConfigSource.default.loadF[IO, AppConfig].map(IO.println(_))
import domain.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

val port: Port = Port(7)
port

import scala.compiletime.*

val one: 1 = 1

//val two:2=3 error

//A literal type and its type aliases can be converted to the corresponding constant value by invoking constValue in the scala.compiletime package:
//To handle non-literal types, the erasedValue method in the same package often comes in handy
type One = 1
val onen: 1 = constValue[1]

val Onen: 1 = constValue[One]

val falsee: false = constValue[false]

val ports: Port.type = Port

//val portss:Port.type=constValue[]
import pureconfig.generic.derivation.default.*
case class ServerConfige(host: Host, port: Port) derives ConfigReader //Ordering

val conf = ConfigSource.default.at("server").loadOrThrow[ServerConfige]

conf.host

conf._2
conf.port

//The inline keyword is an instruction for the compiler to copy the code from the definition site and to compile it at the caller site.

//Inlining offers many advantages, one of which is to help the compiler resolve a type parameter to a concrete type provided by the caller. The inlined method can then pass the resolved T to erasedValue[T].

inline def identity[T]: T = {
  inline erasedValue[T] match {
    case _: T => constValue[T]
  }
}
inline def identity1[T]: T = {
  inline erasedValue[T] match {
    case _: EmptyTuple => EmptyTuple.asInstanceOf[T]
    case _: T          => constValue[T]
  }
}
//Notice that, because of inlining, identity1[EmptyTuple] is compiled the same way as is a direct assignment:
identity[1]

val kl: "stringhello" = identity["stringhello"]

identity1[EmptyTuple]

val emptyTuple = EmptyTuple

inline def identity2[T]: T = inline erasedValue[T] match {
  case _: EmptyTuple => EmptyTuple.asInstanceOf[T]
  case _: (headType *: tailType) =>
    (constValue[headType] *: identity2[tailType]).asInstanceOf[T]
  case _: T => constValue[T]
}

val happyPony = identity2["happy now" *: true *: EmptyTuple]

happyPony

val g: 9 = identity2[9]

identity2["Student"]

// val identityUUID = identity2[java.util.UUID]

// identityUUID

//https://medium.com/@hao.qin/scala-3-enlightenment-unleash-the-power-of-literal-types-41e3436b4df8#:~:text=A%20literal%20type%20is%20a,and%20%E2%80%9CHello%20World!%E2%80%9D.

/**
  * If you don’t specify the type of your val, the compiler will infer the type based on the
  * right-hand side as it did before. If you specify the literal type yourself, the compiler will
  * automatically build a new type behind the scenes, based on what you declared. Any literal can
  * become its own type, and it will be a subtype of its “normal” type, e.g. the type 3 will be a
  * subtype of Int:
  */

type Digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

//val digits: List[Digit] = List(-1, 0, 11)   // does not compile

object Test {
  object Monday
  object Tuesday
  object Wednesday
  object Thursday
  object Friday

  object Saturday
  object Sunday

  type Weekday = Monday.type | Tuesday.type | Wednesday.type | Thursday.type | Friday.type
  type Weekend = Saturday.type | Sunday.type
  type AnyDay  = Weekday | Weekend

}

import Test.*

println("Monday is weekday: " + Monday.isInstanceOf[Weekday])

println("Saturday is weekend: " + Saturday.isInstanceOf[Weekend])

println("Sunday is weekday: " + Sunday.isInstanceOf[Weekday])

// (Monday: AnyDay) match {
//   case _: Weekend => println("shouldn't match")
// }
import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._

object Users {
  implicit val encodeUser: Encoder[User] = Encoder
    .instance[User] { user =>
      Json.obj(
        ("username", Json.fromString(user.username)),
        ("userage", Json.fromInt(3))
      )
    }
    .contramap[User] { user =>
      new User(user.id, user.username, user.imageUrl, user.bio)
    }
}

trait Conversion[X, Y] { self =>

  def apply(x: X): Y

  def map[Z](f: Y => Z) = new Conversion[X, Z] {
    def apply(x: X): Z = f(self.apply(x))
  }

  def contramap[W](f: W => X): Conversion[W, Y] = new Conversion[W, Y] {
    def apply(w: W): Y = self.apply(f(w))
  }

}

final case class Name(value: String)

val encoderName = Encoder[String].contramap[Name](_.value)

case class Toggle(
  id: Option[UUID] = Option(UUID.randomUUID()),
  service: String,
  name: String,
  value: String,
  timestamp: LocalDateTime
)

val encoderToggle: skunk.Encoder[Toggle] =
  (uuid.opt *: text *: varchar(30) *: varchar(50) *: timestamp)
    .contramap[Toggle](t => (t.id, t.service, t.name, t.value, t.timestamp))

val toggle = Toggle(Some(UUID.randomUUID()), "service", "name", "value", LocalDateTime.now())

Tuple.fromProduct(toggle)

Tuple.fromProductTyped(toggle)

// val encoderToggle5: skunk.Encoder[Toggle] =
//   (uuid.opt *: text *: varchar(30) *: varchar(50) *: timestamp)
//     .contramap[Toggle](Tuple.fromProductTyped(_))

val li = (2 to 7).toList.mkString(",")

li
