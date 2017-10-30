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

package com.io7m.cantoria.changes.vanilla;

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBytecodeVersionChanged;
import io.vavr.collection.List;
import org.objectweb.asm.tree.ClassNode;

/**
 * Determine if a class's bytecode version has changed.
 *
 * @see CChangeClassBytecodeVersionChanged
 */

public final class CClassChangedBytecodeVersion implements CClassComparatorType
{
  /**
   * Construct a comparator
   */

  public CClassChangedBytecodeVersion()
  {

  }

  @Override
  public List<String> jlsReferences()
  {
    return List.empty();
  }

  @Override
  public String description()
  {
    return "Determine if a class's bytecode version has changed";
  }

  @Override
  public void compareClass(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass clazz_old,
    final CClass clazz_new)
  {
    /*
     * Ignore the bytecode version of non-public classes.
     */

    if (!CClassModifiers.classIsPublic(clazz_new.node())) {
      return;
    }

    final ClassNode cn_new = clazz_new.node();
    final ClassNode cn_old = clazz_old.node();
    if (cn_new.version != cn_old.version) {
      receiver.onChange(
        this,
        CChangeClassBytecodeVersionChanged.of(
          clazz_new.name(),
          CClassModifiers.classModifiers(clazz_new.node()),
          cn_old.version,
          cn_new.version));
    }
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}
