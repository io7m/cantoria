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

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeClassType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import static com.io7m.cantoria.api.CAccessibility.PUBLIC;

/**
 * JLS 9, §13.4.3:
 *
 * <blockquote>Changing a class that is not declared public to be declared
 * public does not break compatibility with pre-existing binaries.</blockquote>
 *
 * However, making a class public means that the class is now part of the public
 * API and therefore must be considered a new feature according to semantic
 * versioning.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassBecamePublicType
  extends CChangeClassType
{
  @Override
  @Value.Parameter
  CClass classValue();

  /**
   * @return The previous state of the class
   */

  @Value.Parameter
  CClass classPrevious();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    Preconditions.checkPrecondition(
      this.classValue(),
      this.classValue().accessibility() == PUBLIC,
      c -> "Class must not be public");

    Preconditions.checkPrecondition(
      this.classPrevious(),
      this.classPrevious().accessibility() != PUBLIC,
      c -> "Previous class must be non-public");
  }

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_MINOR;
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
