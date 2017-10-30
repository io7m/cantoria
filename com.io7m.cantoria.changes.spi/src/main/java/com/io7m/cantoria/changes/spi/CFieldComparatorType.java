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
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jnull.NullCheck;

import java.util.Objects;

/**
 * A comparator for a field.
 */

public interface CFieldComparatorType extends CChangeCheckType
{
  /**
   * Compare the given fields.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param clazz_old The old version of the class
   * @param field_old The old version of the field
   * @param clazz_new The new version of the class
   * @param field_new The new version of the field
   */

  void compareField(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CClass clazz_old,
    CField field_old,
    CClass clazz_new,
    CField field_new);

  /**
   * Compare the given fields, with extra precondition checks.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param clazz_old The old version of the class
   * @param field_old The old version of the field
   * @param clazz_new The new version of the class
   * @param field_new The new version of the field
   */

  default void compareFieldChecked(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz_old,
    final CField field_old,
    final CClass clazz_new,
    final CField field_new)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(clazz_old, "Class (old)");
    NullCheck.notNull(field_old, "Field (old)");
    NullCheck.notNull(clazz_new, "Class (new)");
    NullCheck.notNull(field_new, "Field (new)");

    Preconditions.checkPrecondition(
      clazz_new.node().name,
      s -> Objects.equals(s, clazz_old.node().name),
      s -> "Class name " + clazz_old.node().name + " must match " + clazz_new.node().name);

    Preconditions.checkPrecondition(
      field_old.name(),
      s -> Objects.equals(s, field_old.name()),
      s -> "Field name " + field_old.name() + " must match " + field_new.name());

    this.compareField(
      receiver,
      registry,
      clazz_old,
      field_old,
      clazz_new,
      field_new);
  }
}
