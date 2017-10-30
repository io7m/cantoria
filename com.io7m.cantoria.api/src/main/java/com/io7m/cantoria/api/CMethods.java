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

import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Functions over methods.
 */

public final class CMethods
{
  private CMethods()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Find all method overloads in the given class with the given name.
   *
   * @param clazz The class
   * @param name  The method name
   *
   * @return A list of methods, if any
   */

  public static List<MethodNode>
  findMethodsWithName(
    final CClass clazz,
    final String name)
  {
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");

    return List.ofAll(
      clazz.node().methods.stream()
        .filter(m -> Objects.equals(m.name, name)));
  }

  /**
   * Find all method overloads in the given class with the given name and type.
   *
   * @param clazz      The class
   * @param name       The method name
   * @param descriptor The method type descriptor
   *
   * @return A list of methods, if any
   *
   * @see MethodNode#desc
   */

  public static Optional<MethodNode>
  findMethodWithNameAndType(
    final CClass clazz,
    final String name,
    final String descriptor)
  {
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");
    NullCheck.notNull(descriptor, "Descriptor");

    return clazz.node().methods.stream()
      .filter(m -> Objects.equals(m.name, name)
        && Objects.equals(descriptor, m.desc))
      .findFirst();
  }

  /**
   * Find all method overloads with the given name in all superclasses of the
   * given class. The results are given in order of most distant ancestor
   * superclass first.
   *
   * @param class_registry A class registry
   * @param clazz          The class
   * @param name           The method name
   *
   * @return A list of methods, if any
   *
   * @throws IOException If the class registry raises I/O errors
   */

  public static List<Tuple2<CClass, List<MethodNode>>>
  findSuperclassMethodsWithName(
    final CClassRegistryType class_registry,
    final CClass clazz,
    final String name)
    throws IOException
  {
    NullCheck.notNull(class_registry, "Class registry");
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");

    return CClasses.superclassesOf(class_registry, clazz)
      .map(c -> Tuple.of(c, findMethodsWithName(c, name)))
      .filter(p -> !p._2.isEmpty());
  }

  /**
   * Find all method overloads with the given name in all superclasses of the
   * given class. The results are given in order of most distant ancestor
   * superclass first.
   *
   * @param class_registry A class registry
   * @param clazz          The class
   * @param name           The method name
   * @param descriptor     The method type descriptor
   *
   * @return A list of methods, if any
   *
   * @throws IOException If the class registry raises I/O errors
   * @see MethodNode#desc
   */

  public static List<Tuple2<CClass, MethodNode>>
  findSuperclassMethodsWithNameAndType(
    final CClassRegistryType class_registry,
    final CClass clazz,
    final String name,
    final String descriptor)
    throws IOException
  {
    NullCheck.notNull(class_registry, "Class registry");
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");
    NullCheck.notNull(descriptor, "Descriptor");

    return CClasses.superclassesOf(class_registry, clazz)
      .map(c -> Tuple.of(c, findMethodWithNameAndType(c, name, descriptor)))
      .filter(p -> p._2.isPresent())
      .map(p -> Tuple.of(p._1, p._2.get()));
  }

  /**
   * Pretty print the given method.
   *
   * @param method The method
   *
   * @return A pretty printed method
   */

  public static String show(
    final CMethod method)
  {
    NullCheck.notNull(method, "Method");

    final StringBuilder sb = new StringBuilder(64);
    final String kw = method.accessibility().keyword();
    sb.append(kw);
    sb.append(kw.isEmpty() ? "" : " ");

    final String modifiers =
      method.modifiers().map(CModifier::keyword)
        .collect(Collectors.joining(" "));
    sb.append(modifiers);
    sb.append(modifiers.isEmpty() ? "" : " ");

    sb.append(method.returnType());
    sb.append(" ");

    sb.append(method.name());
    sb.append("(");
    sb.append(method.parameterTypes().collect(Collectors.joining(",")));
    sb.append(")");

    if (!method.exceptions().isEmpty()) {
      sb.append(" throws ");
      sb.append(method.exceptions().collect(Collectors.joining(",")));
    }

    return sb.toString();
  }

  /**
   * Construct a method from the given method node.
   *
   * @param clazz  The containing class
   * @param method The method
   *
   * @return A method
   */

  public static CMethod method(
    final CClassName clazz,
    final MethodNode method)
  {
    return CMethod.builder()
      .setAccessibility(CMethodModifiers.methodAccessibility(method))
      .setClassName(clazz)
      .setExceptions(List.ofAll(method.exceptions).map(CClassNames::toDottedName))
      .setModifiers(CMethodModifiers.methodModifiers(method))
      .setName(method.name)
      .setNode(method)
      .setParameterTypes(CMethodTypes.parseParameterTypesFromSignature(method.desc))
      .setReturnType(CMethodTypes.parseReturnTypeFromSignature(method.desc))
      .build();
  }
}
