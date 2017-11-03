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
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CMethods;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodCheckAdditionType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverrideBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverrideChangedStatic;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Check to see if the addition of a method would cause a linkage error.
 *
 * JLS 9 §13.4.12
 *
 * @see CChangeClassMethodOverrideChangedStatic
 * @see CChangeClassMethodOverrideBecameLessAccessible
 */

public final class CMethodAddedCheckStaticLinkage
  extends LocalizedCheck implements CMethodCheckAdditionType
{
  /**
   * Construct a check.
   */

  public CMethodAddedCheckStaticLinkage()
  {
    super(CMethodAddedCheckStaticLinkage.class.getCanonicalName());
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.12");
  }

  @Override
  public void checkMethodAddition(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    if (method.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    try {
      final List<Tuple2<CClass, MethodNode>> r =
        CMethods.findSuperclassMethodsWithNameAndType(
          registry, class_new, method.name(), method.node().desc);

      if (!r.isEmpty()) {
        final CMethod super_method =
          CMethods.method(r.last()._1.name(), r.last()._2);

        /*
         * An instance method cannot override a static method, and vice versa.
         */

        final boolean current_static =
          method.modifiers().contains(CModifier.STATIC);
        final boolean ancestor_static =
          super_method.modifiers().contains(CModifier.STATIC);

        final CAccessibility current_access = method.accessibility();
        if (current_static != ancestor_static) {
          receiver.onChange(
            this,
            CChangeClassMethodOverrideChangedStatic.builder()
              .setMethod(method)
              .setMethodAncestor(super_method)
              .build());
        }

        /*
         * An overriding method cannot reduce the accessibility of a superclass method.
         */

        final CAccessibility ancestor_access = super_method.accessibility();
        if (current_access.accessibility() < ancestor_access.accessibility()) {
          receiver.onChange(
            this,
            CChangeClassMethodOverrideBecameLessAccessible.builder()
              .setMethod(method)
              .setMethodAncestor(super_method)
              .build());
        }
      }

    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
