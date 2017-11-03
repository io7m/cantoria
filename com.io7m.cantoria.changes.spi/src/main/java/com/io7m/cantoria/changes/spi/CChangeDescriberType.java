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

package com.io7m.cantoria.changes.spi;

import com.io7m.cantoria.changes.api.CChangeType;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The type of describers for changes.
 */

public interface CChangeDescriberType
{
  /**
   * @param change The change
   *
   * @return {@code true} iff this describer can describe the given change
   */

  boolean canDescribe(
    CChangeType change);

  /**
   * @return The name of the format this describer supports
   */

  String format();

  /**
   * Describe a given change.
   *
   * @param originator The originating check
   * @param change     The change
   * @param out        The output stream
   *
   * @throws IOException On I/O errors
   */

  void describe(
    CChangeCheckType originator,
    CChangeType change,
    OutputStream out)
    throws IOException;
}
