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

import io.vavr.collection.Set;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import org.objectweb.asm.tree.FieldNode;

/**
 * The type of fields.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CFieldType
{
  /**
   * @return The node that was used to construct the field value
   */

  @Value.Parameter
  @Value.Auxiliary
  FieldNode node();

  /**
   * @return The name of the class
   */

  @Value.Parameter
  CClassName className();

  /**
   * @return The name of the field
   */

  @Value.Parameter
  String name();

  /**
   * @return The field type
   */

  @Value.Parameter
  String type();

  /**
   * @return The field accessibility
   */

  @Value.Parameter
  CAccessibility accessibility();

  /**
   * @return The field modifiers
   */

  @Value.Parameter
  Set<CModifier> modifiers();
}
