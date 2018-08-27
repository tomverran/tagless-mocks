package io.tvc.tagless

import io.tvc.tagless.Spy.StringK
import org.scalatest.{Matchers, WordSpec}

import scala.language.higherKinds

class SpyTest extends WordSpec with Matchers {

  "Spy macro" should {

    "Work for traits with no functions" in {
      trait Test[F[_]]
      Spy[Test]
      succeed
    }

    "Work for functions with no param lists" in {
      trait Test[F[_]] { def foo: F[Unit] }
      Spy[Test].foo shouldBe "Test.foo"
    }

    "Work for functions with a single param list" in {
      trait Test[F[_]] { def foo(bar: String): F[Unit] }
      Spy[Test].foo("bar") shouldBe "Test.foo(bar)"
    }

    "Work for functions with multiple param lists" in {
      trait Test[F[_]] { def foo(bar: String)(baz: Int): F[Unit] }
      Spy[Test].foo("bar")(3) shouldBe "Test.foo(bar)(3)"
    }

    "Work for polymorphic functions" in {
      trait Test[F[_]] { def foo[A, B, C](a: A): F[Unit] }
      Spy[Test].foo("bar") shouldBe "Test.foo[A,B,C](bar)"
    }
  }
}
