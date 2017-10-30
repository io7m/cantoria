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

package com.io7m.cantoria.driver.api;

import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;

import java.io.IOException;

/**
 * The type of comparison drivers.
 */

public interface CComparisonDriverType
{
  /**
   * Compare the given module descriptors, delivering changes to the receiver.
   *
   * @param receiver The change receiver
   * @param md_old   The old module descriptor
   * @param md_new   The new module descriptor
   */

  void compareModuleDescriptors(
    CChangeReceiverType receiver,
    CModuleDescriptor md_old,
    CModuleDescriptor md_new);

  /**
   * Compare the given modules recursively, delivering changes to the receiver.
   *
   * @param receiver   The change receiver
   * @param registry   A class registry for finding external classes
   * @param module_old The old module
   * @param module_new The new module
   *
   * @throws IOException On I/O errors
   */

  void compareModules(
    CChangeReceiverType receiver,
    CClassRegistryType registry,
    CModuleType module_old,
    CModuleType module_new)
    throws IOException;
}
