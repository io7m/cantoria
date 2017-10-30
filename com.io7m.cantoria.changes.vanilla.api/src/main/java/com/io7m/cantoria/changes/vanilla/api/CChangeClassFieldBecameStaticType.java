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
 * JLS 9 §13.4.10:
 *
 * <blockquote>If a field that is not declared private was not declared static
 * and is changed to be declared static, or vice versa, then a linkage error,
 * specifically an IncompatibleClassChangeError, will result if the field is
 * used by a pre-existing binary which expected a field of the other kind. Such
 * changes are not recommended in code that has been widely distributed.
 * </blockquote>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassFieldBecameStaticType
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
      this.field().modifiers().contains(CModifier.STATIC),
      m -> "Modifiers must contain 'static'");

    Preconditions.checkPrecondition(
      this.fieldPrevious().modifiers(),
      !this.fieldPrevious().modifiers().contains(CModifier.STATIC),
      m -> "Previous modifiers must not contain 'static'");

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
