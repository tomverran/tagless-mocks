### Tagless Mocks

_A macro based mocking library for Scala_

This library is very WIP indeed, I've never used macros before...

#### Rationale

This library is designed to make testing tagless final Scala programs easier by 
generating mock intepreters (i.e. mock trait implementations) for your algebras (i.e. traits)


#### Planned Features

- A version of Spy that gives you a WriterT somehow
- Less code duplication
- A build in Travis
- Bintray release


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

#### Stubs

Stubs return an empty value when called. In the world of Java this tends to be `null` but cats provides the `Monoid` typeclass for this
so there are two variants of stubs available

- StubK, for when your effect type is directly a `MonoidK` (like `Option`)
- Stub, which needs an `Applicative` for your effect type and a `Monoid` for all your return types

e.g. if you have something like

```scala
trait SomeAlg[F[_]] {
  def findUsers(id: Int, name: String): F[List[String]]
}
```

You can do the following

```scala
import cats.instances.option._
val stubk = StubK[SomeAlg, Option]
stubk.findUsers(1, "Tom") // will return None

import cats.instances.list._
val stub = Stub[SomeAlg, Option]
stub.findUsers(1, "Tom") // will return Some(List.empty)
```


