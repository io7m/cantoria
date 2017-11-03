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

import com.io7m.cantoria.api.CAccessibility;
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeFieldType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * JLS 9, §13.4.8:
 *
 * <blockquote>Widely distributed programs should not expose any fields to their
 * clients. Apart from the binary compatibility issues discussed below, this is
 * generally good software engineering practice. Adding a field to a class may
 * break compatibility with pre-existing binaries that are not recompiled.
 *
 * Assume a reference to a field f with qualifying type T. Assume further that f
 * is in fact an instance (respectively static) field declared in a superclass
 * of T, S, and that the type of f is X.
 *
 * If a new field of type X with the same name as f is added to a subclass of S
 * that is a superclass of T or T itself, then a linkage error may occur. Such a
 * linkage error will occur only if, in addition to the above, either one of the
 * following is true:
 *
 * The new field is less accessible than the old one.
 *
 * The new field is a static (respectively instance) field.
 *
 * In particular, no linkage error will occur in the case where a class could no
 * longer be recompiled because a field access previously referenced a field of
 * a superclass with an incompatible type. The previously compiled class with
 * such a reference will continue to reference the field declared in a
 * superclass.</blockquote>
 *
 * In other words, without doing complex and extensive analysis, the addition of
 * public fields should be considered to be a backwards-incompatible change as
 * it may break binary compatibility in various subtle ways.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassFieldAddedPublicType
  extends CChangeFieldType
{
  @Override
  @Value.Parameter
  CField field();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    Preconditions.checkPrecondition(
      this.field().accessibility(),
      this.field().accessibility() != CAccessibility.PRIVATE,
      m -> "Accessibility must not be 'private'");
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
    return CChangeSourceCompatibility.SOURCE_COMPATIBLE;
  }
}
