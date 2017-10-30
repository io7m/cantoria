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

package com.io7m.cantoria.changes.vanilla.api;

import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.changes.spi.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.spi.CChangeModuleType;
import com.io7m.cantoria.changes.spi.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.spi.CChangeSourceCompatibility;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * JLS 9 §13.3:
 *
 * <blockquote>Adding a requires directive to a module declaration, or adding
 * the transitive modifier to a requires directive, does not break compatibility
 * with pre-existing binaries. However, it may prevent the program from
 * starting, since the module may now read multiple modules that export packages
 * with the same name.
 *
 * Deleting a requires directive in a module declaration, or deleting the
 * transitive modifier from a requires directive, may break compatibility with
 * any pre-existing binary that relied on the directive or modifier for
 * readability of a given module in the course of referencing types exported by
 * that module. An IllegalAccessError may be thrown when such a reference from a
 * pre-existing binary is linked.</blockquote>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeModuleNoLongerRequiredType
  extends CChangeModuleType
{
  @Override
  @Value.Parameter
  String module();

  /**
   * @return The name of the module being required
   */

  @Value.Parameter
  String moduleTarget();

  /**
   * @return {@code true} iff the requirement was transitive
   */

  @Value.Parameter
  boolean isTransitive();

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_MAJOR;
  }

  @Override
  default CChangeBinaryCompatibility binaryCompatibility()
  {
    return CChangeBinaryCompatibility.BINARY_INCOMPATIBLE;
  }

  @Override
  default CChangeSourceCompatibility sourceCompatibility()
  {
    return CChangeSourceCompatibility.SOURCE_INCOMPATIBLE;
  }
}
