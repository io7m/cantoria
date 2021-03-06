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

import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.api.CVersions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class CVersionsTest
{
  @Test
  public void testParse3_Q()
  {
    Assertions.assertEquals(
      CVersion.of(1, 2, 3, "BETA"),
      CVersions.parseNullable("1.2.3-BETA"));
  }

  @Test
  public void testParse3()
  {
    Assertions.assertEquals(
      CVersion.of(1, 2, 3, ""),
      CVersions.parseNullable("1.2.3"));
  }

  @Test
  public void testParse2_Q()
  {
    Assertions.assertEquals(
      CVersion.of(1, 2, 0, "BETA"),
      CVersions.parseNullable("1.2-BETA"));
  }

  @Test
  public void testParse2()
  {
    Assertions.assertEquals(
      CVersion.of(1, 2, 0, ""),
      CVersions.parseNullable("1.2"));
  }

  @Test
  public void testParse1_Q()
  {
    Assertions.assertEquals(
      CVersion.of(1, 0, 0, "BETA"),
      CVersions.parseNullable("1-BETA"));
  }

  @Test
  public void testParse1()
  {
    Assertions.assertEquals(
      CVersion.of(1, 0, 0, ""),
      CVersions.parseNullable("1"));
  }

  @Test
  public void testZero()
  {
    Assertions.assertEquals(
      CVersion.of(0, 0, 0, ""),
      CVersions.parseNullable(null));
  }

  @Test
  public void testShow()
  {
    Assertions.assertEquals(
      "1.2.3-BETA1",
      CVersions.showVersion(CVersion.of(1, 2, 3, "BETA1")));
  }
}
