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

package com.io7m.cantoria.tests.examples;

import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.api.CModules;
import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.driver.CModuleComparisons;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.zip.ZipFile;

public final class ReadModule
{
  private static final Logger LOG =
    LoggerFactory.getLogger(ReadModule.class);

  private ReadModule()
  {
    throw new UnreachableCodeException();
  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final String path0 = args[0];
    final String path1 = args[1];
    final ZipFile file0 = new ZipFile(path0);
    final ZipFile file1 = new ZipFile(path1);

    final CClassRegistryType er = null;

    final CVersion version = CVersion.of(1, 0, 0, "");
    try (CModuleType module0 =
           CModuleWeaklyCaching.wrap(
             CModules.openFromZip(Paths.get(path0), version, file0))) {

      LOG.debug("module: {}", module0.descriptor());
      LOG.debug("module:  {}", module0.descriptor());

      try (CModuleType module1 =
             CModuleWeaklyCaching.wrap(
               CModules.openFromZip(Paths.get(path1), version, file1))) {

        LOG.debug("module: {}", module1.descriptor());
        LOG.debug("module:  {}", module1.descriptor());

        CModuleComparisons.create().compareModules(
          (check, change) -> LOG.info("change: {}", change),
          er,
          module0,
          module1);
      }
    }
  }
}
