import cats.effect._
import cats.effect.implicits._
import cats.effect.unsafe._
import cats.implicits._

object Threading {

  val basicRuntime: IORuntime = IORuntime(
    compute = IORuntime.createDefaultBlockingExecutionContext("compute")._1,
    blocking = IORuntime.createDefaultBlockingExecutionContext("blocking")._1,
    scheduler = IORuntime.createDefaultScheduler()._1,
    shutdown = () => (),
    config = IORuntimeConfig()
  )
  // override protected def runtime: IORuntime =basicRuntime

  def boundedRuntime(numThreads: Int): IORuntime = {
    lazy val lazyRuntime: IORuntime = {
      IORuntime(
        compute = IORuntime.createDefaultComputeThreadPool(lazyRuntime, numThreads, "compute")._1,
        blocking = IORuntime.createDefaultBlockingExecutionContext("blocking")._1,
        scheduler = IORuntime.createDefaultScheduler()._1,
        shutdown = () => (),
        config = IORuntimeConfig()
      )
    }
    lazyRuntime
  }

  def time(work: IO[Unit]): IO[String] =
    work
      .timed
      .map { case (t, _) =>
        s"The task took ${t.toSeconds} seconds."
      }

  // override def run: IO[Unit] = time(snooze).as(())

}
import Threading._
