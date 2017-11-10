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

package com.io7m.cantoria.tests.api;

import com.io7m.cantoria.modules.api.CModuleLoaderType;
import com.io7m.cantoria.tests.modules.api.CModulesContract;

import java.util.Objects;
import java.util.ServiceLoader;

public final class CModulesTest extends CModulesContract
{
  private static boolean hasCorrectName(
    final CModuleLoaderType p)
  {
    return Objects.equals(p.name(), "com.io7m.cantoria.modules.vanilla.CModules");
  }

  @Override
  protected CModuleLoaderType loader()
  {
    return ServiceLoader.load(CModuleLoaderType.class)
      .stream()
      .map(ServiceLoader.Provider::get)
      .filter(CModulesTest::hasCorrectName)
      .findFirst()
      .get();
  }
}
