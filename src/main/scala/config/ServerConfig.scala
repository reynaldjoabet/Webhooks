package config

import ciris.*
import pureconfig.generic.derivation.default.*
import pureconfig.ConfigReader

final case class ServerConfig(port: Int, host: String) derives ConfigReader {

  override def toString(): String = s"""
                                       |host: $host
                                       |port: $port
    """.stripMargin

}

object ServerConfig {

  implicit val serverConfig: ConfigValue[[x] =>> Effect[x], ServerConfig] = for {
    port <- env("HTTP_PORT").or(prop("http.port")).as[Int]
    host <- env("HTTP_HOST").or(prop("http.host")).as[String]
  } yield ServerConfig(port, host)

}
