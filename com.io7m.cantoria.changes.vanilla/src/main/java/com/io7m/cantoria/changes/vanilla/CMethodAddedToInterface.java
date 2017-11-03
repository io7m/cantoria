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
import com.io7m.cantoria.changes.spi.CMethodCheckAdditionType;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodAbstractAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodAbstractAddedType;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodDefaultAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodStaticAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodStaticAddedType;
import com.io7m.jaffirm.core.Invariants;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * JLS 9 §13.5.3
 *
 * <blockquote> Adding an abstract, private, or static method to an interface
 * does not break compatibility with pre-existing binaries.</blockquote>
 *
 * Adding an abstract method to an interface is a source-incompatible change
 * because any implementors of the interface will need to implement the new
 * method and be recompiled.
 *
 * @see CChangeInterfaceMethodAbstractAddedType
 * @see CChangeInterfaceMethodStaticAddedType
 */

public final class CMethodAddedToInterface
  extends LocalizedCheck implements CMethodCheckAdditionType
{
  /**
   * Construct a method check.
   */

  public CMethodAddedToInterface()
  {
    super(CMethodAddedToInterface.class.getCanonicalName());
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
  public void checkMethodAddition(
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

    if (method.modifiers().contains(CModifier.ABSTRACT)) {
      receiver.onChange(this, CChangeInterfaceMethodAbstractAdded.of(method));
      return;
    }

    if (method.modifiers().contains(CModifier.STATIC)) {
      receiver.onChange(this, CChangeInterfaceMethodStaticAdded.of(method));
      return;
    }

    /*
     * Methods are either abstract, static, or neither. There are no
     * other alternatives in Java 9.
     */

    Invariants.checkInvariant(
      !method.modifiers().contains(CModifier.STATIC),
      "Method is not static");
    Invariants.checkInvariant(
      !method.modifiers().contains(CModifier.ABSTRACT),
      "Method is not abstract");

    receiver.onChange(this, CChangeInterfaceMethodDefaultAdded.of(method));
  }
}
