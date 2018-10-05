package io.tvc.tagless
import scala.language.higherKinds
import scala.reflect.macros.blackbox


class Utilities[C <: blackbox.Context](val c: C) extends AnyVal {
  import c.universe._

  /**
    * Given a trait parameterised on some F,
    * produce implementations for each of the trait's methods given the method symbol
    * by calling the provided fn
    */
  def mapMethods[T[_[_]], F[_]](fn: MethodSymbol => Tree)(
    implicit
    T: c.WeakTypeTag[T[F]],
    F: c.WeakTypeTag[F[Unit]]
  ): c.Expr[T[F]] = {
    val traitType = T.tpe
    val methods = traitType.decls.collect { case d if d.isMethod && d.isAbstract => d.asMethod }
    val traitInfo = traitType.typeSymbol.asClass
    c.Expr[T[F]](
      q"""
       new ${traitInfo.name}[${F.tpe.typeConstructor}] {
          ..${methods.map(m => method(m)(fn(m))) }
          type T = String
       }
     """
    )
  }

  /**
    * Given a method, produce a list
    * of it's type parameters, for substitution into quasiquotes
    */
  def typeParams(method: MethodSymbol): List[Tree] =
    method.typeParams.map(tp => internal.typeDef(tp))

  /**
    * Given a method, produce a list of its parameter lists
    * for substitution into quasiquotes
    */
  def params(method: MethodSymbol): List[List[Tree]] =
    method.paramLists.map(_.map { symbol =>
      val mods = if (symbol.isImplicit) Modifiers(Flag.IMPLICIT) else Modifiers()
      q"$mods val ${symbol.asTerm.name}: ${symbol.typeSignature}"
    })

  /**
    * Produce a method given its symbol and some body
    */
  def method(m: MethodSymbol)(body: Tree): Tree = {
    q"""def ${m.name}[..${typeParams(m)}](...${params(m)}) = $body"""
  }
}
