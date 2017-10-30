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
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CFieldComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameMoreAccessible;
import io.vavr.collection.List;

/**
 * Compare the accessibility of fields.
 *
 * JLS 9 §13.4.7
 *
 * @see CChangeClassFieldBecameLessAccessible
 * @see CChangeClassFieldBecameMoreAccessible
 */

public final class CFieldChangedAccessibility implements CFieldComparatorType
{
  /**
   * Construct a comparator.
   */

  public CFieldChangedAccessibility()
  {

  }

  @Override
  public void compareField(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz_old,
    final CField field_old,
    final CClass clazz_new,
    final CField field_new)
  {
    final int new_access = field_new.accessibility().accessibility();
    final int old_access = field_old.accessibility().accessibility();

    if (new_access > old_access) {
      receiver.onChange(
        this,
        CChangeClassFieldBecameMoreAccessible.builder()
          .setFieldPrevious(field_old)
          .setField(field_new)
          .build());
    }

    if (new_access < old_access) {
      receiver.onChange(
        this,
        CChangeClassFieldBecameLessAccessible.builder()
          .setFieldPrevious(field_old)
          .setField(field_new)
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
    return List.of("JLS 9 §13.4.7");
  }

  @Override
  public String description()
  {
    return "Compares the accessibility of fields.";
  }
}
