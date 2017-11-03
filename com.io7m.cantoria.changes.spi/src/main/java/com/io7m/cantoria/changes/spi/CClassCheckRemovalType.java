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

import java.util.Objects;

/**
 * A check performed when a class is removed from a module.
 */

public interface CClassCheckRemovalType extends CChangeCheckType
{
  /**
   * Check the removal of a class.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was removed
   */

  void checkClassRemoval(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass clazz);

  /**
   * Check the removal of a class, with extra preconditions.
   *
   * @param receiver A change receiver
   * @param registry A class registry
   * @param clazz    The class that was removed
   */

  default void checkClassRemovalChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(registry, "Registry");
    Objects.requireNonNull(clazz, "Class");

    this.checkClassRemoval(receiver, registry, clazz);
  }
}
