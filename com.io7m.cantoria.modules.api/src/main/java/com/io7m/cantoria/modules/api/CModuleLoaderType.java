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

package com.io7m.cantoria.modules.api;

import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CVersion;
import io.vavr.collection.SortedSet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * The type of module loaders.
 */

public interface CModuleLoaderType
{
  /**
   * @return The name of the module loader implementation
   */

  String name();

  /**
   * List the available platform modules.
   *
   * @return The list of available platform modules
   *
   * @throws IOException On I/O errors
   */

  SortedSet<String> listPlatformModules()
    throws IOException;

  /**
   * Open the named platform module.
   *
   * @param name The module name
   *
   * @return An opened module
   *
   * @throws IOException On I/O errors
   */

  CModuleType openPlatformModule(
    String name)
    throws IOException;

  /**
   * Open an archive and parse the module descriptor.
   *
   * @param path    The path to the file, for diagnostic purposes
   * @param version The archive version
   *
   * @return An opened module
   *
   * @throws IOException On I/O errors
   */

  default CModuleType open(
    final Path path,
    final CVersion version)
    throws IOException
  {
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(version, "Version");
    return this.openFromZip(path, version, new ZipFile(path.toFile()));
  }

  /**
   * Open an archive and parse the module descriptor.
   *
   * @param path    The path to the file, for diagnostic purposes
   * @param version The archive version
   * @param input   The archive input
   *
   * @return An opened module
   *
   * @throws IOException On I/O errors
   */

  CModuleType openFromZip(
    Path path,
    CVersion version,
    ZipFile input)
    throws IOException;
}
