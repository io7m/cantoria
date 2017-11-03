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

import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Functions to parse and transform class names.
 */

public final class CClassNames
{
  private CClassNames()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Parse a fully-qualified dotted class name into a package name and class
   * name.
   *
   * @param name The fully-qualified class name (of the form {@code a.b.C}).
   *
   * @return A pair consisting of a package name and class name
   */

  public static Tuple2<String, String> parseFullyQualifiedDotted(
    final String name)
  {
    Objects.requireNonNull(name, "name");

    final List<String> components =
      List.of(name.split("\\."));

    final String package_name =
      components
        .toStream()
        .dropRight(1)
        .collect(Collectors.joining("."));

    final String class_name =
      components
        .toStream()
        .takeRight(1)
        .get(0);

    return Tuple.of(package_name, class_name);
  }

  /**
   * Transform a class name from {@code a/b/c} form to {@code a.b.c} form.
   *
   * @param name The class name
   *
   * @return The transformed class name
   */

  public static String toDottedName(
    final String name)
  {
    Objects.requireNonNull(name, "Name");
    return name.replace('/', '.');
  }

  /**
   * Transform a class name from {@code a.b.c} form to {@code a/b/c} form.
   *
   * @param name The class name
   *
   * @return The transformed class name
   */

  public static String toDashedName(
    final String name)
  {
    Objects.requireNonNull(name, "Name");
    return name.replace('.', '/');
  }

  /**
   * Pretty print the given class name.
   *
   * @param name The name
   *
   * @return A pretty-printed name
   */

  public static String show(
    final CClassName name)
  {
    Objects.requireNonNull(name, "Name");
    return new StringBuilder(64)
      .append(name.moduleName())
      .append("/")
      .append(name.packageName())
      .append(".")
      .append(name.className())
      .toString();
  }
}
