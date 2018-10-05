package io.tvc.tagless

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

/**
  * Macro to produce an instance of a trait parameterised on some F[_],
  * where there is a Monoid instance for everything returned by all the functions called within,
  * and F[_] is an applicative. Each function will then be F.pure[M.empty]
  */
object Stub {

  def apply[T[_[_]], F[_]]: T[F] = macro stubImpl[T, F]

  def stubImpl[T[_[_]], F[_]](c: blackbox.Context)(
    implicit
    T: c.WeakTypeTag[T[F]],
    F: c.WeakTypeTag[F[Unit]]
  ): c.Expr[T[F]] = {
    import c.universe._

    val utils = new Utilities[c.type](c)
    utils.mapMethods[T, F] { method =>
      q"""cats.Applicative[${F.tpe.typeConstructor}].pure(cats.Monoid.apply[${method.returnType.typeArgs.head}].empty)"""
    }
  }
}

