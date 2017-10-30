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

import com.io7m.jnull.NullCheck;

/**
 * The type of changes encountered when comparing versions of classes and
 * modules.
 */

public interface CChangeType
{
  /**
   * @return The change category
   */

  Category category();

  /**
   * @return The compatibility according to semantic versioning
   */

  CChangeSemanticVersioning semanticVersioning();

  /**
   * @return The binary compatibility of the change
   */

  CChangeBinaryCompatibility binaryCompatibility();

  /**
   * @return The source compatibility of the change
   */

  CChangeSourceCompatibility sourceCompatibility();

  /**
   * The change category.
   */

  enum Category
  {

    /**
     * @see CChangeFieldType
     */

    CHANGE_FIELD(CChangeFieldType.class),

    /**
     * @see CChangeClassType
     */

    CHANGE_CLASS(CChangeClassType.class),

    /**
     * @see CChangeConstructorType
     */

    CHANGE_CONSTRUCTOR(CChangeConstructorType.class),

    /**
     * @see CChangeMethodType
     */

    CHANGE_METHOD(CChangeMethodType.class),

    /**
     * @see CChangeModuleType
     */

    CHANGE_MODULE(CChangeModuleType.class),

    /**
     * @see CChangeEnumType
     */

    CHANGE_ENUM(CChangeEnumType.class);

    private final Class<? extends CChangeType> type_class;

    Category(final Class<? extends CChangeType> type)
    {
      this.type_class = NullCheck.notNull(type, "Category");
    }

    /**
     * @return The precise type of classes in the category
     */

    public Class<? extends CChangeType> categoryClass()
    {
      return this.type_class;
    }
  }
}
