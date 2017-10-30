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
import io.vavr.collection.Map;

import java.util.Objects;

/**
 * A check performed when a method is added to a class.
 */

public interface CMethodCheckAdditionType extends CChangeCheckType
{
  /**
   * Check the addition of a method.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param class_old The old version of the class
   * @param class_new The new version of the class
   * @param method    The method that was added
   * @param overloads The existing method overloads, not including the current
   *                  method
   */

  void checkMethodAddition(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass class_old,
    CClass class_new,
    CMethod method,
    Map<String, CMethod> overloads);

  /**
   * Check the addition of a method, with extra preconditions.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param class_old The old version of the class
   * @param class_new The new version of the class
   * @param method    The method that was added
   * @param overloads The existing method overloads, not including the current
   *                  method
   */

  default void checkMethodAdditionChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(class_old, "Class (old)");
    NullCheck.notNull(class_new, "Class (new)");
    NullCheck.notNull(method, "Method");
    NullCheck.notNull(overloads, "Overloads");

    Preconditions.checkPrecondition(
      overloads.filter(
        p -> Objects.equals(
          p._2.name(),
          method.name())).size() == overloads.size(),
      "Overloads must all be named " + method.name());

    this.checkMethodAddition(
      receiver, registry, class_old, class_new, method, overloads);
  }
}
