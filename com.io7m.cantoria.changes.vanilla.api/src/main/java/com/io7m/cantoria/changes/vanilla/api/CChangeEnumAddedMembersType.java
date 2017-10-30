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

import com.io7m.cantoria.api.CEnum;
import com.io7m.cantoria.api.CEnumMember;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.changes.spi.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.spi.CChangeEnumType;
import com.io7m.cantoria.changes.spi.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.spi.CChangeSourceCompatibility;
import com.io7m.jaffirm.core.Preconditions;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Objects;

/**
 * JLS 9, §13.4.26:
 *
 * <blockquote>Adding or reordering constants in an enum will not break
 * compatibility with pre-existing binaries.</blockquote>
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeEnumAddedMembersType
  extends CChangeEnumType
{
  @Override
  @Value.Parameter
  CEnum enumType();

  /**
   * @return The previous state of the enum
   */

  @Value.Parameter
  CEnum enumPrevious();

  /**
   * @return The set of added members
   */

  @Value.Derived
  default Set<String> addedMembers()
  {
    return this.enumType().members().removeAll(
      this.enumPrevious().members().keySet()).keySet();
  }

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final Map<String, CEnumMember> members_now = this.enumType().members();
    final Map<String, CEnumMember> members_then = this.enumPrevious().members();

    members_then.keySet().forEach(key -> {
      if (!members_now.containsKey(key)) {
        throw new IllegalArgumentException(
          "Previous enum members must be a proper subset of the current members");
      }
    });

    Preconditions.checkPrecondition(
      Objects.equals(this.enumType().name(), this.enumPrevious().name()),
      "Class names must match");
  }

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_MINOR;
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
