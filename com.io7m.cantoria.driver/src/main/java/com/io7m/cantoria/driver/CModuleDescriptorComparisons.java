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

import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CModuleDescriptorComparatorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Functions to compare module descriptors.
 */

public final class CModuleDescriptorComparisons
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CModuleDescriptorComparisons.class);

  private ServiceLoader<CModuleDescriptorComparatorType> services;

  private CModuleDescriptorComparisons()
  {
    this.services = ServiceLoader.load(CModuleDescriptorComparatorType.class);
  }

  /**
   * @return A module descriptor comparison driver
   */

  public static CModuleDescriptorComparisons create()
  {
    return new CModuleDescriptorComparisons();
  }

  /**
   * Compare the given module descriptors, delivering changes to the receiver.
   *
   * @param receiver The change receiver
   * @param md_old   The old module descriptor
   * @param md_new   The new module descriptor
   */

  public void compareModuleDescriptors(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(md_old, "Module descriptor (old)");
    Objects.requireNonNull(md_new, "Module descriptor (new)");

    this.services.forEach(
      check -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}",
            check.name(),
            md_new.name());
        }

        check.compareModule(receiver, md_old, md_new);
      });
  }
}
