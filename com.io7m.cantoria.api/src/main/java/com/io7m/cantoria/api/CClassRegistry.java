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
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

/**
 * The class registry.
 */

public final class CClassRegistry implements CClassRegistryType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CClassRegistry.class);

  private final HashMap<String, CModuleType> modules_by_name;
  private final HashMap<String, String> module_by_package;

  private CClassRegistry(
    final HashMap<String, CModuleType> in_modules_by_name,
    final HashMap<String, String> in_module_by_package)
  {
    this.modules_by_name =
      NullCheck.notNull(in_modules_by_name, "Modules");
    this.module_by_package =
      NullCheck.notNull(in_module_by_package, "Modules");
  }

  /**
   * Create a class registry.
   *
   * @param modules The list of modules
   *
   * @return A new class registry
   */

  public static CClassRegistryType create(
    final List<CModuleType> modules)
  {
    NullCheck.notNull(modules, "Archives");

    HashMap<String, CModuleType> modules_by_name = HashMap.empty();
    HashMap<String, String> module_by_package = HashMap.empty();

    for (int index = 0; index < modules.size(); ++index) {
      final CModuleType module = modules.get(index);
      final CModuleDescriptor module_desc = module.descriptor();
      final String module_name = module_desc.name();

      modules_by_name = modules_by_name.put(module_name, module);
      for (final String package_name : module_desc.exportsUnqualified()) {
        module_by_package =
          module_by_package.put(package_name, module_name);
      }
      for (final CModuleQualifiedExport export : module_desc.exportsQualified()) {
        module_by_package =
          module_by_package.put(export.packageName(), module_name);
      }
    }

    return new CClassRegistry(modules_by_name, module_by_package);
  }

  @Override
  public Optional<CClass> findClassInModule(
    final String module_name,
    final String package_name,
    final String class_name)
    throws IOException
  {
    NullCheck.notNull(module_name, "Module");
    NullCheck.notNull(package_name, "Package");
    NullCheck.notNull(class_name, "Class");

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "findClassInModule: {}/{}:{}",
        module_name,
        package_name,
        class_name);
    }

    try {
      if (this.modules_by_name.containsKey(module_name)) {
        final CModuleType module = this.modules_by_name.get(module_name).get();
        return module.classValue(package_name, class_name);
      }

      LOG.debug("no such module: {}", module_name);
      return Optional.empty();
    } catch (final UncheckedIOException e) {
      throw e.getCause();
    }
  }

  @Override
  public Optional<CClass> findClass(
    final String package_name,
    final String class_name)
    throws IOException
  {
    NullCheck.notNull(package_name, "Package");
    NullCheck.notNull(class_name, "Class");

    if (LOG.isTraceEnabled()) {
      LOG.trace("findClass: {}:{}", package_name, class_name);
    }

    try {
      if (this.module_by_package.containsKey(package_name)) {
        final String module_name =
          this.module_by_package.get(package_name).get();
        return this.findClassInModule(module_name, package_name, class_name);
      }

      LOG.debug("no module for package: {}", package_name);
      return Optional.empty();
    } catch (final UncheckedIOException e) {
      throw e.getCause();
    }
  }
}
