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

import java.io.IOException;
import java.util.Optional;

/**
 * The type of class registries.
 */

public interface CClassRegistryType
{
  /**
   * Find a class.
   *
   * @param module_name  The module name
   * @param package_name The package
   * @param class_name   The class
   *
   * @return The class, if one exists
   *
   * @throws IOException On I/O errors
   */

  Optional<CClass> findClassInModule(
    String module_name,
    String package_name,
    String class_name)
    throws IOException;

  /**
   * Find a class.
   *
   * @param package_name The package
   * @param class_name   The class
   *
   * @return The class, if one exists
   *
   * @throws IOException On I/O errors
   */

  Optional<CClass> findClass(
    String package_name,
    String class_name)
    throws IOException;
}
