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
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodOverloadComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameMoreAccessible;
import io.vavr.collection.List;

/**
 * JLS 9 §13.4.12
 */

public final class CMethodChangedAccessibility
  extends LocalizedCheck implements CMethodOverloadComparatorType
{
  /**
   * Construct a comparator
   */

  public CMethodChangedAccessibility()
  {
    super(CMethodChangedAccessibility.class.getCanonicalName());
  }

  @Override
  public void compareMethodOverload(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CMethod method_old,
    final CClass class_new,
    final CMethod method_new)
  {
    final int new_access = method_new.accessibility().accessibility();
    final int old_access = method_old.accessibility().accessibility();

    if (new_access > old_access) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameMoreAccessible.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
    }

    if (new_access < old_access) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameLessAccessible.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
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
    return List.of("JLS 9 §13.4.12");
  }
}
