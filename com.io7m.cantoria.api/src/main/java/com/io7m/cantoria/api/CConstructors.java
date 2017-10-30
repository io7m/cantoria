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

import java.util.stream.Collectors;

/**
 * Functions over constructors.
 */

public final class CConstructors
{
  private CConstructors()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Pretty print the given constructor.
   *
   * @param constructor The constructor
   *
   * @return A pretty printed constructor
   */

  public static String show(
    final CConstructor constructor)
  {
    NullCheck.notNull(constructor, "Constructor");

    final StringBuilder sb = new StringBuilder(64);
    final String kw = constructor.accessibility().keyword();
    sb.append(kw);
    sb.append(kw.isEmpty() ? "" : " ");

    final String modifiers =
      constructor.modifiers().map(CModifier::keyword)
        .collect(Collectors.joining(" "));
    sb.append(modifiers);
    sb.append(modifiers.isEmpty() ? "" : " ");

    sb.append(constructor.className().className());
    sb.append("(");
    sb.append(constructor.parameterTypes().collect(Collectors.joining(",")));
    sb.append(")");

    if (!constructor.exceptions().isEmpty()) {
      sb.append(" throws ");
      sb.append(constructor.exceptions().collect(Collectors.joining(",")));
    }

    return sb.toString();
  }

  /**
   * Construct a constructor from the given constructor node.
   *
   * @param constructor The constructor
   *
   * @return A constructor
   */

  public static CConstructor constructor(
    final CMethod constructor)
  {
    return CConstructor.of(constructor);
  }
}
