package io.tvc.tagless

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.whitebox

/**
  * Macro to produce an instance of a trait parameterised on some F[_],
  * that just logs any function calls it receives and returns nothing.
  *
  * This is pretty useless on its own but if you've derived a FunctorK instance
  * for your trait you can map that string into a Const, which has an Applicative instance
  */
object Spy {

  type StringK[A] = String

  def apply[T[_[_]]]: T[StringK] = macro spyImpl[T]

  def spyImpl[T[_[_]]](c: whitebox.Context)(implicit tt: c.WeakTypeTag[T[StringK]]): c.Expr[T[StringK]] = {
    import c.universe._

    def concatParams(pl: List[c.Symbol]): c.universe.Tree = {
      val strings = pl.map(s => q"${s.asTerm.name}.toString()").reduce((a, b) => q"""$a + "," + $b """)
      q""" "(" + $strings + ")""""
    }

    val traitInfo = tt.tpe.typeSymbol.asClass
    val methods = tt.tpe.decls.collect { case d if d.isMethod && d.isAbstract => d.asMethod }
    assert(traitInfo.isAbstract, "Only entirely abstract traits are supported")

    val methodImplementations = methods.map { method =>

      val params: List[List[Tree]] =
        method.paramLists.map(_.map(s => q"val ${s.asTerm.name}: ${s.typeSignature}"))

      val paramStrings: Tree =
        method.paramLists
          .map(pl => concatParams(pl))
          .reduceOption((a, b) => q"$a + $b")
          .getOrElse(q"""""""") // poetry

      q"""
         def ${method.name}(...$params): StringK[${method.returnType}] =
          ${traitInfo.name.toString} + "." + ${method.name.toString} + ...$paramStrings
       """
    }

    c.Expr(
      q"""
       import _root_.io.tvc.tagless.Spy.StringK
       new ${traitInfo.name}[StringK] {
          ..$methodImplementations
          type T = String
       }
     """
    )
  }
}
