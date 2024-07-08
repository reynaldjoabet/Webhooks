# Webhooks

```scala
/**
 * A fragment applied to its argument, yielding an existentially-typed fragment + argument pair
 * that can be useful when constructing dynamic queries in application code. Applied fragments must
 * be deconstructed in order to prepare and execute.
 */
sealed trait AppliedFragment { outer

 type A
  def fragment: Fragment[A]
  def argument: A
}

```


Scala case class to json-> Encoder
Json -> Scala case class -> Decoder

Scala case class to Postgres text-format data -> Encoder

Postgres text-format to Scala case class -Decoder

an essential difference between Tuple and List: only Tuple supports type matching, a feature which is fairly accessible and tremendously powerful.


```scala
// a polymorphic function type (or type lambda) argument 
def map[F[_]](f: [t] => t => F[t]): Map[this.type, F]

```

It means that `f` is a function that takes an argument `t` and returns another function `t => F[t]`

```scala
val intToOption: [T] => T => Option[T] = [T] => (t: T) => Option(t)
```

`scala.compiletime.summonInline`
The summoning is delayed until the call has been fully inlined.

By marking a function as `inline` the compiler will always insert the code for the function directly at the call-site

Unfortunately, once we stepped into `inline-land` just compiling is no longer good enough. The code we are inlining, and especially the `summonInline` call, are only going to be fully evaluated when we compile the call-site.

an inline match forces the compiler to try to actually evaluate the pattern-match at compile-time and to choose the correct case branch based on information known at compile-time during inlining


Use this method when you have a type, do not have a value for it but want to pattern match on it.For example, given a type `Tup <: Tuple`, one can pattern-match on it as follows:

```scala
inline erasedValue[Tup] match {
  case _: EmptyTuple => ???
  case _: (h *: t) => ???
}
```
`constValueOpt`
Same as constValue but returns a None if a constant value cannot be constructed from the provided type. Otherwise returns that value wrapped in Some.

`constValueTuple`
Given a tuple type (X1, ..., Xn), returns a tuple value


`constValue`
Given a constant, singleton type `T`, convert it to a value of the same singleton type. For example: `assert(constValue[1] == 1)`.

pattern matching works on values

`scala.compiletime.summonAll`
Given a tuple T, summons each of its member types and returns them in a Tuple

### From Values to Types

```scala
class Typed[A]{
  type Value = A

}

```

The static return type of makeLabellings is `Typed[_ <: Tuple]`, that means that on every recursive step we lose the precise type information we gleaned, and return back an anonymous `Tuple`

transparent inline
Inline methods can additionally be declared transparent. This means that the return type of the inline method can be specialized to a more precise type upon expansion



```scala
/** Converts a tuple `(T1, ..., Tn)` to `(F[T1], ..., F[Tn])` */
type Map[Tup <: Tuple, F[_ <: Union[Tup]]] <: Tuple = Tup match {
  case EmptyTuple => EmptyTuple
  case h *: t => F[h] *: Map[t, F]
}
```

The inline can appear as:

- inline def
- inline parameter
- inline val
- transparent inline def
- inline if
- inline match


`inline parameter` means:

This means that actual arguments to these parameters will be inlined in the body of the inline def. inline parameters have call semantics equivalent to by-name parameters but allow for duplication of the code in the argument.



By default, the Cats Effect runtime executes the IOs sequentially. If multiple IOs are chained together and executed, they’ll be processed in the order of chaining:

```scala
val sequentialOps = IO("Step1") >> IO("Step2") >> IO("Step3")
```
the fiber is not locked to a particular thread. Hence, the runtime can execute the fibers across multiple threads based on the thread availability.

### Outcomes of Fiber Execution
After a fiber is executed, the result can be one of three types: Succeeded, Errored, or Canceled. When a fiber result is joined, its result is of type Outcome[IO, Throwable, A]. If a fiber completes successfully, the outcome will be of type Succeeded. If the fiber execution resulted in a failure, it will have the type Errored. On fiber cancelation, the outcome will be of type Canceled. Therefore, we can pattern-match on the outcome result to handle different scenarios:

