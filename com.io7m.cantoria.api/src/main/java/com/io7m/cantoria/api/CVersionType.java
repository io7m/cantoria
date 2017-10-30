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
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * <p>The version number of an archive.</p>
 *
 * <p>Version numbers must follow the format given in the SemVer
 * specification.</p>
 *
 * <p>Given a version number MAJOR.MINOR.PATCH, increment the:</p>
 *
 * <ul> <li>MAJOR version when you make incompatible API changes</li>
 *
 * <li>MINOR version when you add functionality in a backwards-compatible
 * manner</li>
 *
 * <li>PATCH version when you make backwards-compatible bug fixes</li>
 *
 * </ul>
 *
 * <p>Additional labels for pre-release and build metadata are available as
 * extensions to the MAJOR.MINOR.PATCH format.</p>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CVersionType extends Comparable<CVersionType>
{
  /**
   * @return The major version number, indicating backwards-incompatible changes
   */

  @Value.Parameter(order = 0)
  int major();

  /**
   * @return The minor version number, indicating backwards-compatible changes
   */

  @Value.Parameter(order = 1)
  int minor();

  /**
   * @return The patch version number, indicating bug fixes
   */

  @Value.Parameter(order = 2)
  int patch();

  /**
   * @return The qualifier
   */

  @Value.Parameter(order = 3)
  String qualifier();

  @Override
  default int compareTo(
    final CVersionType other)
  {
    NullCheck.notNull(other, "Other");

    final int r_major =
      Integer.compareUnsigned(this.major(), other.major());
    if (r_major == 0) {
      final int r_minor =
        Integer.compareUnsigned(this.minor(), other.minor());
      if (r_minor == 0) {
        final int r_patch =
          Integer.compareUnsigned(this.patch(), other.patch());
        if (r_patch == 0) {
          return this.qualifier().compareTo(other.qualifier());
        }
        return r_patch;
      }
      return r_minor;
    }
    return r_major;
  }
}
