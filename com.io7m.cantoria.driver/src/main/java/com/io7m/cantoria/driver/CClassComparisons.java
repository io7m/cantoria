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

package com.io7m.cantoria.driver;

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CEnum;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.spi.CEnumComparatorType;
import com.io7m.jaffirm.core.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Functions to compare classes.
 */

public final class CClassComparisons
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CClassComparisons.class);

  private final ServiceLoader<CClassComparatorType> class_comparators;
  private final CFieldComparisons field_comparisons;
  private final CMethodComparisons method_comparisons;
  private final ServiceLoader<CEnumComparatorType> enum_comparators;

  private CClassComparisons()
  {
    this.class_comparators = ServiceLoader.load(CClassComparatorType.class);
    this.enum_comparators = ServiceLoader.load(CEnumComparatorType.class);
    this.field_comparisons = CFieldComparisons.create();
    this.method_comparisons = CMethodComparisons.create();
  }

  /**
   * @return A class comparison driver
   */

  public static CClassComparisons create()
  {
    return new CClassComparisons();
  }

  /**
   * Compare the given classes, delivering changes to the receiver.
   *
   * @param receiver  The change receiver
   * @param registry  A class registry
   * @param class_old The old class
   * @param class_new The new class
   */

  public void compareClasses(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(registry, "Registry");
    Objects.requireNonNull(class_old, "Class (old)");
    Objects.requireNonNull(class_new, "Class (new)");

    Preconditions.checkPrecondition(
      class_new.name(),
      s -> Objects.equals(s, class_old.name()),
      s -> "Class name " + class_old.name() + " must match " + class_new.name());

    this.class_comparators.forEach(
      compare -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}",
            compare.name(),
            CClassNames.show(class_new.name()));
        }
        compare.compareClassChecked(receiver, registry, class_old, class_new);
      });

    this.method_comparisons.compareAllMethods(
      receiver, registry, class_old, class_new);
    this.field_comparisons.compareAllFields(
      receiver, registry, class_old, class_new);
  }

  /**
   * Compare the given enums, delivering changes to the receiver.
   *
   * @param receiver The change receiver
   * @param registry A class registry
   * @param enum_old The old class
   * @param enum_new The new class
   */

  public void compareEnums(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CEnum enum_old,
    final CEnum enum_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(registry, "Registry");
    Objects.requireNonNull(enum_old, "Class (old)");
    Objects.requireNonNull(enum_new, "Class (new)");

    Preconditions.checkPrecondition(
      enum_new.name(),
      s -> Objects.equals(s, enum_old.name()),
      s -> "Class name " + enum_old.name() + " must match " + enum_new.name());

    this.enum_comparators.forEach(
      compare -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}",
            compare.name(),
            CClassNames.show(enum_new.name()));
        }
        compare.compareEnumChecked(receiver, registry, enum_old, enum_new);
      });

    this.compareClasses(
      receiver, registry, enum_old.classValue(), enum_new.classValue());
  }
}