```scala
val outcome: IO[String] = fibCancel.flatMap {
  case Outcome.Succeeded(fa) => IO("fiber executed successfully").printIO
  case Outcome.Errored(e)    => IO("error occurred during fiber execution").printIO
  case Outcome.Canceled()    => IO("fiber was canceled!").printIO
}
```

`blocking` and `interruptible/interruptibleMany` functions on IO are so important: they declare to the IO runtime that the effect in question will block a thread, and it must be shifted to a blocking thread:
`IO.blocking(url1 == url2) // => IO[Boolean]`


Is it possible to turn off the CPU starvation checker?

`Yes, set the cpuStarvationCheckInitialDelay to Duration.Inf`

 an unbounded thread pool for blocking tasks and a bounded one for compute tasks

 The IORuntime then is composed of an unbounded blocking pool and a bounded compute pool. The global IORuntime has a compute pool bounded at the number of processors in your computer

 Blocking operations are best scheduled on an unbounded thread pool, while compute-intensive operations are best on a thread pool limited to the number of available processors.

An IORuntime has two thread pools: one for compute operations and another for blocking operations.

IO operations run on the compute pool by default. You can tap into the blocking pool using IO.blocking


Twiddle tuples are built incrementally, element by element, via the `*:` and `EmptyTuple` operations which are also made available in Scala 3 standard library and operate exactly the same. On Scala 2, the Twiddles library defines `*:` and `EmptyTuple` as aliases for Shapeless HLists. Check [Twiddle Lists](https://typelevel.org/skunk/reference/TwiddleLists.html) for more details.
The `~` operator build left-associated nested pairs of values to form tuples.

To keep things simple, we will use regular tuples going forward.

> `Skunk queries` are parameterized sql statements where the input and output types represent the type of parameters and the structure of the result set, respectively. While `skunk commands` are parameterized sql statements by input type only, as it does not return a result set.


Either set a global environment variable or run the sbt in the same session where the environment variables are set(local environment variables)
```sh
server{
    host="localhost"
    host= ${?HTTP_HOST}
    port=8080
    port=${?PORT}
    }
```


```scala
trait Encoder[A] extends Serializable { self =>

  /**
   * Convert a value to JSON.
   */
  def apply(a: A): Json

  /**
   * Create a new [[Encoder]] by applying a function to a value of type `B` before encoding as an
   * `A`.
   */
  final def contramap[B](f: B => A): Encoder[B] = new Encoder[B] {
    final def apply(a: B): Json = self(f(a))
  }
}


```

`def consumeEvent: F[Unit]`

This is a parameterless method that does not take any arguments and is generally used for properties or constants that are stable and should not perform side effects.

It is called without parentheses, like accessing a value

It is often used for methods that are idempotent and do not have side effects, resembling a field or property

`def consumeEvent(): F[Unit]`


This is a parameterless method with an empty parameter list and is generally used for methods that may have side effects or perform some actions.

It is called with parentheses, indicating that it performs some computation or action

It is typically used for methods that perform operations, computations, or have side effects.


```scala
def currentTime: String = java.time.Instant.now().toString

// Usage
val time = currentTime
println(time)
```

Here, `currentTime` might be better defined with parentheses because it performs an action (getting the current time), but it's used without parentheses, which suggests it is a stable value


>-- In PostgreSQL, timestamp comparisons are typically done with microsecond precision. However, the timestamp in your 
-- query ('2024-07-05T15:42:17.055') only specifies millisecond precision.
--
-- When PostgreSQL compares a timestamp with microsecond precision to one with only millisecond precision, it 
-- effectively treats the millisecond-precision timestamp as a range. In this case, '2024-07-05T15:42:17.055' is 
-- interpreted as the range from '2024-07-05T15:42:17.055000' to '2024-07-05T15:42:17.055999'.
--
-- As a result, a row with created_at = '2024-07-05 15:42:17.055000' (or any microsecond value up to .055999) would be 
-- considered greater than '2024-07-05T15:42:17.055' in this comparison.
--
-- This behavior can lead to unexpected results and is generally considered a quirk or potential pitfall when working 
-- with PostgreSQL timestamps.
CREATE DOMAIN timestamptz_ms AS TIMESTAMP(3) WITH TIME ZONE;
CREATE DOMAIN timestamp_ms AS TIMESTAMP(3) WITHOUT TIME ZONE;