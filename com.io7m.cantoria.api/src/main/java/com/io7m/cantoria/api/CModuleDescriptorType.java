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

import io.vavr.collection.Set;
import io.vavr.collection.SortedMap;
import io.vavr.collection.SortedSet;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * The type of module descriptors.
 */

@CImmutableStyleType
@Value.Immutable
@VavrEncodingEnabled
public interface CModuleDescriptorType
{
  /**
   * @return The module name
   */

  @Value.Parameter(order = 0)
  String name();

  /**
   * @return The unqualified exports
   */

  @Value.Parameter(order = 1)
  SortedSet<String> exportsUnqualified();

  /**
   * @return The qualified exports
   */

  @Value.Parameter(order = 2)
  Set<CModuleQualifiedExport> exportsQualified();

  /**
   * @return The provided services
   */

  @Value.Parameter(order = 3)
  Set<CModuleProvides> provides();

  /**
   * @return The requires directives
   */

  @Value.Parameter(order = 4)
  SortedMap<String, CModuleRequires> requires();

  /**
   * @return The unqualified opens
   */

  @Value.Parameter(order = 5)
  SortedSet<String> opensUnqualified();

  /**
   * @return The qualified opens
   */

  @Value.Parameter(order = 6)
  Set<CModuleQualifiedOpens> opensQualified();
}
