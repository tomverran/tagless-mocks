package io.tvc.tagless


import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox

/**
  * Macro to produce an instance of a trait parameterised on some F[_],
  * where there exists a MonoidK[F] in scope. The empty F[_] value is then always returned
  */
object StubK {

  def apply[T[_[_]], F[_]]: T[F] = macro stubImpl[T, F]

  def stubImpl[T[_[_]], F[_]](c: whitebox.Context)(
    implicit
    T: c.WeakTypeTag[T[F]],
    F: c.WeakTypeTag[F[Unit]]
  ): c.Expr[T[F]] = {
    import c.universe._

    val traitInfo = T.tpe.typeSymbol.asClass
    val methods = T.tpe.decls.collect { case d if d.isMethod && d.isAbstract => d.asMethod }
    val monoid = F.tpe

    val methodImplementations = methods.map { method =>

      val params: List[List[Tree]] =
        method.paramLists.map(_.map(s => q"val ${s.asTerm.name}: ${s.typeSignature}"))

      val typeParams: List[c.Tree] =
        method.typeParams.map(tp => internal.typeDef(tp))

      q"""
         def ${method.name}[..$typeParams](...$params) =
          cats.MonoidK.apply[$monoid].empty
       """
    }

    c.Expr(
      q"""
       new ${traitInfo.name}[$monoid] {
          ..$methodImplementations
          type T = String
       }
     """
    )
  }
}

