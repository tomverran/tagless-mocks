package io.tvc.tagless

import org.scalatest.{Matchers, WordSpec}

import scala.language.higherKinds
import cats.instances.option._

class StubKTest extends WordSpec with Matchers {

  "StubK macro" should {

    "Work for functions with no param lists" in {
      trait Test[F[_]] { def foo: F[Unit] }
      StubK[Test, Option].foo shouldBe None
    }
  }
}
