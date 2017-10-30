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

package com.io7m.cantoria.api;

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version number handling.
 */

public final class CVersions
{
  private static final CVersion ZERO =
    CVersion.of(0, 0, 0, "");
  private static final Pattern THREE_PART =
    Pattern.compile("^([0-9]+)\\.([0-9]+)\\.([0-9]+)(-[\\p{Alnum}_\\-]+)?$");
  private static final Pattern TWO_PART =
    Pattern.compile("^([0-9]+)\\.([0-9]+)(-[\\p{Alnum}_\\-]+)?$");
  private static final Pattern ONE_PART =
    Pattern.compile("^([0-9]+)(-[\\p{Alnum}_\\-]+)?$");

  private CVersions()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Pretty-print a version number.
   *
   * @param v The version
   *
   * @return A pretty printed version string
   */

  public static String showVersion(
    final CVersion v)
  {
    NullCheck.notNull(v, "Version");

    final StringBuilder sb = new StringBuilder(32);
    sb.append(v.major());
    sb.append(".");
    sb.append(v.minor());
    sb.append(".");
    sb.append(v.patch());
    if (!v.qualifier().isEmpty()) {
      sb.append("-");
      sb.append(v.qualifier());
    }
    return sb.toString();
  }

  /**
   * Parse a version number, returning the zero version if text is {@code
   * null}.
   *
   * @param text The version text
   *
   * @return A parsed version number
   */

  public static CVersion parseNullable(
    final String text)
  {
    if (text == null) {
      return ZERO;
    }

    return parse(text);
  }

  private static String qualifier(
    final String q)
  {
    if (q == null) {
      return "";
    }
    return q.substring(1);
  }

  /**
   * Parse a version number.
   *
   * @param text The version text
   *
   * @return A parsed version number
   */

  public static CVersion parse(
    final String text)
  {
    NullCheck.notNull(text, "Text");

    {
      final Matcher m = THREE_PART.matcher(text);
      if (m.matches()) {
        return CVersion.of(
          Integer.parseUnsignedInt(m.group(1)),
          Integer.parseUnsignedInt(m.group(2)),
          Integer.parseUnsignedInt(m.group(3)),
          qualifier(m.group(4)));
      }
    }

    {
      final Matcher m = TWO_PART.matcher(text);
      if (m.matches()) {
        return CVersion.of(
          Integer.parseUnsignedInt(m.group(1)),
          Integer.parseUnsignedInt(m.group(2)),
          0,
          qualifier(m.group(3)));
      }
    }

    {
      final Matcher m = ONE_PART.matcher(text);
      if (m.matches()) {
        return CVersion.of(
          Integer.parseUnsignedInt(m.group(1)),
          0,
          0,
          qualifier(m.group(2)));
      }
    }

    return ZERO;
  }
}
