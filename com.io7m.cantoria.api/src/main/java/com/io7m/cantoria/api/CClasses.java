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
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Functions over classes.
 */

public final class CClasses
{
  private CClasses()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Determine all superclasses of the given class. The result will not include
   * {@code c}.
   *
   * @param registry The class registry
   * @param c        The current class
   *
   * @return A list of superclasses, most distance ancestor first
   *
   * @throws IOException On I/O errors
   */

  public static List<CClass> superclassesOf(
    final CClassRegistryType registry,
    final CClass c)
    throws IOException
  {
    NullCheck.notNull(registry, "Registry");
    NullCheck.notNull(c, "Class");

    CClass current = c;
    List<CClass> supers = List.empty();

    while (current.node().superName != null) {
      final Tuple2<String, String> pair =
        CClassNames.parseFullyQualifiedDotted(
          CClassNames.toDottedName(current.node().superName));

      final String package_name = pair._1;
      final String class_name = pair._2;

      final Optional<CClass> c_opt =
        registry.findClass(package_name, class_name);

      if (c_opt.isPresent()) {
        current = c_opt.get();
      } else {
        break;
      }
      supers = supers.prepend(current);
    }

    return supers;
  }

  /**
   * Create a class from the given name and class node.
   *
   * @param name   The name
   * @param module The module containing the class
   * @param node   The class node
   *
   * @return A class
   */

  public static CClass classOf(
    final CClassName name,
    final CModuleType module,
    final ClassNode node)
  {
    NullCheck.notNull(name, "Name");
    NullCheck.notNull(module, "Module");
    NullCheck.notNull(node, "Node");

    return CClass.of(
      name,
      node,
      module,
      CClassModifiers.classModifiers(node),
      CClassModifiers.classAccessibility(node),
      node.version);
  }

  /**
   * Parse a class node from the given stream.
   *
   * @param stream The stream
   *
   * @return A class node
   *
   * @throws IOException On I/O or parse errors
   */

  public static ClassNode classNodeFromStream(
    final InputStream stream)
    throws IOException
  {
    final ClassReader reader_new = new ClassReader(stream);
    final ClassNode class_node_new = new ClassNode();
    reader_new.accept(class_node_new, 0);
    return class_node_new;
  }
}
