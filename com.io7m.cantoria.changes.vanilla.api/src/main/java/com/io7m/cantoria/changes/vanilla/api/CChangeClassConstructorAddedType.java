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

package com.io7m.cantoria.changes.vanilla.api;

import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CConstructor;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.changes.api.CChangeBinaryCompatibility;
import com.io7m.cantoria.changes.api.CChangeConstructorType;
import com.io7m.cantoria.changes.api.CChangeSemanticVersioning;
import com.io7m.cantoria.changes.api.CChangeSourceCompatibility;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

/**
 * JLS 9, §13.4.12:
 *
 * <blockquote>Adding a method or constructor declaration to a class will not
 * break compatibility with any pre-existing binaries, even in the case where a
 * type could no longer be recompiled because an invocation previously
 * referenced a method or constructor of a superclass with an incompatible type.
 * The previously compiled class with such a reference will continue to
 * reference the method or constructor declared in a superclass.</blockquote>
 *
 * In other words, adding a constructor with the same name, accessibility, and
 * signature as one defined in a superclass or subclass is guaranteed to be
 * binary compatible. All other constructor additions are not.
 */

@CImmutableStyleType
@VavrEncodingEnabled
@Value.Immutable
public interface CChangeClassConstructorAddedType
  extends CChangeConstructorType
{
  @Override
  default CClassName className()
  {
    return this.constructor().className();
  }

  @Override
  @Value.Parameter
  CConstructor constructor();

  @Override
  default CChangeSemanticVersioning semanticVersioning()
  {
    return CChangeSemanticVersioning.SEMANTIC_MINOR;
  }

  @Override
  default CChangeBinaryCompatibility binaryCompatibility()
  {
    return CChangeBinaryCompatibility.BINARY_COMPATIBLE;
  }

  @Override
  default CChangeSourceCompatibility sourceCompatibility()
  {
    return CChangeSourceCompatibility.SOURCE_COMPATIBLE;
  }
}
