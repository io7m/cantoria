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

import io.vavr.collection.SortedSet;
import org.objectweb.asm.tree.ModuleNode;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * The type of loaded modules.
 */

public interface CModuleType extends Closeable
{
  /**
   * @return {@code true} if {@link #close()} has been called
   */

  boolean isClosed();

  /**
   * @return The module descriptor
   */

  CModuleDescriptor descriptor();

  /**
   * @return The module archive
   */

  CArchiveType archive();

  /**
   * @return The parsed module node
   */

  ModuleNode node();

  /**
   * Get the bytes for the given class.
   *
   * @param package_name The package name
   * @param class_name   The class name
   *
   * @return A stream of bytes, or nothing if the class is not present
   *
   * @throws IOException On I/O errors
   */

  Optional<InputStream>
  classBytes(
    String package_name,
    String class_name)
    throws IOException;

  /**
   * Get the parsed class for the given name.
   *
   * @param package_name The package name
   * @param class_name   The class name
   *
   * @return A parsed class, or nothing if the class is not present
   *
   * @throws IOException On I/O errors
   */

  Optional<CClass>
  classValue(
    String package_name,
    String class_name)
    throws IOException;

  /**
   * @param package_name The package
   *
   * @return A list of classes in the given package
   */

  SortedSet<String>
  classesInPackage(
    String package_name);
}
