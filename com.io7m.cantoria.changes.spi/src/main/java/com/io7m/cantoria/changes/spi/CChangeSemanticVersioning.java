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

package com.io7m.cantoria.changes.spi;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * The compatibility according to semantic versioning
 */

public enum CChangeSemanticVersioning
{
  /**
   * The change requires incrementing the major version
   */

  SEMANTIC_MAJOR,

  /**
   * The change requires incrementing the minor version
   */

  SEMANTIC_MINOR,

  /**
   * The change does not require incrementing a version number
   */

  SEMANTIC_NONE;

  /**
   * Determine the maximum of the versioning specifications.
   *
   * @param x The first spec
   * @param y The second spec
   *
   * @return The most severe of the two specifications
   */

  public static CChangeSemanticVersioning maximum(
    final CChangeSemanticVersioning x,
    final CChangeSemanticVersioning y)
  {
    NullCheck.notNull(x, "x");
    NullCheck.notNull(y, "y");

    switch (x) {
      case SEMANTIC_MAJOR: {
        switch (y) {
          case SEMANTIC_MAJOR:
            return SEMANTIC_MAJOR;
          case SEMANTIC_MINOR:
            return SEMANTIC_MAJOR;
          case SEMANTIC_NONE:
            return SEMANTIC_MAJOR;
        }
        break;
      }

      case SEMANTIC_MINOR: {
        switch (y) {
          case SEMANTIC_MAJOR:
            return SEMANTIC_MAJOR;
          case SEMANTIC_MINOR:
            return SEMANTIC_MINOR;
          case SEMANTIC_NONE:
            return SEMANTIC_MINOR;
        }
        break;
      }

      case SEMANTIC_NONE: {
        switch (y) {
          case SEMANTIC_MAJOR:
            return SEMANTIC_MAJOR;
          case SEMANTIC_MINOR:
            return SEMANTIC_MINOR;
          case SEMANTIC_NONE:
            return SEMANTIC_NONE;
        }
        break;
      }
    }

    throw new UnreachableCodeException();
  }
}
