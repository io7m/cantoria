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
import io.vavr.collection.SortedSet;
import org.objectweb.asm.tree.ModuleNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * An implementation of the {@link CModuleType} interface that delegates to an
 * existing implementation for all operations, but also caches the results of
 * {@link #classValue(String, String)} in a weak hash map to avoid repeated I/O
 * operations.
 */

public final class CModuleWeaklyCaching implements CModuleType
{
  private final CModuleType module;
  private final WeakHashMap<String, CClass> cache;

  private CModuleWeaklyCaching(
    final CModuleType in_module)
  {
    this.module = NullCheck.notNull(in_module, "Module");
    this.cache = new WeakHashMap<>();
  }

  /**
   * Wrap an existing module.
   *
   * @param module The module
   *
   * @return A wrapped module
   */

  public static CModuleType wrap(
    final CModuleType module)
  {
    return new CModuleWeaklyCaching(module);
  }

  @Override
  public boolean isClosed()
  {
    return this.module.isClosed();
  }

  @Override
  public CModuleDescriptor descriptor()
  {
    return this.module.descriptor();
  }

  @Override
  public CArchiveType archive()
  {
    return this.module.archive();
  }

  @Override
  public ModuleNode node()
  {
    return this.module.node();
  }

  @Override
  public Optional<InputStream> classBytes(
    final String package_name,
    final String class_name)
    throws IOException
  {
    return this.module.classBytes(package_name, class_name);
  }

  @Override
  public Optional<CClass> classValue(
    final String package_name,
    final String class_name)
    throws IOException
  {
    final String qual =
      new StringBuilder(32)
        .append(package_name)
        .append(".")
        .append(class_name).toString();

    if (this.cache.containsKey(qual)) {
      return Optional.of(this.cache.get(qual));
    }

    final Optional<CClass> clazz_opt =
      this.module.classValue(package_name, class_name);
    return clazz_opt.map(clazz -> {
      this.cache.put(qual, clazz);
      return clazz;
    });
  }

  @Override
  public SortedSet<String> classesInPackage(final String package_name)
  {
    return this.module.classesInPackage(package_name);
  }

  @Override
  public void close()
    throws IOException
  {
    this.module.close();
  }
}
