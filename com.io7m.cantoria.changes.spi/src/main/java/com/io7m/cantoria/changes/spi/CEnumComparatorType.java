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

import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CEnum;
import com.io7m.jaffirm.core.Preconditions;

import java.util.Objects;

/**
 * An enum comparison function.
 */

public interface CEnumComparatorType extends CChangeCheckType
{
  /**
   * Compare the given enums.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param clazz_old The old version of the class
   * @param clazz_new The new version of the class
   */

  void compareEnum(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CEnum clazz_old,
    CEnum clazz_new);

  /**
   * Compare the given enums, with extra precondition checks.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param clazz_old The old version of the class
   * @param clazz_new The new version of the class
   */

  default void compareEnumChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CEnum clazz_old,
    final CEnum clazz_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(registry, "Registry");
    Objects.requireNonNull(clazz_old, "Class (old)");
    Objects.requireNonNull(clazz_new, "Class (new)");

    Preconditions.checkPrecondition(
      clazz_new.node().name,
      s -> Objects.equals(s, clazz_old.node().name),
      s -> "Class name " + clazz_old.node().name + " must match " + clazz_new.node().name);

    this.compareEnum(receiver, registry, clazz_old, clazz_new);
  }
}
