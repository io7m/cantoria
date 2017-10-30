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

package com.io7m.cantoria.changes.vanilla;

import com.io7m.cantoria.api.CAccessibility;
import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CFieldModifiers;
import com.io7m.cantoria.api.CFields;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CFieldCheckRemovalType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldMovedToSuperclass;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldRemovedPublic;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Check if a removed field has actually been moved to a superclass.
 *
 * JLS 9 §13.4.8
 */

public final class CFieldCheckRemovedOrMoved implements CFieldCheckRemovalType
{
  /**
   * Construct a check
   */

  public CFieldCheckRemovedOrMoved()
  {

  }

  @Override
  public void checkFieldRemoval(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz,
    final CField field)
  {
    if (field.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    /*
     * Ignore enum members.
     */

    if (CClassModifiers.classIsEnum(clazz.node())
      && CFieldModifiers.fieldIsEnumMember(field.node())) {
      return;
    }

    try {
      final List<Tuple2<CClass, FieldNode>> r =
        CFields.findSuperclassFieldsWithNameAndType(
          registry, clazz, field.name(), field.node().desc);

      if (!r.isEmpty()) {
        receiver.onChange(
          this,
          CChangeClassFieldMovedToSuperclass.builder()
            .setFieldAncestor(CFields.field(r.last()._1.name(), r.last()._2))
            .setField(field)
            .build());
      } else {
        receiver.onChange(
          this,
          CChangeClassFieldRemovedPublic.of(field));
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.8");
  }

  @Override
  public String description()
  {
    return "Check if a field has been removed or merely moved to a superclass";
  }
}
