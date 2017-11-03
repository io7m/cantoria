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

import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.changes.api.CChangeType;
import com.io7m.cantoria.changes.api.CCompatibilityTracker;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.io7m.cantoria.changes.api.CChangeBinaryCompatibility.BINARY_COMPATIBLE;
import static com.io7m.cantoria.changes.api.CChangeBinaryCompatibility.BINARY_INCOMPATIBLE;
import static com.io7m.cantoria.changes.api.CChangeSemanticVersioning.SEMANTIC_MAJOR;
import static com.io7m.cantoria.changes.api.CChangeSemanticVersioning.SEMANTIC_MINOR;
import static com.io7m.cantoria.changes.api.CChangeSemanticVersioning.SEMANTIC_NONE;
import static com.io7m.cantoria.changes.api.CChangeSourceCompatibility.SOURCE_COMPATIBLE;
import static com.io7m.cantoria.changes.api.CChangeSourceCompatibility.SOURCE_INCOMPATIBLE;

public final class CCompatibilityTrackerTest
{
  @Test
  public void testCompatibilityInitial()
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        BINARY_COMPATIBLE, t.binaryCompatibility()),
      () -> Assertions.assertEquals(
        SOURCE_COMPATIBLE, t.sourceCompatibility()),
      () -> Assertions.assertEquals(
        SEMANTIC_NONE, t.semanticVersioning()));
  }

  @Test
  public void testCompatibilityMajor(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_COMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_COMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_MAJOR;
    }};

    t.onChange(c);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        BINARY_COMPATIBLE, t.binaryCompatibility()),
      () -> Assertions.assertEquals(
        SOURCE_COMPATIBLE, t.sourceCompatibility()),
      () -> Assertions.assertEquals(
        SEMANTIC_MAJOR, t.semanticVersioning()));
  }

  @Test
  public void testCompatibilityMinor(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_COMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_COMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_MINOR;
    }};

    t.onChange(c);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        BINARY_COMPATIBLE, t.binaryCompatibility()),
      () -> Assertions.assertEquals(
        SOURCE_COMPATIBLE, t.sourceCompatibility()),
      () -> Assertions.assertEquals(
        SEMANTIC_MINOR, t.semanticVersioning()));
  }

  @Test
  public void testCompatibilityBinary(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_COMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_INCOMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_NONE;
    }};

    t.onChange(c);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        BINARY_INCOMPATIBLE, t.binaryCompatibility()),
      () -> Assertions.assertEquals(
        SOURCE_COMPATIBLE, t.sourceCompatibility()),
      () -> Assertions.assertEquals(
        SEMANTIC_NONE, t.semanticVersioning()));
  }

  @Test
  public void testCompatibilitySource(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_INCOMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_COMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_NONE;
    }};

    t.onChange(c);

    Assertions.assertAll(
      () -> Assertions.assertEquals(
        BINARY_COMPATIBLE, t.binaryCompatibility()),
      () -> Assertions.assertEquals(
        SOURCE_INCOMPATIBLE, t.sourceCompatibility()),
      () -> Assertions.assertEquals(
        SEMANTIC_NONE, t.semanticVersioning()));
  }

  @Test
  public void testSuggestedVersionMajor(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_INCOMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_INCOMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_MAJOR;
    }};

    t.onChange(c);

    Assertions.assertEquals(
      CVersion.of(2, 0, 0, ""),
      t.suggestVersionNumber(CVersion.of(1, 2, 3, "")));
  }

  @Test
  public void testSuggestedVersionMinor(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_INCOMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_INCOMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_MINOR;
    }};

    t.onChange(c);

    Assertions.assertEquals(
      CVersion.of(1, 3, 0, ""),
      t.suggestVersionNumber(CVersion.of(1, 2, 3, "")));
  }

  @Test
  public void testSuggestedVersionNone(
    final @Mocked CChangeType c)
  {
    final CCompatibilityTracker t = CCompatibilityTracker.create();

    new Expectations()
    {{
      c.sourceCompatibility();
      this.result = SOURCE_INCOMPATIBLE;
      c.binaryCompatibility();
      this.result = BINARY_INCOMPATIBLE;
      c.semanticVersioning();
      this.result = SEMANTIC_NONE;
    }};

    t.onChange(c);

    Assertions.assertEquals(
      CVersion.of(1, 2, 4, ""),
      t.suggestVersionNumber(CVersion.of(1, 2, 3, "")));
  }
}
