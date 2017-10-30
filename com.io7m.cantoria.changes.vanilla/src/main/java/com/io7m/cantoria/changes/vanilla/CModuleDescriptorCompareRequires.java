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
import com.io7m.cantoria.api.CModuleRequires;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CModuleDescriptorComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleNoLongerRequired;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerTransitivelyExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageTransitivelyExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleRequired;
import io.vavr.collection.List;
import io.vavr.collection.SortedMap;
import io.vavr.collection.SortedSet;

/**
 * Determine if a module's requirements have changed.
 *
 * JLS 9 §13.3
 */

public final class CModuleDescriptorCompareRequires
  implements CModuleDescriptorComparatorType
{
  /**
   * Construct a comparator.
   */

  public CModuleDescriptorCompareRequires()
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
    return "Determine if a module's requirements have changed";
  }

  @Override
  public void compareModule(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    final SortedMap<String, CModuleRequires> requires_old =
      md_old.requires();
    final SortedMap<String, CModuleRequires> requires_new =
      md_new.requires();

    /*
     * Check if any modules are no longer required
     */

    final SortedMap<String, CModuleRequires> required_removed =
      requires_old.removeAll(requires_new.keySet());
    required_removed.forEach(
      r -> receiver.onChange(
        this,
        CChangeModuleNoLongerRequired.of(
          md_new.name(),
          r._1,
          r._2.isTransitive())));

    /*
     * Check if any modules are now required
     */

    final SortedMap<String, CModuleRequires> exports_added =
      requires_new.removeAll(requires_old.keySet());
    exports_added.forEach(
      r -> receiver.onChange(
        this,
        CChangeModuleRequired.of(md_new.name(), r._1, r._2.isTransitive())));

    /*
     * Check the existing requirements to see if the transitive directives are the same.
     */

    final SortedSet<String> requires_both =
      requires_new.keySet().intersect(requires_old.keySet());

    requires_both.forEach(module -> {
      final CModuleRequires req_old = requires_old.get(module).get();
      final CModuleRequires req_new = requires_new.get(module).get();

      if (req_new.isTransitive() && !req_old.isTransitive()) {
        receiver.onChange(
          this,
          CChangeModulePackageTransitivelyExported.of(
            md_new.name(), req_new.moduleName()));
      }

      if (!req_new.isTransitive() && req_old.isTransitive()) {
        receiver.onChange(
          this,
          CChangeModulePackageNoLongerTransitivelyExported.of(
            md_new.name(), req_new.moduleName()));
      }
    });
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}
