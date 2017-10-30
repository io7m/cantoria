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

package com.io7m.cantoria.changes.vanilla;

import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.cantoria.api.CModuleQualifiedExport;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CModuleDescriptorComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerQualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerUnqualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageQualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageUnqualifiedExported;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;

/**
 * Determine if any packages have been newly exported, or have been removed.
 *
 * JLS 9 §13.3
 *
 * @see CChangeModulePackageNoLongerQualifiedExported
 * @see CChangeModulePackageNoLongerUnqualifiedExported
 * @see CChangeModulePackageQualifiedExported
 * @see CChangeModulePackageUnqualifiedExported
 */

public final class CModuleDescriptorCompareExports
  implements CModuleDescriptorComparatorType
{
  /**
   * Construct a comparator.
   */

  public CModuleDescriptorCompareExports()
  {

  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.3");
  }

  @Override
  public String description()
  {
    return "Determine if any packages have been newly exported, or have been removed";
  }

  @Override
  public void compareModule(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    final SortedSet<String> exports_unqualified_old =
      md_old.exportsUnqualified();
    final SortedSet<String> exports_unqualified_new =
      md_new.exportsUnqualified();

    /*
     * Check if any packages are no longer exported unqualified.
     */

    final SortedSet<String> exports_removed =
      exports_unqualified_old.removeAll(exports_unqualified_new);
    exports_removed.forEach(
      r -> receiver.onChange(
        this,
        CChangeModulePackageNoLongerUnqualifiedExported.of(md_new.name(), r)));

    /*
     * Check if any packages are now exported unqualified.
     */

    final SortedSet<String> exports_added =
      exports_unqualified_new.removeAll(exports_unqualified_old);
    exports_added.forEach(
      a -> receiver.onChange(
        this,
        CChangeModulePackageUnqualifiedExported.of(md_new.name(), a)));

    final Set<CModuleQualifiedExport> exports_qualified_old =
      md_old.exportsQualified();
    final Set<CModuleQualifiedExport> exports_qualified_new =
      md_new.exportsQualified();

    /*
     * Check if any packages are no longer exported qualified. If a previously
     * qualified export becomes unqualified, this is a backwards-compatible
     * change.
     */

    exports_qualified_old.forEach(q -> {
      if (!exports_qualified_new.contains(q)) {
        if (!exports_unqualified_new.contains(q.packageName())) {
          receiver.onChange(
            this,
            CChangeModulePackageNoLongerQualifiedExported.of(
              md_new.name(), q.packageName(), q.moduleName()));
        }
      }
    });

    /*
     * Check if any packages are now exported qualified. If a previously
     * unqualified export becomes qualified, this is a compatibility-breaking
     * change.
     */

    exports_qualified_new.forEach(q -> {
      if (!exports_qualified_old.contains(q)) {
        receiver.onChange(
          this,
          CChangeModulePackageQualifiedExported.of(
            md_new.name(), q.packageName(), q.moduleName()));
      }
    });
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}
