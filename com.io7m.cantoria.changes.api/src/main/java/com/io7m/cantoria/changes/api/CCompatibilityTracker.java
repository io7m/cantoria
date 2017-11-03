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

package com.io7m.cantoria.changes.api;

import com.io7m.cantoria.api.CVersion;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.Objects;

import static com.io7m.cantoria.changes.api.CChangeBinaryCompatibility.BINARY_INCOMPATIBLE;
import static com.io7m.cantoria.changes.api.CChangeSemanticVersioning.maximum;
import static com.io7m.cantoria.changes.api.CChangeSourceCompatibility.SOURCE_INCOMPATIBLE;

/**
 * A tracker for compatibility values.
 */

public final class CCompatibilityTracker
{
  private CChangeBinaryCompatibility binary;
  private CChangeSourceCompatibility source;
  private CChangeSemanticVersioning semver;

  private CCompatibilityTracker()
  {
    this.binary = CChangeBinaryCompatibility.BINARY_COMPATIBLE;
    this.source = CChangeSourceCompatibility.SOURCE_COMPATIBLE;
    this.semver = CChangeSemanticVersioning.SEMANTIC_NONE;
  }

  /**
   * @return A new compatibility tracker initialized to "compatible" values
   */

  public static CCompatibilityTracker create()
  {
    return new CCompatibilityTracker();
  }

  /**
   * Calculate the current compatibility values based on a new change {@code
   * c}.
   *
   * @param c The change
   */

  public void onChange(
    final CChangeType c)
  {
    Objects.requireNonNull(c, "Change");

    this.semver = maximum(this.semver, c.semanticVersioning());
    if (c.binaryCompatibility() == BINARY_INCOMPATIBLE) {
      this.binary = BINARY_INCOMPATIBLE;
    }
    if (c.sourceCompatibility() == SOURCE_INCOMPATIBLE) {
      this.source = SOURCE_INCOMPATIBLE;
    }
  }

  /**
   * @return The current degree of binary compatibility
   */

  public CChangeBinaryCompatibility binaryCompatibility()
  {
    return this.binary;
  }

  /**
   * @return The current degree of source compatibility
   */

  public CChangeSourceCompatibility sourceCompatibility()
  {
    return this.source;
  }

  /**
   * @return The current semantic versioning information
   */

  public CChangeSemanticVersioning semanticVersioning()
  {
    return this.semver;
  }

  /**
   * Suggest what the current module version number should be based on the given
   * version and current compatibility values.
   *
   * @param version The current version
   *
   * @return The new version
   */

  public CVersion suggestVersionNumber(
    final CVersion version)
  {
    if (version.major() < 1) {
      return CVersion.of(
        version.major(),
        version.minor(),
        version.patch() + 1,
        "");
    }

    switch (this.semanticVersioning()) {
      case SEMANTIC_MAJOR: {
        return CVersion.of(
          version.major() + 1,
          0,
          0,
          "");
      }
      case SEMANTIC_MINOR: {
        return CVersion.of(
          version.major(),
          version.minor() + 1,
          0,
          "");
      }
      case SEMANTIC_NONE: {
        return CVersion.of(
          version.major(),
          version.minor(),
          version.patch() + 1,
          "");
      }
    }

    throw new UnreachableCodeException();
  }
}
