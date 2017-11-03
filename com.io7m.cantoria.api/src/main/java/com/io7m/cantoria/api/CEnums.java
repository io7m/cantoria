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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.objectweb.asm.tree.ClassNode;

import java.util.Objects;
import java.util.function.Function;

/**
 * Functions over enums.
 */

public final class CEnums
{
  private CEnums()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Construct an enum from the given class node.
   *
   * @param clazz The class name
   *
   * @return An enum
   */

  public static CEnum enumValue(
    final CClass clazz)
  {
    Objects.requireNonNull(clazz, "Class");

    Preconditions.checkPrecondition(
      CClassModifiers.classIsEnum(clazz.node()),
      "Class must be an enum");

    return CEnum.builder()
      .setMembers(enumMembers(clazz.node()))
      .setClassValue(clazz)
      .build();
  }

  private static Map<String, CEnumMember> enumMembers(
    final ClassNode node)
  {
    return List.ofAll(node.fields)
      .filter(CFieldModifiers::fieldIsEnumMember)
      .map(f -> CEnumMember.of(f.name, f))
      .toMap(CEnumMember::name, Function.identity());
  }
}
