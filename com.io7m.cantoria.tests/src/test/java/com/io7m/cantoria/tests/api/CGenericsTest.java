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

package com.io7m.cantoria.tests.api;

import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CClassRegistry;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CClasses;
import com.io7m.cantoria.api.CGReferenceClass;
import com.io7m.cantoria.api.CGReferenceVariable;
import com.io7m.cantoria.api.CGTypeArgumentReference;
import com.io7m.cantoria.api.CGTypeArgumentWildcard;
import com.io7m.cantoria.api.CGTypeBoundClass;
import com.io7m.cantoria.api.CGTypeBoundVariable;
import com.io7m.cantoria.api.CGTypeClass;
import com.io7m.cantoria.api.CGTypeParameter;
import com.io7m.cantoria.api.CGTypeVariable;
import com.io7m.cantoria.api.CGWildcardExtends;
import com.io7m.cantoria.api.CGWildcardSuper;
import com.io7m.cantoria.api.CGenericsParsing;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.api.CModules;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.NoSuchFileException;

public final class CGenericsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CGenericsTest.class);

  private static ClassNode classOf(
    final String name)
    throws IOException
  {
    final String path = "/com/io7m/cantoria/tests/api/" + name;
    final URL url = CGenericsTest.class.getResource(path);
    if (url == null) {
      throw new NoSuchFileException(path);
    }

    try (InputStream stream = url.openStream()) {
      return CClasses.classNodeFromStream(stream);
    }
  }

  private static CClassRegistryType classRegistry(
    final CModuleType... modules)
    throws IOException
  {
    return CClassRegistry.create(
      List.of(modules)
        .prepend(CModules.openPlatformModule("java.base"))
        .map(CModuleWeaklyCaching::wrap));
  }

  @Test
  public void testParseGenerics()
    throws Exception
  {
    final CClassRegistryType er = classRegistry();

    final ClassNode node = classOf("Generics0.class");
    final List<CGTypeParameter> params =
      CGenericsParsing.parseClassGenericParameters(er, node.signature);

    params.forEach(par -> LOG.debug("{}", par.toJava()));

    Assertions.assertEquals(16, params.size());

    final CClassName object_name =
      CClassName.of("java.base", "java.lang", "Object");

    final CClassName integer_name =
      CClassName.of("java.base", "java.lang", "Integer");

    final CClassName list_name =
      CClassName.of("java.base", "java.util", "List");

    final CClassName arraylist_name =
      CClassName.of("java.base", "java.util", "ArrayList");

    final CClassName comparable_name =
      CClassName.of("java.base", "java.lang", "Comparable");

    final CClassName number_name =
      CClassName.of("java.base", "java.lang", "Number");

    final CClassName serial_name =
      CClassName.of("java.base", "java.io", "Serializable");

    final CClassName function_name =
      CClassName.of("java.base", "java.util.function", "Function");

    final CClassName consumer_name =
      CClassName.of("java.base", "java.util.function", "Consumer");

    final CGTypeClass object =
      CGTypeClass.builder()
        .setName(object_name)
        .build();

    final CGTypeClass number =
      CGTypeClass.builder()
        .setName(number_name)
        .build();

    final CGTypeClass integer =
      CGTypeClass.builder()
        .setName(integer_name)
        .build();

    final CGReferenceClass object_reference =
      CGReferenceClass.of(object);

    final CGReferenceClass integer_reference =
      CGReferenceClass.of(integer);

    Assertions.assertAll(
      () -> {
        // A
        final CGTypeParameter a = params.get(0);

        final CGTypeBoundClass object_bound =
          CGTypeBoundClass.builder()
            .setClassType(object)
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("A")
            .setBound(object_bound)
            .build(),
          a);
      },

      () -> {
        // B extends A
        final CGTypeParameter b = params.get(1);

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("B")
            .setBound(CGTypeBoundVariable.of(CGTypeVariable.of("A")))
            .build(),
          b);
      },

      () -> {
        // C extends java.util.List<?>
        final CGTypeParameter c = params.get(2);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentWildcard.of(
                    CGWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("C")
            .setBound(list_bound)
            .build(),
          c);
      },

      () -> {
        // C1 extends java.util.List<java.lang.Object>
        final CGTypeParameter c1 = params.get(3);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(CGTypeArgumentReference.of(object_reference))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("C1")
            .setBound(list_bound)
            .build(),
          c1);
      },

      () -> {
        // D extends java.util.ArrayList<?>
        final CGTypeParameter d = params.get(4);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CGTypeArgumentWildcard.of(
                    CGWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("D")
            .setBound(list_bound)
            .build(),
          d);
      },

      () -> {
        // D extends java.util.ArrayList<?>
        final CGTypeParameter d = params.get(4);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CGTypeArgumentWildcard.of(
                    CGWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("D")
            .setBound(list_bound)
            .build(),
          d);
      },

      () -> {
        // E extends java.util.List<A>
        final CGTypeParameter e = params.get(5);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentReference.of(
                    CGReferenceVariable.of(
                      CGTypeVariable.of("A"))))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("E")
            .setBound(list_bound)
            .build(),
          e);
      },

      () -> {
        // F extends java.util.ArrayList<A>
        final CGTypeParameter f = params.get(6);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CGTypeArgumentReference.of(
                    CGReferenceVariable.of(
                      CGTypeVariable.of("A"))))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("F")
            .setBound(list_bound)
            .build(),
          f);
      },

      () -> {
        // G extends java.util.List<Comparable<?>>
        final CGTypeParameter g = params.get(7);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentReference.of(CGReferenceClass.of(
                    CGTypeClass.builder()
                      .setName(comparable_name)
                      .addArguments(CGTypeArgumentWildcard.of(
                        CGWildcardExtends.of(object_reference)))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("G")
            .setBound(list_bound)
            .build(),
          g);
      },

      () -> {
        // G1 extends java.util.List<? extends Comparable<?>>
        final CGTypeParameter g = params.get(8);

        final CGWildcardExtends w_extends =
          CGWildcardExtends.of(
            CGReferenceClass.of(
              CGTypeClass.builder()
                .setName(comparable_name)
                .addArguments(CGTypeArgumentWildcard.of(
                  CGWildcardExtends.of(object_reference)))
                .build()));

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(CGTypeArgumentWildcard.of(w_extends))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("G1")
            .setBound(list_bound)
            .build(),
          g);
      },

      () -> {
        // extends java.util.ArrayList<Comparable<?>>
        final CGTypeParameter h = params.get(9);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CGTypeArgumentReference.of(CGReferenceClass.of(
                    CGTypeClass.builder()
                      .setName(comparable_name)
                      .addArguments(CGTypeArgumentWildcard.of(
                        CGWildcardExtends.of(object_reference)))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("H")
            .setBound(list_bound)
            .build(),
          h);
      },

      () -> {
        // I extends java.util.List<Comparable<A>>
        final CGTypeParameter i = params.get(10);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentReference.of(CGReferenceClass.of(
                    CGTypeClass.builder()
                      .setName(comparable_name)
                      .addArguments(CGTypeArgumentReference.of(
                        CGReferenceVariable.of(CGTypeVariable.of("A"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("I")
            .setBound(list_bound)
            .build(),
          i);
      },

      () -> {
        // J extends java.util.ArrayList<Comparable<A>>
        final CGTypeParameter j = params.get(11);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CGTypeArgumentReference.of(CGReferenceClass.of(
                    CGTypeClass.builder()
                      .setName(comparable_name)
                      .addArguments(CGTypeArgumentReference.of(
                        CGReferenceVariable.of(CGTypeVariable.of("A"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("J")
            .setBound(list_bound)
            .build(),
          j);
      },

      () -> {
        // K extends java.util.List<java.util.function.Function<A, B>>
        final CGTypeParameter k = params.get(12);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentReference.of(CGReferenceClass.of(
                    CGTypeClass.builder()
                      .setName(function_name)
                      .addArguments(CGTypeArgumentReference.of(
                        CGReferenceVariable.of(CGTypeVariable.of("A"))))
                      .addArguments(CGTypeArgumentReference.of(
                        CGReferenceVariable.of(CGTypeVariable.of("B"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("K")
            .setBound(list_bound)
            .build(),
          k);
      },

      () -> {
        // L extends java.util.List<? super Integer>
        final CGTypeParameter l = params.get(13);

        final CGTypeBoundClass list_bound =
          CGTypeBoundClass.builder()
            .setClassType(
              CGTypeClass.builder()
                .setName(list_name)
                .addArguments(
                  CGTypeArgumentWildcard.of(
                    CGWildcardSuper.of(integer_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("L")
            .setBound(list_bound)
            .build(),
          l);
      },

      () -> {
        // M extends Number & java.io.Serializable
        final CGTypeParameter m = params.get(14);

        final CGTypeBoundClass object_bound =
          CGTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(CGTypeClass.of(serial_name, List.empty()))
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("M")
            .setBound(object_bound)
            .build(),
          m);
      },

      () -> {
        // N extends Number & java.util.function.Consumer<Integer>
        final CGTypeParameter n = params.get(15);

        final CGTypeBoundClass object_bound =
          CGTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(CGTypeClass.of(
              consumer_name,
              List.of(CGTypeArgumentReference.of(integer_reference))))
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("N")
            .setBound(object_bound)
            .build(),
          n);
      }
    );
  }

  @Test
  public void testParseGenericsIntersection()
    throws Exception
  {
    final CClassRegistryType er = classRegistry();

    final ClassNode node = classOf("GenericsIntersection.class");
    final List<CGTypeParameter> params =
      CGenericsParsing.parseClassGenericParameters(er, node.signature);

    params.forEach(par -> LOG.debug("{}", par.toJava()));

    Assertions.assertEquals(1, params.size());

    final CClassName object_name =
      CClassName.of("java.base", "java.lang", "Object");
    final CClassName serial_name =
      CClassName.of("java.base", "java.io", "Serializable");
    final CClassName number_name =
      CClassName.of("java.base", "java.lang", "Number");

    final CGTypeClass number =
      CGTypeClass.builder()
        .setName(number_name)
        .build();

    final CGTypeClass serializable =
      CGTypeClass.builder()
        .setName(serial_name)
        .build();

    Assertions.assertAll(
      () -> {
        // M extends Number & java.io.Serializable
        final CGTypeParameter m = params.get(0);

        final CGTypeBoundClass bound =
          CGTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(serializable)
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("M")
            .setBound(bound)
            .build(),
          m);
      }
    );
  }

  @Test
  public void testParseGenericsArrays()
    throws Exception
  {
    final CClassRegistryType er = classRegistry();

    final ClassNode node = classOf("GenericsArrays.class");
    final List<CGTypeParameter> params =
      CGenericsParsing.parseClassGenericParameters(er, node.signature);

    params.forEach(par -> LOG.debug("{}", par.toJava()));

    Assertions.assertEquals(3, params.size());

    final CClassName object_name =
      CClassName.of("java.base", "java.lang", "Object");
    final CClassName serial_name =
      CClassName.of("java.base", "java.io", "Serializable");
    final CClassName number_name =
      CClassName.of("java.base", "java.lang", "Number");

    final CGTypeClass number =
      CGTypeClass.builder()
        .setName(number_name)
        .build();

    final CGTypeClass serializable =
      CGTypeClass.builder()
        .setName(serial_name)
        .build();

    Assertions.assertAll(
      () -> {
        // M extends Number & java.io.Serializable
        final CGTypeParameter m = params.get(0);

        final CGTypeBoundClass bound =
          CGTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(serializable)
            .build();

        Assertions.assertEquals(
          CGTypeParameter.builder()
            .setName("M")
            .setBound(bound)
            .build(),
          m);
      }
    );
  }
}
