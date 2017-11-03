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
import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGenericsComparison;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CClassComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassGenericsChanged;
import io.vavr.collection.List;

import java.util.Optional;

/**
 * Determine if a class has modified generic parameters.
 *
 * @see CChangeClassGenericsChanged
 */

public final class CClassChangedGenerics
  extends LocalizedCheck implements CClassComparatorType
{
  /**
   * Construct a comparator
   */

  public CClassChangedGenerics()
  {
    super(CClassChangedGenerics.class.getCanonicalName());
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.empty();
  }

  @Override
  public void compareClass(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new)
  {
    if (class_new.accessibility() != CAccessibility.PUBLIC) {
      return;
    }

    final Optional<CGClassSignature> sig_old_opt = class_old.signature();
    final Optional<CGClassSignature> sig_new_opt = class_new.signature();

    if (!sig_old_opt.isPresent() && !sig_new_opt.isPresent()) {
      return;
    }

    if (!sig_old_opt.isPresent() && sig_new_opt.isPresent()) {
      receiver.onChange(
        this, CChangeClassGenericsChanged.of(class_new, class_old));
      return;
    }

    if (sig_old_opt.isPresent() && !sig_new_opt.isPresent()) {
      receiver.onChange(
        this, CChangeClassGenericsChanged.of(class_new, class_old));
      return;
    }

    final CGClassSignature sig_new = sig_new_opt.get();
    final CGClassSignature sig_old = sig_old_opt.get();

    if (!CGenericsComparison.typeParametersAreEquivalent(
      sig_old.parameters(), sig_new.parameters())) {
      receiver.onChange(
        this, CChangeClassGenericsChanged.of(class_new, class_old));
    }
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }
}
