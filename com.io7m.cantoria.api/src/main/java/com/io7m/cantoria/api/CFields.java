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
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Functions over fields.
 */

public final class CFields
{
  private CFields()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Find the field in the given class with the given name.
   *
   * @param clazz The class
   * @param name  The field name
   *
   * @return The field, if any
   */

  public static Optional<FieldNode>
  findFieldsWithName(
    final CClass clazz,
    final String name)
  {
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");

    return clazz.node().fields.stream()
      .filter(m -> Objects.equals(m.name, name))
      .findAny();
  }

  /**
   * Find the field in the given class with the given name and type.
   *
   * @param clazz The class
   * @param name  The field name
   * @param type  The  type descriptor
   *
   * @return The field, if any
   *
   * @see FieldNode#desc
   */

  public static Optional<FieldNode>
  findFieldsWithNameAndType(
    final CClass clazz,
    final String name,
    final String type)
  {
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");
    NullCheck.notNull(type, "Type");

    return clazz.node().fields.stream()
      .filter(m -> Objects.equals(m.name, name) && Objects.equals(m.desc, type))
      .findAny();
  }

  /**
   * Find all field overloads with the given name in all superclasses of the
   * given class. The results are given in order of most distant ancestor
   * superclass first.
   *
   * @param class_registry A class registry
   * @param clazz          The class
   * @param name           The field name
   *
   * @return A list of fields, if any
   *
   * @throws IOException If the class registry raises I/O errors
   */

  public static List<Tuple2<CClass, FieldNode>>
  findSuperclassFieldsWithName(
    final CClassRegistryType class_registry,
    final CClass clazz,
    final String name)
    throws IOException
  {
    NullCheck.notNull(class_registry, "Class registry");
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");

    return CClasses.superclassesOf(class_registry, clazz)
      .map(c -> Tuple.of(c, findFieldsWithName(c, name)))
      .filter(p -> p._2.isPresent())
      .map(p -> Tuple.of(p._1, p._2.get()));
  }

  /**
   * Find all field overloads with the given name in all superclasses of the
   * given class. The results are given in order of most distant ancestor
   * superclass first.
   *
   * @param class_registry A class registry
   * @param clazz          The class
   * @param name           The field name
   * @param type           The field type descriptor
   *
   * @return A list of fields, if any
   *
   * @throws IOException If the class registry raises I/O errors
   * @see FieldNode#desc
   */

  public static List<Tuple2<CClass, FieldNode>>
  findSuperclassFieldsWithNameAndType(
    final CClassRegistryType class_registry,
    final CClass clazz,
    final String name,
    final String type)
    throws IOException
  {
    NullCheck.notNull(class_registry, "Class registry");
    NullCheck.notNull(clazz, "Clazz");
    NullCheck.notNull(name, "Name");
    NullCheck.notNull(type, "Type");

    return CClasses.superclassesOf(class_registry, clazz)
      .map(c -> Tuple.of(c, findFieldsWithNameAndType(c, name, type)))
      .filter(p -> p._2.isPresent())
      .map(p -> Tuple.of(p._1, p._2.get()));
  }

  /**
   * Construct a field from the given field node.
   *
   * @param clazz The containing class
   * @param field The field
   *
   * @return A field
   */

  public static CField field(
    final CClassName clazz,
    final FieldNode field)
  {
    NullCheck.notNull(clazz, "Class");
    NullCheck.notNull(field, "Field");

    return CField.builder()
      .setAccessibility(CFieldModifiers.fieldAccessibility(field))
      .setClassName(clazz)
      .setModifiers(CFieldModifiers.fieldModifiers(field))
      .setNode(field)
      .setName(field.name)
      .setType(CFieldTypes.parseFieldType(field.desc))
      .build();
  }

  /**
   * Pretty print the given field.
   *
   * @param field The field
   *
   * @return A pretty printed field
   */

  public static String show(
    final CField field)
  {
    NullCheck.notNull(field, "Field");

    final StringBuilder sb = new StringBuilder(64);
    final String kw = field.accessibility().keyword();
    sb.append(kw);
    sb.append(kw.isEmpty() ? "" : " ");

    final String modifiers =
      field.modifiers().map(CModifier::keyword)
        .collect(Collectors.joining(" "));
    sb.append(modifiers);
    sb.append(modifiers.isEmpty() ? "" : " ");

    sb.append(field.type());
    sb.append(" ");
    sb.append(field.name());
    return sb.toString();
  }
}
