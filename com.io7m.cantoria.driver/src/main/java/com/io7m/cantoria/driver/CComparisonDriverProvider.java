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

import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.driver.api.CComparisonDriverProviderType;
import com.io7m.cantoria.driver.api.CComparisonDriverType;

import java.io.IOException;

/**
 * A driver provider.
 */

public final class CComparisonDriverProvider implements
  CComparisonDriverProviderType
{
  /**
   * Construct a driver provider.
   */

  public CComparisonDriverProvider()
  {

  }

  @Override
  public CComparisonDriverType create()
  {
    return new CDriver();
  }

  private static final class CDriver implements CComparisonDriverType
  {
    private final CModuleComparisons module_comp;
    private final CModuleDescriptorComparisons module_desc_comp;

    CDriver()
    {
      this.module_desc_comp = CModuleDescriptorComparisons.create();
      this.module_comp = CModuleComparisons.create();
    }

    @Override
    public void compareModuleDescriptors(
      final CChangeReceiverType receiver,
      final CModuleDescriptor md_old,
      final CModuleDescriptor md_new)
    {
      this.module_desc_comp.compareModuleDescriptors(receiver, md_old, md_new);
    }

    @Override
    public void compareModules(
      final CChangeReceiverType receiver,
      final CClassRegistryType registry,
      final CModuleType module_old,
      final CModuleType module_new)
      throws IOException
    {
      this.module_comp.compareModules(
        receiver, registry, module_old, module_new);
    }
  }
}
