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
import io.vavr.Tuple;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Objects;

/**
 * JLS 9 §13.4.7:
 *
 * <blockquote>Changing the declared access of a member or constructor to permit
 * less access may break compatibility with pre-existing binaries, causing a
 * linkage error to be thrown when these binaries are resolved. Less access is
 * permitted if the access modifier is changed from package access to private
 * access; from protected access to package or private access; or from public
 * access to protected, package, or private access. Changing a member or
 * constructor to permit less access is therefore not recommended for widely
 * distributed classes.</blockquote>
 *
 * @see CChangeClassMethodRemoved
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassMethodBecameLessAccessibleType
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
        < this.methodPrevious().accessibility().accessibility(),
      m -> "Accessibility must have been reduced");

    Preconditions.checkPrecondition(
      Tuple.of(this.method(), this.methodPrevious()),
      Objects.equals(this.method().name(), this.methodPrevious().name()),
      p -> "Method names must match");

    Preconditions.checkPrecondition(
      Tuple.of(this.method(), this.methodPrevious()),
      Objects.equals(
        this.method().className(),
        this.methodPrevious().className()),
      p -> "Method class names must match");
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
