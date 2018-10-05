package io.tvc.tagless


import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

/**
  * Macro to produce an instance of a trait (T) parameterised on some F[_],
  * where there exists a MonoidK[F] in scope. The empty F[_] value is then always returned
  */
object StubK {

  def apply[T[_[_]], F[_]]: T[F] = macro stubImpl[T, F]

  def stubImpl[T[_[_]], F[_]](c: blackbox.Context)(
    implicit
    T: c.WeakTypeTag[T[F]],
    F: c.WeakTypeTag[F[Unit]]
  ): c.Expr[T[F]] = {
    import c.universe._
    new Utilities[c.type](c).mapMethods[T, F] { _ =>
      q"cats.MonoidK.apply[${F.tpe.typeConstructor}].empty"
    }
  }
}

