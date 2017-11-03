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
import com.io7m.cantoria.api.CModuleProvides;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeModuleType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * JLS 9 §13.3:
 *
 * <blockquote>Adding or deleting a uses or provides directive in a module
 * declaration does not break compatibility with pre-existing
 * binaries.</blockquote>
 *
 * This implementation chooses to believe that removing a provider is a
 * backwards-incompatible change according to semantic versioning, because code
 * that implicitly depended on the existence of the provider will fail at
 * run-time now that the provider has been removed.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeModuleServiceNoLongerProvidedType
  extends CChangeModuleType
{
  @Override
  @Value.Parameter
  String module();

  /**
   * @return The provides directive
   */

  @Value.Parameter
  CModuleProvides provides();

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_MAJOR;
  }

  @Override
  default CChangeBinaryCompatibility binaryCompatibility()
  {
    return CChangeBinaryCompatibility.BINARY_COMPATIBLE;
  }

  @Override
  default CChangeSourceCompatibility sourceCompatibility()
  {
    return CChangeSourceCompatibility.SOURCE_COMPATIBLE;
  }
}
