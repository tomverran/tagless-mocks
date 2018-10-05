package io.tvc.tagless

import scala.language.experimental.macros
import scala.language.higherKinds
import scala.reflect.macros.blackbox

/**
  * Macro to produce an instance of a trait parameterised on some F[_],
  * that just returns a string representation of which methods were called
  *
  * This is pretty useless on its own but if you've derived a FunctorK instance
  * for your trait you can map that string into a Const, which has an Applicative instance
  */
object Spy {

  type StringK[A] = String

  def apply[T[_[_]]]: T[StringK] = macro spyImpl[T]

  def spyImpl[T[_[_]]](c: blackbox.Context)(implicit tt: c.WeakTypeTag[T[StringK]]): c.Expr[T[StringK]] = {
    import c.universe._

    def concatParams(pl: List[c.Symbol]): c.universe.Tree = {
      val strings = pl.map(s => q"${s.asTerm.name}.toString()").reduce((a, b) => q"""$a + "," + $b """)
      q""" "(" + $strings + ")""""
    }

    val traitInfo = tt.tpe.typeSymbol.asClass
    val utils = new Utilities[c.type](c)

    utils.mapMethods[T, StringK] { method =>
     val typeParams = utils.typeParams(method)

      val typeParamStrings =
        if (typeParams.nonEmpty) {
          s"[${method.typeParams.map(_.asType.name.toString).mkString(",")}]"
        } else {
          ""
        }

      val paramStrings: Tree =
        method.paramLists
          .map(pl => concatParams(pl))
          .reduceOption((a, b) => q"$a + $b")
          .getOrElse(q"""""""") // poetry

      q"""${traitInfo.name.toString} + "." + ${method.name.toString + typeParamStrings} + ...$paramStrings"""
    }
  }
}
