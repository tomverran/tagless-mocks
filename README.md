### Tagless Mocks

_A macro based mocking library for Scala_

This library is very WIP indeed, I've never used macros before...

#### Rationale

This library is designed to make testing tagless final Scala programs easier by 
generating mock intepreters (i.e. mock trait implementations) for your algebras (i.e. traits)

#### Spies

A `Spy` in mocking parlence is an implementation that does nothing other than record that a particular function was called.
This is useful often when testing functions that purely execute side effects (i.e. return `F[Unit]`) that you really
want to be 100% sure you're going to call in the right circumstances in your code.

The `Spy` implementation in here generates an implementation for your trait where the `F[_]` is a `String`, so all the
function calls will return a `String` which corresponds to the function name + parameters.

```scala
import io.tvc.tagless.Spy

trait SomeAlg[F[_]] {
  def saveUser(id: Int, name: String): F[Unit]
}

val spy = Spy[SomeAlg]
val result = spy.saveUser(id = 1, name = "Tom")
// result: String = SomeAlg.saveUser(1,Tom)
```



