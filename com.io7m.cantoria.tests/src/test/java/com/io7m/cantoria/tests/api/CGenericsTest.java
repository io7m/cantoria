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
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.api.CModules;
import com.io7m.cantoria.api.generics.CGenericClass;
import com.io7m.cantoria.api.generics.CGenerics;
import com.io7m.cantoria.api.generics.CReferenceClass;
import com.io7m.cantoria.api.generics.CReferenceVariable;
import com.io7m.cantoria.api.generics.CTypeArgumentReference;
import com.io7m.cantoria.api.generics.CTypeArgumentWildcard;
import com.io7m.cantoria.api.generics.CTypeBoundClass;
import com.io7m.cantoria.api.generics.CTypeBoundVariable;
import com.io7m.cantoria.api.generics.CTypeParameter;
import com.io7m.cantoria.api.generics.CTypeVariable;
import com.io7m.cantoria.api.generics.CWildcardExtends;
import com.io7m.cantoria.api.generics.CWildcardSuper;
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
    final List<CTypeParameter> params =
      CGenerics.parseClassGenericParameters(er, node.signature);

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

    final CGenericClass object =
      CGenericClass.builder()
        .setName(object_name)
        .build();

    final CGenericClass number =
      CGenericClass.builder()
        .setName(number_name)
        .build();

    final CGenericClass integer =
      CGenericClass.builder()
        .setName(integer_name)
        .build();

    final CReferenceClass object_reference =
      CReferenceClass.of(object);

    final CReferenceClass integer_reference =
      CReferenceClass.of(integer);

    Assertions.assertAll(
      () -> {
        // A
        final CTypeParameter a = params.get(0);

        final CTypeBoundClass object_bound =
          CTypeBoundClass.builder()
            .setClassType(object)
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("A")
            .setBound(object_bound)
            .build(),
          a);
      },

      () -> {
        // B extends A
        final CTypeParameter b = params.get(1);

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("B")
            .setBound(CTypeBoundVariable.of(CTypeVariable.of("A")))
            .build(),
          b);
      },

      () -> {
        // C extends java.util.List<?>
        final CTypeParameter c = params.get(2);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentWildcard.of(
                    CWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("C")
            .setBound(list_bound)
            .build(),
          c);
      },

      () -> {
        // C1 extends java.util.List<java.lang.Object>
        final CTypeParameter c1 = params.get(3);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(CTypeArgumentReference.of(object_reference))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("C1")
            .setBound(list_bound)
            .build(),
          c1);
      },

      () -> {
        // D extends java.util.ArrayList<?>
        final CTypeParameter d = params.get(4);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CTypeArgumentWildcard.of(
                    CWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("D")
            .setBound(list_bound)
            .build(),
          d);
      },

      () -> {
        // D extends java.util.ArrayList<?>
        final CTypeParameter d = params.get(4);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CTypeArgumentWildcard.of(
                    CWildcardExtends.of(object_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("D")
            .setBound(list_bound)
            .build(),
          d);
      },

      () -> {
        // E extends java.util.List<A>
        final CTypeParameter e = params.get(5);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentReference.of(
                    CReferenceVariable.of(
                      CTypeVariable.of("A"))))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("E")
            .setBound(list_bound)
            .build(),
          e);
      },

      () -> {
        // F extends java.util.ArrayList<A>
        final CTypeParameter f = params.get(6);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CTypeArgumentReference.of(
                    CReferenceVariable.of(
                      CTypeVariable.of("A"))))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("F")
            .setBound(list_bound)
            .build(),
          f);
      },

      () -> {
        // G extends java.util.List<Comparable<?>>
        final CTypeParameter g = params.get(7);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentReference.of(CReferenceClass.of(
                    CGenericClass.builder()
                      .setName(comparable_name)
                      .addArguments(CTypeArgumentWildcard.of(
                        CWildcardExtends.of(object_reference)))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("G")
            .setBound(list_bound)
            .build(),
          g);
      },

      () -> {
        // G1 extends java.util.List<? extends Comparable<?>>
        final CTypeParameter g = params.get(8);

        final CWildcardExtends w_extends =
          CWildcardExtends.of(
            CReferenceClass.of(
              CGenericClass.builder()
                .setName(comparable_name)
                .addArguments(CTypeArgumentWildcard.of(
                  CWildcardExtends.of(object_reference)))
                .build()));

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(CTypeArgumentWildcard.of(w_extends))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("G1")
            .setBound(list_bound)
            .build(),
          g);
      },

      () -> {
        // extends java.util.ArrayList<Comparable<?>>
        final CTypeParameter h = params.get(9);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CTypeArgumentReference.of(CReferenceClass.of(
                    CGenericClass.builder()
                      .setName(comparable_name)
                      .addArguments(CTypeArgumentWildcard.of(
                        CWildcardExtends.of(object_reference)))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("H")
            .setBound(list_bound)
            .build(),
          h);
      },

      () -> {
        // I extends java.util.List<Comparable<A>>
        final CTypeParameter i = params.get(10);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentReference.of(CReferenceClass.of(
                    CGenericClass.builder()
                      .setName(comparable_name)
                      .addArguments(CTypeArgumentReference.of(
                        CReferenceVariable.of(CTypeVariable.of("A"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("I")
            .setBound(list_bound)
            .build(),
          i);
      },

      () -> {
        // J extends java.util.ArrayList<Comparable<A>>
        final CTypeParameter j = params.get(11);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(arraylist_name)
                .addArguments(
                  CTypeArgumentReference.of(CReferenceClass.of(
                    CGenericClass.builder()
                      .setName(comparable_name)
                      .addArguments(CTypeArgumentReference.of(
                        CReferenceVariable.of(CTypeVariable.of("A"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("J")
            .setBound(list_bound)
            .build(),
          j);
      },

      () -> {
        // K extends java.util.List<java.util.function.Function<A, B>>
        final CTypeParameter k = params.get(12);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentReference.of(CReferenceClass.of(
                    CGenericClass.builder()
                      .setName(function_name)
                      .addArguments(CTypeArgumentReference.of(
                        CReferenceVariable.of(CTypeVariable.of("A"))))
                      .addArguments(CTypeArgumentReference.of(
                        CReferenceVariable.of(CTypeVariable.of("B"))))
                      .build())))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("K")
            .setBound(list_bound)
            .build(),
          k);
      },

      () -> {
        // L extends java.util.List<? super Integer>
        final CTypeParameter l = params.get(13);

        final CTypeBoundClass list_bound =
          CTypeBoundClass.builder()
            .setClassType(
              CGenericClass.builder()
                .setName(list_name)
                .addArguments(
                  CTypeArgumentWildcard.of(
                    CWildcardSuper.of(integer_reference)))
                .build())
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("L")
            .setBound(list_bound)
            .build(),
          l);
      },

      () -> {
        // M extends Number & java.io.Serializable
        final CTypeParameter m = params.get(14);

        final CTypeBoundClass object_bound =
          CTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(CGenericClass.of(serial_name, List.empty()))
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("M")
            .setBound(object_bound)
            .build(),
          m);
      },

      () -> {
        // N extends Number & java.util.function.Consumer<Integer>
        final CTypeParameter n = params.get(15);

        final CTypeBoundClass object_bound =
          CTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(CGenericClass.of(
              consumer_name,
              List.of(CTypeArgumentReference.of(integer_reference))))
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
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
    final List<CTypeParameter> params =
      CGenerics.parseClassGenericParameters(er, node.signature);

    params.forEach(par -> LOG.debug("{}", par.toJava()));

    Assertions.assertEquals(1, params.size());

    final CClassName object_name =
      CClassName.of("java.base", "java.lang", "Object");
    final CClassName serial_name =
      CClassName.of("java.base", "java.io", "Serializable");
    final CClassName number_name =
      CClassName.of("java.base", "java.lang", "Number");

    final CGenericClass number =
      CGenericClass.builder()
        .setName(number_name)
        .build();

    final CGenericClass serializable =
      CGenericClass.builder()
        .setName(serial_name)
        .build();

    Assertions.assertAll(
      () -> {
        // M extends Number & java.io.Serializable
        final CTypeParameter m = params.get(0);

        final CTypeBoundClass bound =
          CTypeBoundClass.builder()
            .setClassType(number)
            .addIntersections(serializable)
            .build();

        Assertions.assertEquals(
          CTypeParameter.builder()
            .setName("M")
            .setBound(bound)
            .build(),
          m);
      }
    );
  }
}
