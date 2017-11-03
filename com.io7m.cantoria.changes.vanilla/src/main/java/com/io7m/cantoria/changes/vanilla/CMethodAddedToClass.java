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
import com.io7m.cantoria.api.CConstructors;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodCheckAdditionType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassConstructorAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassStaticInitializerAdded;
import io.vavr.collection.List;
import io.vavr.collection.Map;

/**
 * JLS 9 §13.4.12
 *
 * <blockquote>Adding a method or constructor declaration to a class will not
 * break compatibility with any pre-existing binaries, even in the case where a
 * type could no longer be recompiled because an invocation previously
 * referenced a method or constructor of a superclass with an incompatible type.
 * The previously compiled class with such a reference will continue to
 * reference the method or constructor declared in a superclass.</blockquote>
 */

public final class CMethodAddedToClass
  extends LocalizedCheck implements CMethodCheckAdditionType
{
  /**
   * Construct a method check.
   */

  public CMethodAddedToClass()
  {
    super(CMethodAddedToClass.class.getCanonicalName());
  }

  @Override
  public String name()
  {
    return this.getClass().getCanonicalName();
  }

  @Override
  public List<String> jlsReferences()
  {
    return List.of("JLS 9 §13.4.12");
  }

  @Override
  public void checkMethodAddition(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    /*
     * Ignore interface types and private methods.
     */

    if (class_new.modifiers().contains(CModifier.INTERFACE)) {
      return;
    }

    if (method.accessibility() == CAccessibility.PRIVATE) {
      return;
    }

    if (method.isStaticInitializer()) {
      receiver.onChange(
        this,
        CChangeClassStaticInitializerAdded.builder()
          .setClassPrevious(class_old)
          .setClassValue(class_new)
          .build());
      return;
    }

    if (!overloads.isEmpty()) {
      if (method.isInstanceConstructor()) {
        receiver.onChange(
          this,
          CChangeClassConstructorAdded.of(CConstructors.constructor(method)));
        return;
      }

      receiver.onChange(this, CChangeClassMethodOverloadAdded.of(method));
      return;
    }

    if (method.isInstanceConstructor()) {
      receiver.onChange(
        this,
        CChangeClassConstructorAdded.of(CConstructors.constructor(method)));
      return;
    }

    receiver.onChange(this, CChangeClassMethodAdded.of(method));
  }
}
