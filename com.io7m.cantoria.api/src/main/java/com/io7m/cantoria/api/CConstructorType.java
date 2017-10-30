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

package com.io7m.cantoria.api;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * The type of constructors.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CConstructorType
{
  /**
   * @return The name of the class
   */

  default CClassName className()
  {
    return this.method().className();
  }

  /**
   * @return The underlying method
   */

  @Value.Parameter
  CMethod method();

  /**
   * @return The list of parameter types
   */

  default List<String> parameterTypes()
  {
    return this.method().parameterTypes();
  }

  /**
   * @return The list of declared exceptions
   */

  default List<String> exceptions()
  {
    return this.method().exceptions();
  }

  /**
   * @return The constructor accessibility
   */

  default CAccessibility accessibility()
  {
    return this.method().accessibility();
  }

  /**
   * @return The constructor modifiers
   */

  default Set<CModifier> modifiers()
  {
    return this.method().modifiers();
  }

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    if (!this.method().isInstanceConstructor()) {
      throw new IllegalArgumentException(
        "Underlying method must be an instance constructor");
    }
  }
}
