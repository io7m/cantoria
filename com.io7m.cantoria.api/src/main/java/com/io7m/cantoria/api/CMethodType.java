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
import org.objectweb.asm.tree.MethodNode;

import java.util.Objects;

/**
 * The type of methods.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CMethodType
{
  /**
   * Obtain the node used to construct the method. This value should be
   * considered strictly informative; {@link MethodNode} values are mutable and
   * therefore code should not depend on the data contained within the node in
   * any form. Use the other methods defined on the {@link CMethodType} to
   * perform checks instead.
   *
   * @return The node that was used to construct the method value
   */

  @Value.Parameter
  @Value.Auxiliary
  MethodNode node();

  /**
   * @return The name of the class
   */

  @Value.Parameter
  CClassName className();

  /**
   * @return The name of the method
   */

  @Value.Parameter
  String name();

  /**
   * @return The list of parameter types
   */

  @Value.Parameter
  List<String> parameterTypes();

  /**
   * @return The return type
   */

  @Value.Parameter
  String returnType();

  /**
   * @return The list of declared exceptions
   */

  @Value.Parameter
  List<String> exceptions();

  /**
   * @return The method accessibility
   */

  @Value.Parameter
  CAccessibility accessibility();

  /**
   * @return The method modifiers
   */

  @Value.Parameter
  Set<CModifier> modifiers();

  /**
   * @return {@code true} iff the method is a static initializer
   */

  default boolean isStaticInitializer()
  {
    return Objects.equals(this.name(), "<clinit>");
  }

  /**
   * @return {@code true} iff the method is an instance constructor
   */

  default boolean isInstanceConstructor()
  {
    return Objects.equals(this.name(), "<init>");
  }

  /**
   * @return {@code true} iff the method is variadic (varargs)
   */

  default boolean isVariadic()
  {
    return this.modifiers().contains(CModifier.VARARGS);
  }
}
