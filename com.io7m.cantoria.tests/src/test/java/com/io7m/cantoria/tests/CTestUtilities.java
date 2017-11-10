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

package com.io7m.cantoria.tests;

import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.modules.api.CModuleLoaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.zip.ZipFile;

public final class CTestUtilities
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CTestUtilities.class);

  private CTestUtilities()
  {

  }

  public static CModuleType module(
    final String name)
    throws Exception
  {
    final Path f = Files.createTempFile("cantoria-test-", ".jar");
    LOG.debug("copying {} to {}", name, f);

    try (OutputStream out = Files.newOutputStream(f)) {
      final String path =
        "/com/io7m/cantoria/tests/driver/api/" + name + "/module.jar";
      final URL url = CTestUtilities.class.getResource(path);

      if (url == null) {
        throw new NoSuchFileException(path);
      }

      try (InputStream in = url.openStream()) {
        final byte[] buffer = new byte[8192];
        while (true) {
          final int r = in.read(buffer);
          if (r <= 0) {
            break;
          }
          out.write(buffer, 0, r);
        }
        out.flush();
      }
    }

    return defaultModuleLoader().openFromZip(
      f,
      CVersion.of(1, 0, 0, ""),
      new ZipFile(f.toFile()));
  }

  public static CModuleLoaderType defaultModuleLoader()
  {
    return ServiceLoader.load(CModuleLoaderType.class)
      .findFirst()
      .get();
  }
}
