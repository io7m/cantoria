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

package com.io7m.cantoria.changes.spi;

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CField;
import com.io7m.jnull.NullCheck;

/**
 * A check performed when a field is removed from a class.
 */

public interface CFieldCheckRemovalType extends CChangeCheckType
{
  /**
   * Check the removal of a field.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was modified
   * @param field    The field that was removed
   */

  void checkFieldRemoval(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass clazz,
    CField field);

  /**
   * Check the removal of a field, with extra preconditions.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was modified
   * @param field    The field that was removed
   */

  default void checkFieldRemovalChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz,
    final CField field)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(clazz, "Class");
    NullCheck.notNull(field, "Field");

    this.checkFieldRemoval(receiver, registry, clazz, field);
  }
}
