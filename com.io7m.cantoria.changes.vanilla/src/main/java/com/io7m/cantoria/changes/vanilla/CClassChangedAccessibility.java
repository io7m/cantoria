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
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonPublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecamePublic;
import io.vavr.collection.List;

/**
 * Determine if a class has started or stopped being public.
 *
 * JLS 9 §13.4.3
 *
 * @see CChangeClassBecamePublic
 * @see CChangeClassBecameNonPublic
 */

public final class CClassChangedAccessibility
  extends LocalizedCheck implements CClassComparatorType
{
  /**
   * Construct a comparator
   */

  public CClassChangedAccessibility()
  {
    super(CClassChangedAccessibility.class.getCanonicalName());
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.3");
  }

  @Override
  public void compareClass(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new)
  {
    final boolean was_public =
      class_old.accessibility() == CAccessibility.PUBLIC;
    final boolean is_public =
      class_new.accessibility() == CAccessibility.PUBLIC;

    if (!was_public && is_public) {
      receiver.onChange(
        this,
        CChangeClassBecamePublic.builder()
          .setClassPrevious(class_old)
          .setClassValue(class_new)
          .build());
    }

    if (was_public && !is_public) {
      receiver.onChange(
        this,
        CChangeClassBecameNonPublic.builder()
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
