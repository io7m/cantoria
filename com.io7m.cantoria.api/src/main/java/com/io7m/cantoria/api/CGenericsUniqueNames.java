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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Functions to transform type signatures into unique names.
 */

public final class CGenericsUniqueNames
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CGenericsUniqueNames.class);

  private CGenericsUniqueNames()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Make all type variables in the given class signature unique.
   *
   * @param names     A mapping of old names to new names
   * @param signature A class signature
   *
   * @return A class signature with unique names
   */

  public static CGClassSignature uniqueClassSignature(
    final UniqueNamesType names,
    final CGClassSignature signature)
  {
    Objects.requireNonNull(names, "Names");
    Objects.requireNonNull(signature, "Signature");

    return CGClassSignature.of(
      uniqueTypeParameters(names, signature.parameters()),
      uniqueBindingsClassTypeSignature(names, signature.superclass()),
      signature.interfaces()
        .map(i -> uniqueBindingsClassTypeSignature(names, i)));
  }

  /**
   * Make all type variables in the given type parameters unique.
   *
   * @param names A mapping of old names to new names
   * @param a     A list of type variables
   *
   * @return A list of type variables with unique names
   */

  public static List<CGTypeParameter> uniqueTypeParameters(
    final UniqueNamesType names,
    final List<CGTypeParameter> a)
  {
    Objects.requireNonNull(names, "Names");
    Objects.requireNonNull(a, "Parameters");

    return a.map(p -> uniqueBindingsTypeParameter(names, p));
  }

  /**
   * @return A new empty set of unique names
   */

  public static UniqueNamesType emptyUniqueNames()
  {
    return new Names();
  }

  private static CGTypeParameter uniqueBindingsTypeParameter(
    final UniqueNamesType names,
    final CGTypeParameter p)
  {
    final String id = names.freshOrExisting(p.name());

    final CGenericsType.CGFieldTypeSignatureType type =
      uniqueBindingsType(names, p.type());
    final List<CGenericsType.CGFieldTypeSignatureType> intersections =
      p.intersections().map(q -> uniqueBindingsType(names, q));

    return CGTypeParameter.of(id, type, intersections);
  }

  private static CGenericsType.CGFieldTypeSignatureType uniqueBindingsType(
    final UniqueNamesType names,
    final CGenericsType.CGFieldTypeSignatureType type)
  {
    switch (type.kind()) {
      case FIELD_TYPE_SIGNATURE_CLASS: {
        return uniqueBindingsTypeClass(
          names, (CGFieldTypeSignatureClass) type);
      }
      case FIELD_TYPE_SIGNATURE_ARRAY: {
        return uniqueBindingsTypeArray(
          names, (CGFieldTypeSignatureArray) type);
      }
      case FIELD_TYPE_SIGNATURE_VARIABLE: {
        return uniqueBindingsTypeVariable(
          names, (CGFieldTypeSignatureVariable) type);
      }
    }

    throw new UnreachableCodeException();
  }

  private static CGFieldTypeSignatureClass uniqueBindingsTypeClass(
    final UniqueNamesType names,
    final CGFieldTypeSignatureClass type)
  {
    return CGFieldTypeSignatureClass.of(
      uniqueBindingsClassTypeSignature(names, type.classType()));
  }

  private static CGClassTypeSignature uniqueBindingsClassTypeSignature(
    final UniqueNamesType names,
    final CGClassTypeSignature signature)
  {
    final List<CGenericsType.CGTypeArgumentType> unique_args =
      signature.typeArguments()
        .arguments()
        .map(a -> uniqueBindingsTypeArgument(names, a));

    final List<CGClassTypeSignature> unique_inner =
      signature.innerTypes()
        .map(i -> uniqueBindingsClassTypeSignature(names, i));

    return CGClassTypeSignature.of(
      signature.typeName(), CGTypeArguments.of(unique_args), unique_inner);
  }

  private static CGenericsType.CGTypeArgumentType uniqueBindingsTypeArgument(
    final UniqueNamesType names,
    final CGenericsType.CGTypeArgumentType a)
  {
    switch (a.kind()) {
      case ANY:
        return a;
      case EXTENDS:
        return uniqueBindingsTypeArgumentExtends(
          names, (CGTypeArgumentExtends) a);
      case EXACTLY:
        return uniqueBindingsTypeArgumentExactly(
          names, (CGTypeArgumentExactly) a);
      case SUPER:
        return uniqueBindingsTypeArgumentSuper(
          names, (CGTypeArgumentSuper) a);
    }

    throw new UnreachableCodeException();
  }

  private static CGTypeArgumentExtends uniqueBindingsTypeArgumentExtends(
    final UniqueNamesType names,
    final CGTypeArgumentExtends a)
  {
    return CGTypeArgumentExtends.of(
      uniqueBindingsType(names, a.fieldSignature()));
  }

  private static CGTypeArgumentExactly uniqueBindingsTypeArgumentExactly(
    final UniqueNamesType names,
    final CGTypeArgumentExactly a)
  {
    return CGTypeArgumentExactly.of(
      uniqueBindingsType(names, a.fieldSignature()));
  }

  private static CGTypeArgumentSuper uniqueBindingsTypeArgumentSuper(
    final UniqueNamesType names,
    final CGTypeArgumentSuper a)
  {
    return CGTypeArgumentSuper.of(
      uniqueBindingsType(names, a.fieldSignature()));
  }

  private static CGFieldTypeSignatureVariable uniqueBindingsTypeVariable(
    final UniqueNamesType names,
    final CGFieldTypeSignatureVariable type)
  {
    final CGFieldTypeSignatureVariable var = type;
    final String name = var.variable().name();
    return CGFieldTypeSignatureVariable.of(
      CGTypeVariable.of(names.existing(name)));
  }

  private static CGFieldTypeSignatureArray uniqueBindingsTypeArray(
    final UniqueNamesType names,
    final CGFieldTypeSignatureArray type)
  {
    final CGFieldTypeSignatureArray arr = type;
    final CGenericsType.CGTypeSignatureType arr_t = arr.type();
    switch (arr_t.kind()) {
      case FIELD: {
        final CGTypeSignatureField tf = (CGTypeSignatureField) arr_t;
        return CGFieldTypeSignatureArray.of(
          CGTypeSignatureField.of(uniqueBindingsType(names, tf.field())),
          arr.dimensions());
      }
      case PRIMITIVE: {
        return arr;
      }
    }

    throw new UnreachableCodeException();
  }

  /**
   * An interface to track unique name bindings.
   */

  public interface UniqueNamesType
  {
    /**
     * Generate a fresh variable name and associate it with the given name.
     *
     * @param name The initial name
     *
     * @return A fresh name
     */

    String fresh(
      String name);

    /**
     * @param name The initial name
     *
     * @return The existing (fresh) name associated with {@code name}, or a
     * fresh name if no binding exists
     */

    String freshOrExisting(
      String name);

    /**
     * @param name The initial name
     *
     * @return The existing (fresh) name associated with {@code name}
     */

    String existing(
      String name);
  }

  private static final class Names implements UniqueNamesType
  {
    private final AtomicInteger id;
    private final HashMap<String, String> old_to_fresh;

    Names()
    {
      this.id = new AtomicInteger();
      this.old_to_fresh = new HashMap<>(32);
    }

    @Override
    public String fresh(
      final String name)
    {
      Preconditions.checkPrecondition(
        name,
        !this.old_to_fresh.containsKey(name),
        s -> "Name must not be reused");

      final String new_name = ":" + this.id.getAndIncrement();
      this.old_to_fresh.put(name, new_name);

      LOG.trace("fresh: {} -> {}", name, new_name);
      return new_name;
    }

    @Override
    public String freshOrExisting(
      final String name)
    {
      if (this.old_to_fresh.containsKey(name)) {
        return this.existing(name);
      }
      return this.fresh(name);
    }

    @Override
    public String existing(
      final String name)
    {
      Preconditions.checkPrecondition(
        name,
        this.old_to_fresh.containsKey(name),
        s -> "Name must be present");

      return this.old_to_fresh.get(name);
    }
  }
}
