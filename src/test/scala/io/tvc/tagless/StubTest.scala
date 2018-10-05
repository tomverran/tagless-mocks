package io.tvc.tagless

import cats.Id
import org.scalatest.{Matchers, WordSpec}
import cats.instances.list._
import cats.instances.option._

import scala.language.higherKinds

class StubTest extends WordSpec with Matchers {

  "Stub macro" should {

    "Work for functions with no param lists" in {
      trait Test[F[_]] { def foo(): F[List[Unit]] }
      Stub[Test, Id].foo() shouldBe List.empty
    }
  }
}
