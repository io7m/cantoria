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
import io.vavr.collection.Set;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Objects;

/**
 * JLS 9 §13.4.8:
 *
 * <blockquote>If a new field of type X with the same name as f is added to a
 * subclass of S that is a superclass of T or T itself, then a linkage error may
 * occur. Such a linkage error will occur only if, in addition to the above,
 * either one of the following is true:
 *
 * The new field is less accessible than the old one.
 *
 * The new field is a static (respectively instance) field.</blockquote>
 *
 * In other words, overriding a static field with a non-static field (or vice
 * versa) will result in a LinkageError at runtime.
 *
 * @see LinkageError
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassFieldOverrideChangedStaticType
  extends CChangeFieldType
{
  @Override
  @Value.Parameter
  CField field();

  /**
   * @return The ancestor field
   */

  @Value.Parameter
  CField fieldAncestor();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final Set<CModifier> mod_curr = this.field().modifiers();
    final Set<CModifier> mod_super = this.fieldAncestor().modifiers();

    Preconditions.checkPrecondition(
      mod_curr.contains(CModifier.STATIC) != mod_super.contains(CModifier.STATIC),
      "The current and ancestor field cannot have the same static modifier");

    Preconditions.checkPrecondition(
      Objects.equals(this.field().name(), this.fieldAncestor().name()),
      "Field names must match");
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
