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
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CEnum;
import com.io7m.cantoria.api.CEnumMember;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CEnumComparatorType;
import com.io7m.cantoria.changes.vanilla.api.CChangeEnumAddedMembers;
import com.io7m.cantoria.changes.vanilla.api.CChangeEnumRemovedMembers;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * Check if any members were removed from an enum.
 *
 * JLS 9 §13.4.26
 *
 * <blockquote>If a pre-existing binary attempts to access an enum constant that
 * no longer exists, the client will fail at run time with a NoSuchFieldError.
 * Therefore such a change is not recommended for widely distributed
 * enums.</blockquote>
 *
 * @see CChangeEnumAddedMembers
 */

public final class CEnumCheckMembersRemoved
  extends LocalizedCheck implements CEnumComparatorType
{
  /**
   * Construct a comparator
   */

  public CEnumCheckMembersRemoved()
  {
    super(CEnumCheckMembersRemoved.class.getCanonicalName());
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.26");
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public void compareEnum(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CEnum class_old,
    final CEnum class_new)
  {
    if (class_new.accessibility() != CAccessibility.PUBLIC) {
      return;
    }

    final Map<String, CEnumMember> removed =
      class_old.members().removeAll(class_new.members().keySet());

    if (!removed.isEmpty()) {
      receiver.onChange(
        this,
        CChangeEnumRemovedMembers.builder()
          .setEnumPrevious(class_old)
          .setEnumType(class_new)
          .build());
    }
  }
}
