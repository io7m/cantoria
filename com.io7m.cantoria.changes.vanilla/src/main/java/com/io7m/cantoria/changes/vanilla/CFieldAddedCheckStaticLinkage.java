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
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CFields;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CFieldCheckAdditionType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldOverrideBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldOverrideChangedStatic;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Check to see if the addition of a field would cause a linkage error.
 *
 * JLS 9 §13.4.8
 *
 * @see CChangeClassFieldOverrideChangedStatic
 * @see CChangeClassFieldOverrideBecameLessAccessible
 */

public final class CFieldAddedCheckStaticLinkage
  extends LocalizedCheck implements CFieldCheckAdditionType
{
  /**
   * Construct a check.
   */

  public CFieldAddedCheckStaticLinkage()
  {
    super(CFieldAddedCheckStaticLinkage.class.getCanonicalName());
  }

  @Override
  public void checkFieldAddition(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz,
    final CField field)
  {
    if (field.accessibility() != CAccessibility.PRIVATE) {
      try {
        final List<Tuple2<CClass, FieldNode>> r =
          CFields.findSuperclassFieldsWithName(registry, clazz, field.name());

        if (!r.isEmpty()) {
          final CField super_field =
            CFields.field(r.last()._1.name(), r.last()._2);

          /*
           * An instance field cannot override a static field, and vice versa.
           */

          final boolean static_curr =
            field.modifiers().contains(CModifier.STATIC);
          final boolean static_super =
            super_field.modifiers().contains(CModifier.STATIC);
          final CAccessibility access_curr =
            field.accessibility();

          if (static_curr != static_super) {
            receiver.onChange(
              this,
              CChangeClassFieldOverrideChangedStatic.builder()
                .setField(field)
                .setFieldAncestor(super_field)
                .build());
          }

          /*
           * An overriding field cannot reduce the accessibility of a superclass field.
           */

          final CAccessibility access_super = super_field.accessibility();
          if (access_curr.accessibility() < access_super.accessibility()) {
            receiver.onChange(
              this,
              CChangeClassFieldOverrideBecameLessAccessible.builder()
                .setField(CFields.field(clazz.name(), field.node()))
                .setFieldAncestor(super_field)
                .build());
          }
        }

      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
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
}
