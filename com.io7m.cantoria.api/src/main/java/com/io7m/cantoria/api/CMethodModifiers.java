/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.cantoria.api;

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.Objects;

/**
 * Functions to determine method modifiers.
 */

public final class CMethodModifiers
{
  private CMethodModifiers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is private
   */

  public static boolean methodIsPrivate(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is package private
   */

  public static boolean methodIsPackagePrivate(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is protected
   */

  public static boolean methodIsProtected(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is public
   */

  public static boolean methodIsPublic(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is final
   */

  public static boolean methodIsFinal(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is static
   */

  public static boolean methodIsStatic(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is abstract
   */

  public static boolean methodIsAbstract(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT;
  }

  /**
   * @param method The method
   *
   * @return The accessibility of the method
   */

  public static CAccessibility methodAccessibility(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");

    if (methodIsPublic(method)) {
      return CAccessibility.PUBLIC;
    }
    if (methodIsPackagePrivate(method)) {
      return CAccessibility.PACKAGE_PRIVATE;
    }
    if (methodIsProtected(method)) {
      return CAccessibility.PROTECTED;
    }

    Invariants.checkInvariant(
      methodIsPrivate(method), "Method must be private");
    return CAccessibility.PRIVATE;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is an instance constructor
   */

  public static boolean methodIsInstanceConstructor(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return Objects.equals(method.name, "<init>");
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is a static initializer
   */

  public static boolean methodIsStaticInitializer(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return Objects.equals(method.name, "<clinit>");
  }

  /**
   * @param method The method
   *
   * @return The modifiers for the method
   */

  public static Set<CModifier> methodModifiers(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");

    Set<CModifier> m = HashSet.empty();

    if (methodIsAbstract(method)) {
      m = m.add(CModifier.ABSTRACT);
    }
    if (methodIsFinal(method)) {
      m = m.add(CModifier.FINAL);
    }
    if (methodIsStatic(method)) {
      m = m.add(CModifier.STATIC);
    }
    if (methodIsSynchronized(method)) {
      m = m.add(CModifier.SYNCHRONIZED);
    }
    if (methodIsVarArgs(method)) {
      m = m.add(CModifier.VARARGS);
    }

    return m;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is synchronized
   */

  public static boolean methodIsSynchronized(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_SYNCHRONIZED) == Opcodes.ACC_SYNCHRONIZED;
  }

  /**
   * @param method The method
   *
   * @return {@code true} iff the method is variadic
   */

  public static boolean methodIsVarArgs(
    final MethodNode method)
  {
    NullCheck.notNull(method, "Method");
    return (method.access & Opcodes.ACC_VARARGS) == Opcodes.ACC_VARARGS;
  }
}
