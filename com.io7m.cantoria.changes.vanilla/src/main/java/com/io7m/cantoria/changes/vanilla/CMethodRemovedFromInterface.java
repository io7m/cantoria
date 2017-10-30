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
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodCheckRemovalType;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodAbstractRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodDefaultRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodStaticRemoved;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * JLS 9 §13.5.3
 *
 * <blockquote>Deleting a member from an interface may cause linkage errors in
 * pre-existing binaries.</blockquote>
 */

public final class CMethodRemovedFromInterface
  implements CMethodCheckRemovalType
{
  /**
   * Construct a check.
   */

  public CMethodRemovedFromInterface()
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
    return List.of("JLS 9 §13.5.3");
  }

  @Override
  public String description()
  {
    return "Check interface method removals";
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
    if (!CClassModifiers.classIsInterface(class_new.node())) {
      return;
    }

    if (method.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    final boolean is_abstract =
      method.modifiers().contains(CModifier.ABSTRACT);
    final boolean was_interface =
      CClassModifiers.classIsInterface(class_old.node());

    if (is_abstract) {
      if (!was_interface) {
        return;
      }

      receiver.onChange(this, CChangeInterfaceMethodAbstractRemoved.of(method));
      return;
    }

    final boolean is_static = method.modifiers().contains(CModifier.STATIC);
    if (is_static) {
      if (!was_interface) {
        return;
      }

      receiver.onChange(this, CChangeInterfaceMethodStaticRemoved.of(method));
      return;
    }

    if (!was_interface) {
      return;
    }

    /*
     * Methods are either abstract, static, or neither. There are no
     * other alternatives in Java 9.
     */

    receiver.onChange(this, CChangeInterfaceMethodDefaultRemoved.of(method));
  }
}
