package io.tvc.tagless

import scala.language.higherKinds

object SpyTest extends App {

  case class AccountId(value: String) extends AnyVal

  trait Foo[F[_]] {
    def bar(what: String, why: Int)(cat: AccountId): F[Unit]
  }

  val foo = Spy[Foo]
  println(foo.bar("hi", 3)(AccountId("123")))
}
