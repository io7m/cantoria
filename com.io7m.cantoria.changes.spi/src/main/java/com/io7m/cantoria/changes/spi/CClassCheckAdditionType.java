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
import com.io7m.jnull.NullCheck;

/**
 * A check performed when a class is added to a module.
 */

public interface CClassCheckAdditionType extends CChangeCheckType
{
  /**
   * Check the addition of a class.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was removed
   */

  void checkClassAddition(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass clazz);

  /**
   * Check the addition of a class, with extra preconditions.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was removed
   */

  default void checkClassAdditionChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(clazz, "Class");

    this.checkClassAddition(receiver, registry, clazz);
  }
}
