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

import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGClassTypeSignature;
import com.io7m.cantoria.api.CGFieldTypeSignatureArray;
import com.io7m.cantoria.api.CGFieldTypeSignatureClass;
import com.io7m.cantoria.api.CGFieldTypeSignatureVariable;
import com.io7m.cantoria.api.CGTypeArgumentAny;
import com.io7m.cantoria.api.CGTypeArgumentExactly;
import com.io7m.cantoria.api.CGTypeArgumentExtends;
import com.io7m.cantoria.api.CGTypeArgumentSuper;
import com.io7m.cantoria.api.CGTypeParameter;
import com.io7m.cantoria.api.CGTypeSignatureField;
import com.io7m.cantoria.api.CGTypeSignaturePrimitive;
import com.io7m.cantoria.api.CGenericsParsing;
import com.io7m.cantoria.api.CGenericsType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CGenericsParsingTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CGenericsParsingTest.class);

  @Test
  public void testParseClassTypeSignature0()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<TE;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals("extends java.util.List<E>", t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 =
          (CGTypeArgumentExactly) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureVariable t0v =
          (CGFieldTypeSignatureVariable) t0.fieldSignature();

        Assertions.assertEquals("E", t0v.variable().name());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature1()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<*>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals("extends java.util.List<?>", t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentAny t0 =
          (CGTypeArgumentAny) sc.typeArguments().arguments().get(0);
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature2()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<+Ljava/lang/Number;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "extends java.util.List<? extends java.lang.Number>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExtends t0 =
          (CGTypeArgumentExtends) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureClass t0v =
          (CGFieldTypeSignatureClass) t0.fieldSignature();

        final CGClassTypeSignature pcc = t0v.classType();
        Assertions.assertEquals("java.lang.Number", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(0, pcc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature3()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<-Ljava/lang/Integer;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "extends java.util.List<? super java.lang.Integer>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentSuper t0 =
          (CGTypeArgumentSuper) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureClass t0v =
          (CGFieldTypeSignatureClass) t0.fieldSignature();

        final CGClassTypeSignature pcc = t0v.classType();
        Assertions.assertEquals("java.lang.Integer", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(0, pcc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature4()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<[Ljava/util/List<Ljava/lang/String;>;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "extends java.util.List<java.util.List<java.lang.String>[]>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 =
          (CGTypeArgumentExactly) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureArray t0v =
          (CGFieldTypeSignatureArray) t0.fieldSignature();

        Assertions.assertEquals(1, t0v.dimensions());

        final CGTypeSignatureField t0vc =
          (CGTypeSignatureField) t0v.type();
        final CGFieldTypeSignatureClass t0vcc =
          (CGFieldTypeSignatureClass) t0vc.field();

        final CGClassTypeSignature pcc = t0vcc.classType();
        Assertions.assertEquals("java.util.List", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(1, pcc.typeArguments().arguments().size());

        final CGTypeArgumentExactly ta =
          (CGTypeArgumentExactly) pcc.typeArguments().arguments().get(0);

        final CGFieldTypeSignatureClass ta_sig =
          (CGFieldTypeSignatureClass) ta.fieldSignature();
        final CGClassTypeSignature ta_sig_class =
          ta_sig.classType();

        Assertions.assertEquals("java.lang.String", ta_sig_class.typeName());
        Assertions.assertEquals(0, ta_sig_class.innerTypes().size());
        Assertions.assertEquals(
          0,
          ta_sig_class.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature5()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<[[Ljava/util/List<Ljava/lang/String;>;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "extends java.util.List<java.util.List<java.lang.String>[][]>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 =
          (CGTypeArgumentExactly) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureArray t0v =
          (CGFieldTypeSignatureArray) t0.fieldSignature();

        Assertions.assertEquals(2, t0v.dimensions());

        final CGTypeSignatureField t0vc =
          (CGTypeSignatureField) t0v.type();
        final CGFieldTypeSignatureClass t0vcc =
          (CGFieldTypeSignatureClass) t0vc.field();

        final CGClassTypeSignature pcc = t0vcc.classType();
        Assertions.assertEquals("java.util.List", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(1, pcc.typeArguments().arguments().size());

        final CGTypeArgumentExactly ta =
          (CGTypeArgumentExactly) pcc.typeArguments().arguments().get(0);

        final CGFieldTypeSignatureClass ta_sig =
          (CGFieldTypeSignatureClass) ta.fieldSignature();
        final CGClassTypeSignature ta_sig_class =
          ta_sig.classType();

        Assertions.assertEquals("java.lang.String", ta_sig_class.typeName());
        Assertions.assertEquals(0, ta_sig_class.innerTypes().size());
        Assertions.assertEquals(
          0,
          ta_sig_class.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature6()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/HashMap<TK;TV;>.HashIterator<TK;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());


  }

  @Test
  public void testParseClassTypeSignature7()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "Ljava/util/List<[[[I>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "extends java.util.List<int[][][]>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.util.List", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 =
          (CGTypeArgumentExactly) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureArray t0a =
          (CGFieldTypeSignatureArray) t0.fieldSignature();

        Assertions.assertEquals(3, t0a.dimensions());
        Assertions.assertEquals(
          CGTypeSignaturePrimitive.of(CGenericsType.Primitive.INTEGER),
          t0a.type());
      },

      () -> {
        Assertions.assertEquals(0, t.parameters().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature8()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<A:Ljava/lang/Object;>LC<TA;>;Ljava/util/function/Consumer<TA;>;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<A extends java.lang.Object> extends C<A> implements java.util.function.Consumer<A>",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(1, t.interfaces().size());

        final CGClassTypeSignature sc = t.interfaces().get(0);
        Assertions.assertEquals("java.util.function.Consumer", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 =
          (CGTypeArgumentExactly) sc.typeArguments().arguments().get(0);
        final CGFieldTypeSignatureVariable t0v =
          (CGFieldTypeSignatureVariable) t0.fieldSignature();

        Assertions.assertEquals("A", t0v.variable().name());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("C", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(1, sc.typeArguments().arguments().size());

        final CGTypeArgumentExactly t0 = (CGTypeArgumentExactly) sc.typeArguments().arguments().get(
          0);
        final CGFieldTypeSignatureVariable pc = (CGFieldTypeSignatureVariable) t0.fieldSignature();
        Assertions.assertEquals("A", pc.variable().name());
      },

      () -> {
        Assertions.assertEquals(1, t.parameters().size());
        final CGTypeParameter p0 = t.parameters().get(0);
        Assertions.assertEquals("A", p0.name());
        Assertions.assertEquals(0, p0.intersections().size());

        final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
        final CGClassTypeSignature pcc = pc.classType();
        Assertions.assertEquals("java.lang.Object", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(0, pcc.typeArguments().arguments().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature9()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<T:LA<Ljava/lang/Number;>.B<Ljava/lang/Integer;>.C<Ljava/lang/Double;>.D<Ljava/lang/Void;>;>Ljava/lang/Object;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<T extends A<java.lang.Number>.B<java.lang.Integer>.C<java.lang.Double>.D<java.lang.Void>> extends java.lang.Object",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.lang.Object", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(0, sc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(1, t.parameters().size());
        final CGTypeParameter p0 = t.parameters().get(0);
        Assertions.assertEquals("T", p0.name());
        Assertions.assertEquals(0, p0.intersections().size());

        {
          final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
          final CGClassTypeSignature pcc = pc.classType();
          Assertions.assertEquals("A", pcc.typeName());
          Assertions.assertEquals(3, pcc.innerTypes().size());
          Assertions.assertEquals(1, pcc.typeArguments().arguments().size());

          {
            final CGTypeArgumentExactly ta =
              (CGTypeArgumentExactly) pcc.typeArguments().arguments().get(0);
            final CGFieldTypeSignatureClass ta_fs =
              (CGFieldTypeSignatureClass) ta.fieldSignature();
            final CGClassTypeSignature ta_cl =
              ta_fs.classType();

            Assertions.assertEquals("java.lang.Number", ta_cl.typeName());
            Assertions.assertEquals(0, ta_cl.innerTypes().size());
            Assertions.assertEquals(
              0,
              ta_cl.typeArguments().arguments().size());
          }

          {
            final CGClassTypeSignature it = pcc.innerTypes().get(0);
            Assertions.assertEquals("B", it.typeName());
            Assertions.assertEquals(0, it.innerTypes().size());
            Assertions.assertEquals(1, it.typeArguments().arguments().size());

            final CGTypeArgumentExactly ta =
              (CGTypeArgumentExactly) it.typeArguments().arguments().get(0);
            final CGFieldTypeSignatureClass ta_fs =
              (CGFieldTypeSignatureClass) ta.fieldSignature();
            final CGClassTypeSignature ta_cl =
              ta_fs.classType();

            Assertions.assertEquals("java.lang.Integer", ta_cl.typeName());
            Assertions.assertEquals(0, ta_cl.innerTypes().size());
            Assertions.assertEquals(
              0,
              ta_cl.typeArguments().arguments().size());
          }

          {
            final CGClassTypeSignature it = pcc.innerTypes().get(1);
            Assertions.assertEquals("C", it.typeName());
            Assertions.assertEquals(0, it.innerTypes().size());
            Assertions.assertEquals(1, it.typeArguments().arguments().size());

            final CGTypeArgumentExactly ta =
              (CGTypeArgumentExactly) it.typeArguments().arguments().get(0);
            final CGFieldTypeSignatureClass ta_fs =
              (CGFieldTypeSignatureClass) ta.fieldSignature();
            final CGClassTypeSignature ta_cl =
              ta_fs.classType();

            Assertions.assertEquals("java.lang.Double", ta_cl.typeName());
            Assertions.assertEquals(0, ta_cl.innerTypes().size());
            Assertions.assertEquals(
              0,
              ta_cl.typeArguments().arguments().size());
          }

          {
            final CGClassTypeSignature it = pcc.innerTypes().get(2);
            Assertions.assertEquals("D", it.typeName());
            Assertions.assertEquals(0, it.innerTypes().size());
            Assertions.assertEquals(1, it.typeArguments().arguments().size());

            final CGTypeArgumentExactly ta =
              (CGTypeArgumentExactly) it.typeArguments().arguments().get(0);
            final CGFieldTypeSignatureClass ta_fs =
              (CGFieldTypeSignatureClass) ta.fieldSignature();
            final CGClassTypeSignature ta_cl =
              ta_fs.classType();

            Assertions.assertEquals("java.lang.Void", ta_cl.typeName());
            Assertions.assertEquals(0, ta_cl.innerTypes().size());
            Assertions.assertEquals(
              0,
              ta_cl.typeArguments().arguments().size());
          }
        }
      }
    );
  }

  @Test
  public void testParseClassTypeSignature10()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<A:Ljava/lang/Object;>Ljava/lang/Object;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<A extends java.lang.Object> extends java.lang.Object",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.lang.Object", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(0, sc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(1, t.parameters().size());
        final CGTypeParameter p0 = t.parameters().get(0);
        Assertions.assertEquals("A", p0.name());
        Assertions.assertEquals(0, p0.intersections().size());

        final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
        final CGClassTypeSignature pcc = pc.classType();
        Assertions.assertEquals("java.lang.Object", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(0, pcc.typeArguments().arguments().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature11()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<Z:Ljava/lang/Number;:Ljava/io/Serializable;>Ljava/lang/Object;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<Z extends java.lang.Number & java.io.Serializable> extends java.lang.Object",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.lang.Object", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(0, sc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(1, t.parameters().size());
        final CGTypeParameter p0 = t.parameters().get(0);
        Assertions.assertEquals("Z", p0.name());
        Assertions.assertEquals(1, p0.intersections().size());

        final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
        final CGClassTypeSignature pcc = pc.classType();
        Assertions.assertEquals("java.lang.Number", pcc.typeName());
        Assertions.assertEquals(0, pcc.innerTypes().size());
        Assertions.assertEquals(0, pcc.typeArguments().arguments().size());

        final CGFieldTypeSignatureClass pi =
          (CGFieldTypeSignatureClass) p0.intersections().get(0);
        final CGClassTypeSignature pic = pi.classType();
        Assertions.assertEquals("java.io.Serializable", pic.typeName());
        Assertions.assertEquals(0, pic.innerTypes().size());
        Assertions.assertEquals(0, pic.typeArguments().arguments().size());
      }
    );
  }

  @Test
  public void testParseClassTypeSignature12()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<A:Ljava/lang/Object;B:TA;>Ljava/lang/Object;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<A extends java.lang.Object,B extends A> extends java.lang.Object",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.lang.Object", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(0, sc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(2, t.parameters().size());

        {
          final CGTypeParameter p0 = t.parameters().get(0);
          Assertions.assertEquals("A", p0.name());
          Assertions.assertEquals(0, p0.intersections().size());
          final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
          Assertions.assertEquals(
            "java.lang.Object",
            pc.classType().typeName());
        }

        {
          final CGTypeParameter p0 = t.parameters().get(1);
          Assertions.assertEquals("B", p0.name());
          Assertions.assertEquals(0, p0.intersections().size());
          final CGFieldTypeSignatureVariable pc = (CGFieldTypeSignatureVariable) p0.type();
          Assertions.assertEquals("A", pc.variable().name());
        }
      }
    );
  }

  @Test
  public void testParseClassTypeSignature13()
  {
    final CGClassSignature t =
      CGenericsParsing.parseClassSignature(
        "<A::Ljava/util/function/Consumer<Ljava/lang/Integer;>;>Ljava/lang/Object;");

    LOG.debug("t: {}", t);
    LOG.debug("t: toJava: {}", t.toJava());

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(
          "<A extends java.util.function.Consumer<java.lang.Integer>> extends java.lang.Object",
          t.toJava());
      },

      () -> {
        Assertions.assertEquals(0, t.interfaces().size());
      },

      () -> {
        final CGClassTypeSignature sc = t.superclass();
        Assertions.assertEquals("java.lang.Object", sc.typeName());
        Assertions.assertEquals(0, sc.innerTypes().size());
        Assertions.assertEquals(0, sc.typeArguments().arguments().size());
      },

      () -> {
        Assertions.assertEquals(1, t.parameters().size());

        {
          final CGTypeParameter p0 = t.parameters().get(0);
          Assertions.assertEquals("A", p0.name());
          Assertions.assertEquals(0, p0.intersections().size());
          final CGFieldTypeSignatureClass pc = (CGFieldTypeSignatureClass) p0.type();
          Assertions.assertEquals(
            "java.util.function.Consumer",
            pc.classType().typeName());

          final CGTypeArgumentExactly pca0 =
            (CGTypeArgumentExactly) pc.classType().typeArguments().arguments().get(
              0);
          final CGFieldTypeSignatureClass pca0f =
            (CGFieldTypeSignatureClass) pca0.fieldSignature();
          Assertions.assertEquals(
            "java.lang.Integer",
            pca0f.classType().typeName());
          Assertions.assertEquals(
            0,
            pca0f.classType().typeArguments().arguments().size());
          Assertions.assertEquals(0, pca0f.classType().innerTypes().size());
        }
      }
    );
  }
}
