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

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.api.CModules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public final class CModulesTest
{
  @Test
  public void testJavaBase()
    throws Exception
  {
    try (CModuleType m = CModules.openPlatformModule("java.base")) {
      checkBase(m);
    }
  }

  private static void checkBase(final CModuleType m)
    throws IOException
  {
    Assertions.assertEquals("java.base", m.descriptor().name());
    Assertions.assertFalse(m.isClosed());
    Assertions.assertFalse(m.archive().isClosed());

    {
      final Optional<CClass> object_opt =
        m.classValue("java.lang", "Object");
      final CClass object = object_opt.get();
      Assertions.assertEquals("Object", object.name().className());
      Assertions.assertEquals("java.lang", object.name().packageName());
    }

    {
      final Optional<CClass> object_opt =
        m.classValue("java.lang", "Object");
      final CClass object = object_opt.get();
      Assertions.assertEquals("Object", object.name().className());
      Assertions.assertEquals("java.lang", object.name().packageName());
    }
  }

  @Test
  public void testJavaBaseWeaklyCached()
    throws Exception
  {
    try (CModuleType m = CModuleWeaklyCaching.wrap(
      CModules.openPlatformModule("java.base"))) {
      checkBase(m);
    }
  }

  @Test
  public void testPlatformModules()
    throws Exception
  {
    Assertions.assertTrue(CModules.listPlatformModules().contains("java.base"));
  }
}
