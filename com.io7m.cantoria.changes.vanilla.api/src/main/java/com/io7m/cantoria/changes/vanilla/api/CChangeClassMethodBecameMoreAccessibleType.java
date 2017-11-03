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

import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeMethodType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Objects;

/**
 * JLS 9 §13.4.7:
 *
 * <blockquote>Perhaps surprisingly, the binary format is defined so that
 * changing a member or constructor to be more accessible does not cause a
 * linkage error when a subclass (already) defines a method to have less
 * access.</blockquote>
 *
 * Making a field more accessible than it was previously is a binary-compatible
 * change, but is potentially source-incompatible.
 *
 * @see CChangeClassMethodRemoved
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassMethodBecameMoreAccessibleType
  extends CChangeMethodType
{
  @Override
  default CClassName className()
  {
    return this.method().className();
  }

  @Override
  @Value.Parameter
  CMethod method();

  /**
   * @return The previous state of the method
   */

  @Value.Parameter
  CMethod methodPrevious();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    Preconditions.checkPrecondition(
      this.method().accessibility(),
      this.method().accessibility().accessibility()
        > this.methodPrevious().accessibility().accessibility(),
      m -> "Accessibility must have been increased");

    Preconditions.checkPrecondition(
      Objects.equals(this.method().name(), this.methodPrevious().name()),
      "Method names must match");

    Preconditions.checkPrecondition(
      Objects.equals(
        this.method().className(),
        this.methodPrevious().className()),
      "Method class names must match");
  }

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
    return CChangeSourceCompatibility.SOURCE_INCOMPATIBLE;
  }
}
