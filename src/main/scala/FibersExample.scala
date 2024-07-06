import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

import cats.effect._
import cats.effect.kernel._
import cats.effect.metrics.CpuStarvationWarningMetrics
import cats.effect.unsafe.IORuntime
import cats.effect.unsafe.IORuntimeBuilder
import cats.effect.unsafe.IORuntimeConfig
import cats.effect.Outcome

object FibersExample extends IOApp.Simple {

  implicit class Xtensions[A](io: IO[A]) {

    def printIO: IO[A] =
      for {
        a <- io
        _  = println(s"[${Thread.currentThread().getName}] " + a)
      } yield a

  }

  val io: IO[String] = IO("Starting a task").printIO >> IO.sleep(400.millis) >> IO("Task completed")
    .printIO

  val fibExec = for {
    fib <- io.start
    _   <- fib.join
  } yield ()

  val fibCancel = for {
    fib     <- io.start
    _       <- IO.sleep(100.millis) >> fib.cancel >> IO("Fiber cancelled").printIO
    outcome <- fib.join
  } yield outcome

  val outcome = fibCancel.flatMap {
    case Outcome.Succeeded(fa) => IO("fiber executed successfully").printIO
    case Outcome.Errored(e)    => IO("error occurred during fiber execution").printIO
    case Outcome.Canceled()    => IO("fiber was canceled!").printIO
  }

  override def run: IO[Unit] = fibExec

  override protected def blockedThreadDetectionEnabled  = true
  override protected def runtimeConfig: IORuntimeConfig = ???

  override protected def MainThread: ExecutionContext = ???

  override protected def computeWorkerThreadCount: Int =
    Math.max(2, Runtime.getRuntime().availableProcessors())

  override protected def logNonDaemonThreadsInterval: FiniteDuration = ???

  override protected def reportFailure(err: Throwable): IO[Unit] = ???

  override protected def logNonDaemonThreadsEnabled: Boolean = ???

  override protected def onCpuStarvationWarn(metrics: CpuStarvationWarningMetrics): IO[Unit] = ???

  override protected def runtime: IORuntime = ???

}
