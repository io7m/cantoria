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

import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.jaffirm.core.Preconditions;

import java.util.Objects;

/**
 * The type of comparators for module descriptors.
 */

public interface CModuleDescriptorComparatorType extends CChangeCheckType
{
  /**
   * Compare the module descriptors.
   *
   * @param receiver A change receiver
   * @param md_old   The old version of the module
   * @param md_new   The new version of the module
   */

  void compareModule(
    CChangeReceiverType receiver,
    CModuleDescriptor md_old,
    CModuleDescriptor md_new);

  /**
   * Compare the module descriptors with extra precondition checks.
   *
   * @param receiver A change receiver
   * @param md_old   The old version of the module
   * @param md_new   The new version of the module
   */

  default void compareModuleChecked(
    final CChangeReceiverType receiver,
    final CModuleDescriptor md_old,
    final CModuleDescriptor md_new)
  {
    Objects.requireNonNull(receiver, "Receiver");
    Objects.requireNonNull(md_old, "Module (old)");
    Objects.requireNonNull(md_new, "Module (new)");

    Preconditions.checkPrecondition(
      md_old.name(),
      s -> Objects.equals(s, md_old.name()),
      s -> "Module name " + md_old.name() + " must match " + md_new.name());

    this.compareModule(receiver, md_old, md_new);
  }
}
