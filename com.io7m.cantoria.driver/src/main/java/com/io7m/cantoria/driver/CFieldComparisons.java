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
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CFields;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CFieldCheckAdditionType;
import com.io7m.cantoria.changes.spi.CFieldCheckRemovalType;
import com.io7m.cantoria.changes.spi.CFieldComparatorType;
import com.io7m.jaffirm.core.Preconditions;
import io.vavr.collection.List;
import io.vavr.collection.SortedMap;
import io.vavr.collection.SortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Functions to compare fields.
 */

public final class CFieldComparisons
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CFieldComparisons.class);

  private final ServiceLoader<CFieldCheckRemovalType> field_removals;
  private final ServiceLoader<CFieldComparatorType> field_comparators;
  private final ServiceLoader<CFieldCheckAdditionType> field_additions;

  private CFieldComparisons()
  {
    this.field_additions = ServiceLoader.load(CFieldCheckAdditionType.class);
    this.field_removals = ServiceLoader.load(CFieldCheckRemovalType.class);
    this.field_comparators = ServiceLoader.load(CFieldComparatorType.class);
  }

  /**
   * @return A field comparison driver
   */

  public static CFieldComparisons create()
  {
    return new CFieldComparisons();
  }

  /**
   * Compare the given fields.
   *
   * @param receiver  A change receiver
   * @param registry  A class registry
   * @param c_old     The old class
   * @param field_old The old field
   * @param c_new     The new class
   * @param field_new The new field
   */

  public void compareField(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass c_old,
    final CField field_old,
    final CClass c_new,
    final CField field_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(c_old, "Class (old)");
    Objects.requireNonNull(field_old, "Field (old)");
    Objects.requireNonNull(c_new, "Class (new)");
    Objects.requireNonNull(field_new, "Field (new)");

    Preconditions.checkPrecondition(
      c_new.node().name,
      s -> Objects.equals(s, c_old.node().name),
      s -> "Class name " + c_old.node().name + " must match " + c_new.node().name);

    Preconditions.checkPrecondition(
      field_old.name(),
      s -> Objects.equals(s, field_old.name()),
      s -> "Field name " + field_old.name() + " must match " + field_new.name());

    this.onFieldCompare(receiver, registry, c_old, field_old, c_new, field_new);
  }

  /**
   * Compare all fields of the given classes.
   *
   * @param receiver The change receiver
   * @param registry The class registry
   * @param c_old    The old class
   * @param c_new    The new class
   */

  public void compareAllFields(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass c_old,
    final CClass c_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(c_old, "Class (old)");
    Objects.requireNonNull(c_new, "Class (new)");

    Preconditions.checkPrecondition(
      c_new.node().name,
      s -> Objects.equals(s, c_old.node().name),
      s -> "Class name " + c_old.node().name + " must match " + c_new.node().name);

    final SortedMap<String, CField> fields_old =
      List.ofAll(c_old.node().fields)
        .toSortedMap(
          n -> n.name,
          n -> CFields.field(c_old.name(), n));

    final SortedMap<String, CField> fields_new =
      List.ofAll(c_new.node().fields)
        .toSortedMap(
          n -> n.name,
          n -> CFields.field(c_new.name(), n));

    this.onFieldsAdditionsAndRemovals(
      receiver, registry, c_new, fields_old, fields_new);

    final SortedSet<String> fields_both =
      fields_new.keySet().intersect(fields_old.keySet());

    fields_both.forEach(name -> {
      final CField field_old = fields_old.get(name).get();
      final CField field_new = fields_new.get(name).get();
      this.onFieldCompare(
        receiver, registry, c_old, field_old, c_new, field_new);
    });
  }

  private void onFieldCompare(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz_old,
    final CField field_old,
    final CClass clazz_new,
    final CField field_new)
  {
    this.field_comparators.forEach(
      ch -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            ch.name(),
            CClassNames.show(clazz_new.name()),
            field_new.name());
        }
        ch.compareFieldChecked(
          receiver, registry, clazz_old, field_old, clazz_new, field_new);
      });
  }

  private void onFieldsAdditionsAndRemovals(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass c_new,
    final SortedMap<String, CField> fields_old,
    final SortedMap<String, CField> fields_new)
  {
    final SortedMap<String, CField> fields_removed =
      fields_old.removeAll(fields_new.keySet());

    fields_removed.forEach(
      p -> this.onFieldRemoved(receiver, registry, c_new, p._2));

    final SortedMap<String, CField> fields_added =
      fields_new.removeAll(fields_old.keySet());

    fields_added.forEach(
      p -> this.onFieldAdded(receiver, registry, c_new, p._2));
  }

  private void onFieldAdded(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz,
    final CField field)
  {
    this.field_additions.forEach(
      ch -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            ch.name(),
            CClassNames.show(clazz.name()),
            field.name());
        }
        ch.checkFieldAdditionChecked(receiver, registry, clazz, field);
      });
  }

  private void onFieldRemoved(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz,
    final CField field)
  {
    this.field_removals.forEach(
      ch -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            ch.name(),
            CClassNames.show(clazz.name()),
            field.name());
        }
        ch.checkFieldRemovalChecked(receiver, registry, clazz, field);
      });
  }

}
