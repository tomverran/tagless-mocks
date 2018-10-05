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

    val traitInfo = T.tpe.typeSymbol.asClass
    val methods = T.tpe.decls.collect { case d if d.isMethod && d.isAbstract => d.asMethod }
    val applicative = F.tpe

    val methodImplementations = methods.map { method =>

      val params: List[List[Tree]] =
        method.paramLists.map(_.map(s => q"val ${s.asTerm.name}: ${s.typeSignature}"))

      val typeParams: List[c.Tree] =
        method.typeParams.map(tp => internal.typeDef(tp))

      q"""
         def ${method.name}[..$typeParams](...$params) =
          cats.Applicative[$applicative].pure(cats.Monoid.apply[${method.returnType.typeArgs.head}].empty)
       """
    }

    c.Expr(
      q"""
       new ${traitInfo.name}[$applicative] {
          ..$methodImplementations
          type T = String
       }
     """
    )
  }
}

