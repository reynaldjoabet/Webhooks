import java.util.UUID

import scala.deriving.Mirror

import cats.effect.{IO, Resource}
import cats.syntax.all.*

import domain.user.*
import domain.user.{FullUser, User, UserCredentials}
import org.http4s.server.staticcontent.FileService.Config
import pureconfig.error.CannotConvert
import pureconfig.ConfigReader
import pureconfig.ConfigSource
import skunk.~
import skunk.codec
//import skunk.*
import skunk.codec.all.*
import skunk.data
import skunk.exception
import skunk.feature
import skunk.featureFlags
import skunk.implicits
import skunk.net
import skunk.syntax
import skunk.syntax.all.*
import skunk.util
import skunk.AppliedFragment
import skunk.Channel
import skunk.Codec
import skunk.Command
import skunk.Cursor
import skunk.Decoder
import skunk.Encoder
import skunk.Fragment
import skunk.PreparedCommand
import skunk.PreparedQuery
import skunk.Query
import skunk.RedactionStrategy
import skunk.SSL
import skunk.Session
import skunk.SessionPool
import skunk.SqlState
import skunk.Statement
import skunk.Strategy
import skunk.Transaction
import skunk.Void

private val user = (uuid, varchar(32), text.opt, text.opt).tupled.to[User]

user

(1, 'a').map[[X] =>> Option[X]]([T] => (t: T) => Some(t))

(1, 'a').map[[X] =>> X]([T] => (t: T) => t)

(uuid *: varchar(32) *: text.opt *: text.opt)

private val fullUser: Codec[FullUser] = (uuid, varchar(32), varchar(128), text, text.opt, text.opt)
  .tupled
  .to[FullUser]
private val userCredentials: Codec[UserCredentials] = (varchar(32), text).tupled.to[UserCredentials]

private val _getCredentials: Query[String, UserCredentials] =
  sql"SELECT username, password FROM app_user WHERE username = ${varchar(32)}"
    .query(userCredentials)

private val _getById: Query[UUID, User] =
  sql"SELECT id, username, image_url, bio FROM app_user WHERE id = $uuid".query(user)

private val _getByUsername: Query[String, User] =
  sql"SELECT id, username, image_url, bio FROM app_user WHERE username = ${varchar(32)}".query(user)

private val _create: Query[FullUser, User] =
  sql"""
    INSERT INTO app_user (id, username, email, password, image_url, bio)
    VALUES $fullUser
    RETURNING id, username, email, image_url, bio
  """.query(user)
