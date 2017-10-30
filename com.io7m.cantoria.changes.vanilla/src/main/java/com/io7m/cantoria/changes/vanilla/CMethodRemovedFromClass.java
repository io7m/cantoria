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
import com.io7m.cantoria.api.CConstructors;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CMethods;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodCheckRemovalType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassConstructorRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodMovedToSuperclass;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodRemoved;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * JLS 9 §13.4.12
 *
 * <blockquote>Deleting a method or constructor from a class may break
 * compatibility with any pre-existing binary that referenced this method or
 * constructor; a NoSuchMethodError may be thrown when such a reference from a
 * pre-existing binary is linked. Such an error will occur only if no method
 * with a matching signature and return type is declared in a
 * superclass.</blockquote>
 */

public final class CMethodRemovedFromClass implements CMethodCheckRemovalType
{
  /**
   * Construct a check.
   */

  public CMethodRemovedFromClass()
  {

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
  public String description()
  {
    return "Check method removals";
  }

  @Override
  public void checkMethodRemoval(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    /*
     * Ignore private methods and interface classes.
     */

    if (CClassModifiers.classIsInterface(class_new.node())) {
      return;
    }

    if (method.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    if (!overloads.isEmpty()) {
      if (method.isInstanceConstructor()) {
        receiver.onChange(
          this,
          CChangeClassConstructorRemoved.of(CConstructors.constructor(method)));
        return;
      }

      /*
       * The method may actually have moved to a superclass. If that happens,
       * the removal of the method is binary compatible.
       */

      final List<Tuple2<CClass, MethodNode>> supers;

      try {
        supers = CMethods.findSuperclassMethodsWithNameAndType(
          registry, class_new, method.name(), method.node().desc);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }

      if (supers.isEmpty()) {
        receiver.onChange(this, CChangeClassMethodOverloadRemoved.of(method));
        return;
      }

      final CMethod super_method =
        CMethods.method(supers.last()._1.name(), supers.last()._2);

      receiver.onChange(
        this,
        CChangeClassMethodMovedToSuperclass.builder()
          .setMethodAncestor(super_method)
          .setMethod(method)
          .build());
      return;
    }

    if (method.isInstanceConstructor()) {
      receiver.onChange(
        this,
        CChangeClassConstructorRemoved.of(CConstructors.constructor(method)));
      return;
    }

    receiver.onChange(this, CChangeClassMethodRemoved.of(method));
  }
}
