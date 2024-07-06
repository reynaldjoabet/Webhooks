import pureconfig.error.FailureReason
import pureconfig.ConfigReader

package object config {

  opaque type Host = String
  object Host {

    def apply(value: String): Host           = value
    extension (host: Host) def value: String = host
    given Ordering[Host]                     = Ordering.String

    // ConfigReader[String].emap(Right[ConfigReaderException,Host](apply(_)))
    val n: ConfigReader[Host] = ConfigReader
      .stringConfigReader
      .emap(host => Right[FailureReason, Host](Host(host)))

    given portConfigReader: ConfigReader[Host] = ConfigReader.stringConfigReader.map(apply(_))

  }

  opaque type Port = Int
  object Port {

    def apply(value: Int): Port                = value
    extension (port: Port) def value: Int      = port
    given Ordering[Port]                       = Ordering.Int
    given portConfigReader: ConfigReader[Port] = ConfigReader.intConfigReader.map(apply(_))

    // given ConfigReader[Port] = ConfigReader.intConfigReader// .contramapConfig
  }

}
