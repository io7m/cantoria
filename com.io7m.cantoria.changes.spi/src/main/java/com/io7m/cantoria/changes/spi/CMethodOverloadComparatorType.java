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
import com.io7m.cantoria.api.CMethod;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jnull.NullCheck;

import java.util.Objects;

/**
 * A comparison of a specific method overload.
 */

public interface CMethodOverloadComparatorType extends CChangeCheckType
{
  /**
   * Compare a specific method overload.
   *
   * @param receiver   A change receiver
   * @param registry   A class registry
   * @param class_old  The old version of the class
   * @param method_old The old version of the method
   * @param class_new  The new version of the class
   * @param method_new The new version of the method
   */

  void compareMethodOverload(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass class_old,
    CMethod method_old,
    CClass class_new,
    CMethod method_new);

  /**
   * Compare a specific method overload with extra precondition checks.
   *
   * @param receiver   A change receiver
   * @param registry   A class registry
   * @param class_old  The old version of the class
   * @param method_old The old version of the method
   * @param class_new  The new version of the class
   * @param method_new The new version of the method
   */

  default void compareMethodOverloadChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CMethod method_old,
    final CClass class_new,
    final CMethod method_new)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(class_old, "Class (old)");
    NullCheck.notNull(method_old, "Method (old)");
    NullCheck.notNull(class_new, "Class (new)");
    NullCheck.notNull(method_new, "Method (new)");

    Preconditions.checkPrecondition(
      class_new.node().name,
      s -> Objects.equals(s, class_old.node().name),
      s -> "Class name " + class_old.node().name + " must match " + class_new.node().name);

    Preconditions.checkPrecondition(
      method_old.name(),
      s -> Objects.equals(s, method_old.name()),
      s -> "Method name " + method_old.name() + " must match " + method_new.name());

    this.compareMethodOverload(
      receiver, registry, class_old, method_old, class_new, method_new);
  }
}
