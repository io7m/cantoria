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
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.io.IOException;
import java.util.Optional;

/**
 * Functions over classes.
 */

public final class CClasses
{
  private CClasses()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Determine all superclasses of the given class. The result will not include
   * {@code c}.
   *
   * @param registry The class registry
   * @param c        The current class
   *
   * @return A list of superclasses, most distance ancestor first
   *
   * @throws IOException On I/O errors
   */

  public static List<CClass> superclassesOf(
    final CClassRegistryType registry,
    final CClass c)
    throws IOException
  {
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(c, "Class");

    CClass current = c;
    List<CClass> supers = List.empty();

    while (current.node().superName != null) {
      final Tuple2<String, String> pair =
        CClassNames.parseFullyQualifiedDotted(
          CClassNames.toDottedName(current.node().superName));

      final String package_name = pair._1;
      final String class_name = pair._2;

      final Optional<CClass> c_opt =
        registry.findClass(package_name, class_name);

      if (c_opt.isPresent()) {
        current = c_opt.get();
      } else {
        break;
      }
      supers = supers.prepend(current);
    }

    return supers;
  }
}
