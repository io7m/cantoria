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
import com.io7m.cantoria.api.CModuleQualifiedOpens;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CModuleDescriptorComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerQualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerUnqualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageQualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageUnqualifiedOpened;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;

/**
 * Determine if any packages have been newly opened, or have been removed.
 *
 * JLS 9 §13.3
 *
 * @see CChangeModulePackageNoLongerQualifiedOpened
 * @see CChangeModulePackageNoLongerUnqualifiedOpened
 * @see CChangeModulePackageQualifiedOpened
 * @see CChangeModulePackageUnqualifiedOpened
 */

public final class CModuleDescriptorCompareOpens
  implements CModuleDescriptorComparatorType
{
  /**
   * Construct a comparator.
   */

  public CModuleDescriptorCompareOpens()
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
    return "Determine if any packages have been newly opened, or have been removed";
  }

  @Override
  public void compareModule(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    final SortedSet<String> opens_unqualified_old =
      md_old.opensUnqualified();
    final SortedSet<String> opens_unqualified_new =
      md_new.opensUnqualified();

    /*
     * Check if any packages are no longer opened unqualified.
     */

    final SortedSet<String> opens_removed =
      opens_unqualified_old.removeAll(opens_unqualified_new);
    opens_removed.forEach(
      r -> receiver.onChange(
        this,
        CChangeModulePackageNoLongerUnqualifiedOpened.of(md_new.name(), r)));

    /*
     * Check if any packages are now opened unqualified.
     */

    final SortedSet<String> opens_added =
      opens_unqualified_new.removeAll(opens_unqualified_old);
    opens_added.forEach(
      a -> receiver.onChange(
        this,
        CChangeModulePackageUnqualifiedOpened.of(md_new.name(), a)));

    final Set<CModuleQualifiedOpens> opens_qualified_old =
      md_old.opensQualified();
    final Set<CModuleQualifiedOpens> opens_qualified_new =
      md_new.opensQualified();

    /*
     * Check if any packages are no longer opened qualified. If a previously
     * qualified open becomes unqualified, this is a backwards-compatible
     * change.
     */

    opens_qualified_old.forEach(q -> {
      if (!opens_qualified_new.contains(q)) {
        if (!opens_unqualified_new.contains(q.packageName())) {
          receiver.onChange(
            this,
            CChangeModulePackageNoLongerQualifiedOpened.of(
              md_new.name(), q.packageName(), q.moduleName()));
        }
      }
    });

    /*
     * Check if any packages are now opened qualified. If a previously
     * unqualified open becomes qualified, this is a compatibility-breaking
     * change.
     */

    opens_qualified_new.forEach(q -> {
      if (!opens_qualified_old.contains(q)) {
        receiver.onChange(
          this,
          CChangeModulePackageQualifiedOpened.of(
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
