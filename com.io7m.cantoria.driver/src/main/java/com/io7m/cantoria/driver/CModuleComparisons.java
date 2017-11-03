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
import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CEnum;
import com.io7m.cantoria.api.CEnums;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassCheckAdditionType;
import com.io7m.cantoria.changes.spi.CClassCheckRemovalType;
import com.io7m.jaffirm.core.Invariants;
import io.vavr.collection.SortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Functions to compare modules.
 */

public final class CModuleComparisons
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CModuleComparisons.class);

  private final CModuleDescriptorComparisons module_desc_comparisons;
  private final ServiceLoader<CClassCheckRemovalType> class_removals;
  private final ServiceLoader<CClassCheckAdditionType> class_additions;
  private final CClassComparisons class_comparisons;

  private CModuleComparisons()
  {
    this.class_removals =
      ServiceLoader.load(CClassCheckRemovalType.class);
    this.class_additions =
      ServiceLoader.load(CClassCheckAdditionType.class);
    this.module_desc_comparisons =
      CModuleDescriptorComparisons.create();
    this.class_comparisons =
      CClassComparisons.create();
  }

  /**
   * @return A module comparison driver
   */

  public static CModuleComparisons create()
  {
    return new CModuleComparisons();
  }

  /**
   * Compare the given modules, delivering changes to the receiver.
   *
   * @param receiver The change receiver
   * @param registry A class registry for finding external classes
   * @param c_old    The old module
   * @param c_new    The new module
   *
   * @throws IOException On I/O errors
   */

  public void compareModules(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CModuleType c_old,
    final CModuleType c_new)
    throws IOException
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(registry, "Registry");
    Objects.requireNonNull(c_old, "Module (old)");
    Objects.requireNonNull(c_new, "Module (new)");

    this.module_desc_comparisons.compareModuleDescriptors(
      receiver, c_old.descriptor(), c_new.descriptor());

    try {
      this.compareClassesExportedUnqualified(receiver, registry, c_old, c_new);
    } catch (final UncheckedIOException e) {
      throw e.getCause();
    }
  }

  /**
   * For each of the packages exported by both modules, compare all of the
   * public classes.
   */

  private void compareClassesExportedUnqualified(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CModuleType module_old,
    final CModuleType module_new)
  {
    final SortedSet<String> old_exports =
      module_old.descriptor().exportsUnqualified();
    final SortedSet<String> new_exports =
      module_new.descriptor().exportsUnqualified();
    final SortedSet<String> exported_both =
      old_exports.intersect(new_exports);

    exported_both.forEach(pack -> {
      final SortedSet<String> classes_old =
        module_old.classesInPackage(pack);
      final SortedSet<String> classes_new =
        module_new.classesInPackage(pack);

      final SortedSet<String> classes_added =
        classes_new.removeAll(classes_old);

      classes_added.forEach(
        added -> {
          try {
            module_new.classValue(pack, added).ifPresent(
              clazz -> this.onClassAdded(receiver, registry, clazz));
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }
        });

      final SortedSet<String> classes_removed =
        classes_old.removeAll(classes_new);

      classes_removed.forEach(
        removed -> {
          try {
            module_old.classValue(pack, removed).ifPresent(
              clazz -> this.onClassRemoved(receiver, registry, clazz));
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }
        });

      final SortedSet<String> classes_both =
        classes_new.intersect(classes_old);

      classes_both.forEach(
        present -> this.compareClasses(
          receiver, registry, module_old, module_new, pack, present));
    });
  }

  private void onClassRemoved(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz)
  {
    this.class_removals.forEach(
      check -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}",
            check.name(),
            CClassNames.show(clazz.name()));
        }
        check.checkClassRemovalChecked(receiver, registry, clazz);
      });
  }

  private void onClassAdded(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz)
  {
    this.class_additions.forEach(
      check -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}",
            check.name(),
            CClassNames.show(clazz.name()));
        }
        check.checkClassAdditionChecked(receiver, registry, clazz);
      });
  }

  private void compareClasses(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CModuleType module_old,
    final CModuleType module_new,
    final String package_name,
    final String class_name)
  {
    try {
      final Optional<CClass> clazz_opt_old =
        module_old.classValue(package_name, class_name);
      final Optional<CClass> clazz_opt_new =
        module_new.classValue(package_name, class_name);

      Invariants.checkInvariant(
        clazz_opt_old.isPresent(), "Class must be present");
      Invariants.checkInvariant(
        clazz_opt_new.isPresent(), "Class must be present");

      final CClass class_node_old = clazz_opt_old.get();
      final CClass class_node_new = clazz_opt_new.get();

      /*
       * If the classes are enums, run the enum comparisons.
       */

      if (CClassModifiers.classIsEnum(class_node_old.node())
        && CClassModifiers.classIsEnum(class_node_new.node())) {
        final CEnum enum_old = CEnums.enumValue(class_node_old);
        final CEnum enum_new = CEnums.enumValue(class_node_new);
        this.class_comparisons.compareEnums(
          receiver, registry, enum_old, enum_new);
      }

      this.class_comparisons.compareClasses(
        receiver, registry, class_node_old, class_node_new);

    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
