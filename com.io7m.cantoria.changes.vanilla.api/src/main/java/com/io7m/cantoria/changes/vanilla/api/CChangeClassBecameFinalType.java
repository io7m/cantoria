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
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeClassType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * JLS 9, §13.4.2:
 *
 * <blockquote>If a class that was not declared final is changed to be declared
 * final, then a VerifyError is thrown if a binary of a pre-existing subclass of
 * this class is loaded, because final classes can have no subclasses; such a
 * change is not recommended for widely distributed classes.</blockquote>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassBecameFinalType
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
      this.classValue().modifiers().contains(CModifier.FINAL),
      c -> "Class must be final");

    Preconditions.checkPrecondition(
      this.classPrevious(),
      !this.classPrevious().modifiers().contains(CModifier.FINAL),
      c -> "Previous class must not be final");
  }

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
