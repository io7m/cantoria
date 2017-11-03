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

import com.io7m.cantoria.api.CAccessibility;
import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodOverloadComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonVarArgs;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameVarArgs;
import io.vavr.collection.List;

/**
 * Compare varargs modifier of methods.
 *
 * @see CChangeClassMethodBecameVarArgs
 * @see CChangeClassMethodBecameNonVarArgs
 */

public final class CMethodChangedVarArgs
  extends LocalizedCheck implements CMethodOverloadComparatorType
{
  /**
   * Construct a comparator
   */

  public CMethodChangedVarArgs()
  {
    super(CMethodChangedVarArgs.class.getCanonicalName());
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.empty();
  }

  @Override
  public void compareMethodOverload(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CMethod method_old,
    final CClass class_new,
    final CMethod method_new)
  {
    if (method_new.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    final boolean varargs_old = method_old.isVariadic();
    final boolean varargs_new = method_new.isVariadic();

    if (varargs_old && !varargs_new) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameNonVarArgs.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
    }

    if (!varargs_old && varargs_new) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameVarArgs.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
    }
  }
}
