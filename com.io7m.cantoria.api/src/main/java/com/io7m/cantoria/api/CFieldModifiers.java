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
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import java.util.Objects;

/**
 * Functions to determine the modifiers of fields.
 */

public final class CFieldModifiers
{
  private CFieldModifiers()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is private
   */

  public static boolean fieldIsPrivate(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is package private
   */

  public static boolean fieldIsPackagePrivate(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is protected
   */

  public static boolean fieldIsProtected(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is public
   */

  public static boolean fieldIsPublic(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is final
   */

  public static boolean fieldIsFinal(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is static
   */

  public static boolean fieldIsStatic(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
  }

  /**
   * @param field The field
   *
   * @return The accessibility of the field
   */

  public static CAccessibility fieldAccessibility(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");

    if (fieldIsPublic(field)) {
      return CAccessibility.PUBLIC;
    }
    if (fieldIsPackagePrivate(field)) {
      return CAccessibility.PACKAGE_PRIVATE;
    }
    if (fieldIsProtected(field)) {
      return CAccessibility.PROTECTED;
    }

    Invariants.checkInvariant(
      fieldIsPrivate(field), "Field must be private");
    return CAccessibility.PRIVATE;
  }

  /**
   * @param field The field
   *
   * @return The field modifiers
   */

  public static Set<CModifier> fieldModifiers(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Method");

    Set<CModifier> m = HashSet.empty();

    if (fieldIsFinal(field)) {
      m = m.add(CModifier.FINAL);
    }
    if (fieldIsStatic(field)) {
      m = m.add(CModifier.STATIC);
    }
    if (fieldIsTransient(field)) {
      m = m.add(CModifier.TRANSIENT);
    }
    if (fieldIsVolatile(field)) {
      m = m.add(CModifier.VOLATILE);
    }

    return m;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is transient
   */

  public static boolean fieldIsTransient(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_TRANSIENT) == Opcodes.ACC_TRANSIENT;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is volatile
   */

  public static boolean fieldIsVolatile(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    return (field.access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE;
  }

  /**
   * @param field The field
   *
   * @return {@code true} iff the field is an enum member
   */

  public static boolean fieldIsEnumMember(
    final FieldNode field)
  {
    Objects.requireNonNull(field, "Field");
    final int mix = fieldEnumMemberModifiers();
    return (field.access & mix) == mix;
  }

  /**
   * @return The combination of modifiers used for enum fields
   */

  public static int fieldEnumMemberModifiers()
  {
    return Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL | Opcodes.ACC_ENUM;
  }
}
