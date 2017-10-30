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

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameInterface;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonInterface;
import io.vavr.collection.List;

/**
 * Determine if a class has started or stopped being an interface.
 *
 * @see CChangeClassBecameInterface
 * @see CChangeClassBecameNonInterface
 */

public final class CClassChangedInterfaceModifier implements
  CClassComparatorType
{
  /**
   * Construct a comparator
   */

  public CClassChangedInterfaceModifier()
  {

  }

  @Override
  public List<String> jlsReferences()
  {
    return List.empty();
  }

  @Override
  public String description()
  {
    return "Determine if a class has started or stopped being an interface";
  }

  @Override
  public void compareClass(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz_old,
    final CClass clazz_new)
  {
    final boolean was_interface =
      CClassModifiers.classIsInterface(clazz_old.node());
    final boolean is_interface =
      CClassModifiers.classIsInterface(clazz_new.node());

    if (!was_interface && is_interface) {
      receiver.onChange(
        this,
        CChangeClassBecameInterface.of(
          clazz_new.name(),
          CClassModifiers.classModifiers(clazz_new.node())));
    }

    if (was_interface && !is_interface) {
      receiver.onChange(
        this,
        CChangeClassBecameNonInterface.of(
          clazz_new.name(),
          CClassModifiers.classModifiers(clazz_new.node())));
    }
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}
