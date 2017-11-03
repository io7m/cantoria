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
import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodOverloadComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonFinal;
import io.vavr.collection.List;

/**
 * Compare the finality of methods.
 *
 * <blockquote>Changing a method that is declared final to no longer be declared
 * final does not break compatibility with pre-existing binaries.
 *
 * Changing an instance method that is not declared final to be declared final
 * may break compatibility with existing binaries that depend on the ability to
 * override the method.</blockquote>
 *
 * JLS 9 §13.4.17
 *
 * @see CChangeClassMethodBecameFinal
 * @see CChangeClassMethodBecameNonFinal
 */

public final class CMethodChangedFinality
  extends LocalizedCheck implements CMethodOverloadComparatorType
{
  /**
   * Construct a comparator
   */

  public CMethodChangedFinality()
  {
    super(CMethodChangedFinality.class.getCanonicalName());
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.17");
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
    /*
     * If a class is final, then changes to the finality of individual methods
     * are irrelevant.
     */

    if (CClassModifiers.classIsFinal(class_new.node())) {
      return;
    }

    /*
     * Private methods are effectively final, because they cannot be overridden.
     */

    if (method_new.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    final boolean final_old = method_old.modifiers().contains(CModifier.FINAL);
    final boolean final_new = method_new.modifiers().contains(CModifier.FINAL);

    if (final_old && !final_new) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameNonFinal.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
    }

    if (!final_old && final_new) {
      receiver.onChange(
        this,
        CChangeClassMethodBecameFinal.builder()
          .setMethodPrevious(method_old)
          .setMethod(method_new)
          .build());
    }
  }
}
