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
import io.vavr.collection.List;

/**
 * Functions to compare type signatures.
 */

public final class CGenericsComparison
{
  private CGenericsComparison()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Determines if two lists of type parameters are equivalent. The parameters
   * are considered to be equivalent if there is a set of names mappings {@code
   * M} such that {@code CGenericsUniqueNames.uniqueTypeParameters(M, a) ==
   * CGenericsUniqueNames.uniqueTypeParameters(M, b)}. This is essentially
   * α-equivalence.
   *
   * @param a A list of type parameters
   * @param b A list of type parameters
   *
   * @return {@code true} iff the type parameters are equivalent
   */

  public static boolean typeParametersAreEquivalent(
    final List<CGTypeParameter> a,
    final List<CGTypeParameter> b)
  {
    final CGenericsUniqueNames.UniqueNamesType names_a =
      CGenericsUniqueNames.emptyUniqueNames();
    final List<CGTypeParameter> ap =
      CGenericsUniqueNames.uniqueTypeParameters(names_a, a);
    final CGenericsUniqueNames.UniqueNamesType names_b =
      CGenericsUniqueNames.emptyUniqueNames();
    final List<CGTypeParameter> bp =
      CGenericsUniqueNames.uniqueTypeParameters(names_b, b);

    return ap.eq(bp);
  }
}