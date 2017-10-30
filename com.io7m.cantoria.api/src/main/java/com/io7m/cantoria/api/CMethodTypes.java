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
import io.vavr.collection.List;
import org.objectweb.asm.Type;

/**
 * Functions over method types.
 */

public final class CMethodTypes
{
  private CMethodTypes()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Parse the given type descriptor and return the list of parameter types.
   *
   * @param signature The method type descriptor
   *
   * @return A list of parameter types
   *
   * @see org.objectweb.asm.tree.MethodNode#desc
   */

  public static List<String> parseParameterTypesFromSignature(
    final String signature)
  {
    NullCheck.notNull(signature, "Signature");
    final Type r = Type.getMethodType(signature);
    return List.of(r.getArgumentTypes()).map(Type::getClassName);
  }

  /**
   * Parse the given type descriptor and return the return type
   *
   * @param signature The method type descriptor
   *
   * @return The return type
   *
   * @see org.objectweb.asm.tree.MethodNode#desc
   */

  public static String parseReturnTypeFromSignature(
    final String signature)
  {
    NullCheck.notNull(signature, "Signature");
    final Type r = Type.getReturnType(signature);
    return r.getClassName();
  }
}
