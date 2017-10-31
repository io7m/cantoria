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

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

/**
 * Functions to determine the modifiers of classes.
 */

public final class CClassModifiers
{
  private CClassModifiers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is private
   */

  public static boolean classIsPrivate(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is an enum
   */

  public static boolean classIsEnum(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is package private
   */

  public static boolean classIsPackagePrivate(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is protected
   */

  public static boolean classIsProtected(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is public
   */

  public static boolean classIsPublic(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is final
   */

  public static boolean classIsFinal(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is final
   */

  public static boolean classIsAbstract(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} iff the class is static
   */

  public static boolean classIsStatic(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
  }

  /**
   * @param clazz The class
   *
   * @return {@code true} if the class is an interface
   */

  public static boolean classIsInterface(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");
    return (clazz.access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
  }

  /**
   * @param clazz The class
   *
   * @return The set of class modifiers
   */

  public static Set<CModifier> classModifiers(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");

    Set<CModifier> m = HashSet.empty();

    if (classIsAbstract(clazz)) {
      m = m.add(CModifier.ABSTRACT);
    }
    if (classIsFinal(clazz)) {
      m = m.add(CModifier.FINAL);
    }
    if (classIsStatic(clazz)) {
      m = m.add(CModifier.STATIC);
    }
    if (classIsEnum(clazz)) {
      m = m.add(CModifier.ENUM);
    }
    if (classIsInterface(clazz)) {
      m = m.add(CModifier.INTERFACE);
    }

    return m;
  }

  /**
   * @param clazz The class
   *
   * @return The accessibility of the class
   */

  public static CAccessibility classAccessibility(
    final ClassNode clazz)
  {
    NullCheck.notNull(clazz, "Class");

    if (classIsPublic(clazz)) {
      return CAccessibility.PUBLIC;
    }
    if (classIsProtected(clazz)) {
      return CAccessibility.PROTECTED;
    }
    if (classIsPrivate(clazz)) {
      return CAccessibility.PRIVATE;
    }
    return CAccessibility.PACKAGE_PRIVATE;
  }
}
