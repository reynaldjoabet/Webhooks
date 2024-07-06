package domain

import java.util.UUID

import cats.syntax.all.*

import io.circe

object user {

  case class User(
    id: UUID,
    username: String,
    imageUrl: Option[String],
    bio: Option[String]
  ) derives circe.Codec.AsObject {

    def toUserId: UserId =
      UserId(id, username)

  }

  case class FullUser(
    id: UUID,
    username: String,
    email: String,
    password: String,
    imageUrl: Option[String],
    bio: Option[String]
  ) derives circe.Codec.AsObject {

    def toUser: User =
      User(id, username, imageUrl, bio)

    def toCredentials: UserCredentials =
      UserCredentials(username, password)

    def toUserId: UserId =
      UserId(id, username)

  }

  case class UserCredentials(username: String, password: String) derives circe.Codec.AsObject

  case class UserToken(userId: UUID, token: String, expires: Long) derives circe.Codec.AsObject

  case class UserId(id: UUID, username: String)

}
