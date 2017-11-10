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

package com.io7m.cantoria.tests.changes.spi;

import com.io7m.cantoria.api.CClassRegistry;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.changes.api.CChangeType;
import com.io7m.cantoria.changes.spi.CChangeCheckType;
import com.io7m.cantoria.changes.spi.CChangeDescriberType;
import com.io7m.cantoria.driver.CModuleComparisons;
import com.io7m.cantoria.tests.CTestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class CChangeDescriberTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CChangeDescriberTest.class);

  private static CClassRegistryType classRegistry(
    final CModuleType... modules)
    throws IOException
  {
    return CClassRegistry.create(
      io.vavr.collection.List.of(modules)
        .prepend(CTestUtilities.defaultModuleLoader()
                   .openPlatformModule("java.base"))
        .map(CModuleWeaklyCaching::wrap));
  }

  private static void describe(
    final ServiceLoader<CChangeDescriberType> providers,
    final CChangeCheckType originator,
    final CChangeType change)
  {
    final Iterator<CChangeDescriberType> iter = providers.iterator();
    while (iter.hasNext()) {
      final CChangeDescriberType describer = iter.next();

      Assertions.assertTrue(
        describer.canDescribe(change),
        () -> new StringBuilder(128)
          .append("Describer ")
          .append(describer)
          .append(" must support change type ")
          .append(change)
          .append(System.lineSeparator())
          .toString());

      try {
        describer.describe(originator, change, System.out);
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private static List<String> listComparisonModules()
    throws IOException
  {
    final String path = "/com/io7m/cantoria/tests/driver/api";

    final URL url = CChangeDescriberTest.class.getResource(path);
    if (url == null) {
      throw new NoSuchFileException(
        path);
    }

    final List<String> lines;
    try (InputStream stream = url.openStream()) {
      try (BufferedReader reader =
             new BufferedReader(new InputStreamReader(stream, UTF_8))) {
        lines = reader.lines()
          .filter(line -> !line.contains("."))
          .collect(Collectors.toList());
      }
    }
    return lines;
  }

  /**
   * XXX: This test is quite excessively brutal: it tries to use all available
   * describers to describe all available changes. There is specifically no
   * guarantee that any particular describer can describe any particular change,
   * it's just a coincidence that the current single describer implementation
   * can describe all of the vanilla changes.
   */

  @Test
  public void testAllDescribers()
    throws Exception
  {
    final List<String> lines = listComparisonModules();
    final CModuleComparisons comp = CModuleComparisons.create();
    final AtomicReference<Exception> ex = new AtomicReference<>();
    final ServiceLoader<CChangeDescriberType> providers =
      ServiceLoader.load(CChangeDescriberType.class);

    lines.forEach(line -> {
      try {
        try (CModuleType module0 = CTestUtilities.module(line + "/before")) {
          try (CModuleType module1 = CTestUtilities.module(line + "/after")) {
            final CClassRegistryType er = classRegistry(module0, module1);
            comp.compareModules(
              (originator, change) ->
                describe(providers, originator, change), er, module0, module1);
          }
        }
      } catch (final Exception e) {
        LOG.error("module comparison exception: ", e);
        if (ex.get() != null) {
          ex.get().addSuppressed(e);
        } else {
          ex.set(e);
        }
      }
    });

    if (ex.get() != null) {
      throw ex.get();
    }
  }
}
