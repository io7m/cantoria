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

/**
 * The accessibility of a language element such as a class, field, or method.
 */

public enum CAccessibility
{
  /**
   * Accessible to the current class only.
   */

  PRIVATE(0, "private"),

  /**
   * Accessible to subclasses only.
   */

  PROTECTED(1, "protected"),

  /**
   * Accessible to classes within the current package only.
   */

  PACKAGE_PRIVATE(2, ""),

  /**
   * Accessible to classes in modules that can read the current class.
   */

  PUBLIC(3, "public");

  private final int accessibility;
  private final String keyword;

  CAccessibility(
    final int in_access,
    final String in_keyword)
  {
    this.accessibility = in_access;
    this.keyword = NullCheck.notNull(in_keyword, "Keyword");
  }

  /**
   * @return The privacy level, where 0 is {@link #PRIVATE} and larger values
   * denote higher privacy (less accessibility)
   */

  public int accessibility()
  {
    return this.accessibility;
  }

  /**
   * @return The corresponding Java language keyword
   */

  public String keyword()
  {
    return this.keyword;
  }
}
