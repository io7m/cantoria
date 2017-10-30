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

import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.spi.CChangeFieldType;
import com.io7m.cantoria.changes.spi.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.spi.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Objects;

/**
 * JLS 9 §13.4.9:
 *
 * <blockquote>Deleting the keyword final or changing the value to which a field
 * is initialized does not break compatibility with existing binaries.
 *
 * If a field is a constant variable (§4.12.4), and moreover is static, then
 * deleting the keyword final or changing its value will not break compatibility
 * with pre-existing binaries by causing them not to run, but they will not see
 * any new value for a usage of the field unless they are recompiled. This
 * result is a side-effect of the decision to support conditional compilation
 * (§14.21). (One might suppose that the new value is not seen if the usage
 * occurs in a constant expression (§15.28) but is seen otherwise. This is not
 * so; pre-existing binaries do not see the new value at all.)</blockquote>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassFieldBecameNonFinalType
  extends CChangeFieldType
{
  @Override
  @Value.Parameter
  CField field();

  /**
   * @return The previous state of the field
   */

  @Value.Parameter
  CField fieldPrevious();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    Preconditions.checkPrecondition(
      this.field().modifiers(),
      !this.field().modifiers().contains(CModifier.FINAL),
      m -> "Modifiers must not contain 'final'");

    Preconditions.checkPrecondition(
      this.fieldPrevious().modifiers(),
      this.fieldPrevious().modifiers().contains(CModifier.FINAL),
      m -> "Previous modifiers must contain 'final'");

    Preconditions.checkPrecondition(
      Objects.equals(this.field().name(), this.fieldPrevious().name()),
      "Field names must match");

    Preconditions.checkPrecondition(
      Objects.equals(
        this.field().className(),
        this.fieldPrevious().className()),
      "Field class names must match");
  }

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_NONE;
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
