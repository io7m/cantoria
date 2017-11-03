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
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameAbstract;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonAbstract;
import io.vavr.collection.List;

/**
 * Determine if a class has started or stopped being abstract.
 *
 * JLS 9 §13.4.1
 *
 * @see CChangeClassBecameAbstract
 * @see CChangeClassBecameNonAbstract
 */

public final class CClassChangedAbstractness
  extends LocalizedCheck implements CClassComparatorType
{
  /**
   * Construct a comparator
   */

  public CClassChangedAbstractness()
  {
    super(CClassChangedAbstractness.class.getCanonicalName());
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.1");
  }

  @Override
  public void compareClass(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new)
  {
    /*
     * Ignore the abstractness of non-public classes; classes outside of the
     * package can only subclass public classes and therefore only public
     * classes are interesting from an API perspective.
     */

    if (class_new.accessibility() != CAccessibility.PUBLIC) {
      return;
    }

    final boolean was_abstract =
      CClassModifiers.classIsAbstract(class_old.node());
    final boolean is_abstract =
      CClassModifiers.classIsAbstract(class_new.node());

    if (!was_abstract && is_abstract) {
      receiver.onChange(
        this,
        CChangeClassBecameAbstract.builder()
          .setClassPrevious(class_old)
          .setClassValue(class_new)
          .build());
    }

    if (was_abstract && !is_abstract) {
      receiver.onChange(
        this,
        CChangeClassBecameNonAbstract.builder()
          .setClassPrevious(class_old)
          .setClassValue(class_new)
          .build());
    }
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }


}
