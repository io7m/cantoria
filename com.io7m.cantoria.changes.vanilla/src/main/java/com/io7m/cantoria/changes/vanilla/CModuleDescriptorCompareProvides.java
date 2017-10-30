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
import com.io7m.cantoria.api.CModuleProvides;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CModuleDescriptorComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleServiceNoLongerProvided;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleServiceProvided;
import io.vavr.collection.List;
import io.vavr.collection.Set;

/**
 * Determine if any new services have been provided or revoked.
 *
 * JLS 9 §13.3
 *
 * @see CChangeModuleServiceNoLongerProvided
 * @see CChangeModuleServiceProvided
 */

public final class CModuleDescriptorCompareProvides
  implements CModuleDescriptorComparatorType
{
  /**
   * Construct a comparator.
   */

  public CModuleDescriptorCompareProvides()
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
    return "Determine if any new services have been provided or revoked";
  }

  @Override
  public void compareModule(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    final Set<CModuleProvides> provides_old = md_old.provides();
    final Set<CModuleProvides> provides_new = md_new.provides();

    final Set<CModuleProvides> provides_removed =
      provides_old.removeAll(provides_new);
    final Set<CModuleProvides> provides_added =
      provides_new.removeAll(provides_old);

    provides_removed.forEach(
      provides -> receiver.onChange(
        this,
        CChangeModuleServiceNoLongerProvided.of(md_new.name(), provides)));

    provides_added.forEach(
      provides -> receiver.onChange(
        this,
        CChangeModuleServiceProvided.of(md_new.name(), provides)));
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}