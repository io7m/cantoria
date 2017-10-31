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

import com.io7m.jaffirm.core.Preconditions;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import org.objectweb.asm.tree.ClassNode;

/**
 * A parsed enum.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CEnumType extends CClassValuesType
{
  /**
   * @return The underlying class
   */

  @Value.Parameter
  CClass classValue();

  @Override
  default CClassName name()
  {
    return this.classValue().name();
  }

  @Override
  default ClassNode node()
  {
    return this.classValue().node();
  }

  @Override
  default CModuleType module()
  {
    return this.classValue().module();
  }

  @Override
  default Set<CModifier> modifiers()
  {
    return this.classValue().modifiers();
  }

  @Override
  default CAccessibility accessibility()
  {
    return this.classValue().accessibility();
  }

  @Override
  default int bytecodeVersion()
  {
    return this.classValue().bytecodeVersion();
  }

  /**
   * @return The enum members
   */

  @Value.Parameter
  Map<String, CEnumMember> members();

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    Preconditions.checkPrecondition(
      this.classValue(),
      this.classValue().isEnum(),
      c -> "Underlying class must be an enum");
  }
}
