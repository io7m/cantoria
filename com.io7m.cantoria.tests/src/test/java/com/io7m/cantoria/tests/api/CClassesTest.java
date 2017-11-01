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

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassRegistry;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CClasses;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModules;
import com.io7m.cantoria.tests.CTestUtilities;
import io.vavr.collection.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public final class CClassesTest
{
  private static final Logger LOG = LoggerFactory.getLogger(CClassesTest.class);

  private static CClassRegistryType classRegistry(
    final List<CModuleType> modules)
  {
    return CClassRegistry.create(modules);
  }

  @Test
  public void testSuperclassesInternal()
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_superclass_changed/before");

    final CClassRegistryType er =
      classRegistry(List.of(
        CModules.openPlatformModule("java.base"),
        module0));

    final CClass clazz =
      module0.classValue("x.y.z.p", "X").get();

    final List<CClass> supers = CClasses.superclassesOf(er, clazz);
    Assertions.assertEquals(2, supers.size());
  }

  @Test
  public void testSuperclassesExternal()
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_superclass_changed_external/after");

    final CClassRegistryType er =
      classRegistry(List.of(
        CModules.openPlatformModule("java.base"),
        module0));

    final CClass clazz =
      module0.classValue("x.y.z.p", "X").get();

    final List<CClass> supers = CClasses.superclassesOf(er, clazz);
    Assertions.assertEquals(2, supers.size());
  }

  @Test
  public void testGenerics()
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_generics_added/after");

    final CClassRegistryType er =
      classRegistry(List.of(
        CModules.openPlatformModule("java.base"),
        module0));

    final CClass clazz =
      module0.classValue("x.y.z.p", "X").get();

    new SignatureReader(clazz.node().signature).accept(new SignatureVisitor(
      Opcodes.ASM6)
    {
      @Override
      public void visitFormalTypeParameter(
        final String name)
      {
        LOG.debug("visitFormalTypeParameter: {}", name);
      }

      @Override
      public SignatureVisitor visitClassBound()
      {
        LOG.debug("visitClassBound");
        return this;
      }

      @Override
      public SignatureVisitor visitInterfaceBound()
      {
        LOG.debug("visitInterfaceBound");
        return this;
      }

      @Override
      public SignatureVisitor visitSuperclass()
      {
        LOG.debug("visitSuperclass");
        return this;
      }

      @Override
      public SignatureVisitor visitInterface()
      {
        LOG.debug("visitInterface");
        return this;
      }

      @Override
      public SignatureVisitor visitParameterType()
      {
        LOG.debug("visitParameterType");
        return this;
      }

      @Override
      public SignatureVisitor visitReturnType()
      {
        LOG.debug("visitReturnType");
        return this;
      }

      @Override
      public SignatureVisitor visitExceptionType()
      {
        LOG.debug("visitExceptionType");
        return this;
      }

      @Override
      public void visitBaseType(
        final char descriptor)
      {
        LOG.debug("visitBaseType: {}", Character.valueOf(descriptor));
      }

      @Override
      public void visitTypeVariable(
        final String name)
      {
        LOG.debug("visitTypeVariable: {}", name);
      }

      @Override
      public SignatureVisitor visitArrayType()
      {
        LOG.debug("visitArrayType");
        return this;
      }

      @Override
      public void visitClassType(
        final String name)
      {
        LOG.debug("visitClassType: {}", name);
      }

      @Override
      public void visitInnerClassType(
        final String name)
      {
        LOG.debug("visitInnerClassType: {}", name);
      }

      @Override
      public void visitTypeArgument()
      {
        LOG.debug("visitTypeArgument");
      }

      @Override
      public SignatureVisitor visitTypeArgument(
        final char wildcard)
      {
        LOG.debug("visitTypeArgument: {}", Character.valueOf(wildcard));
        return this;
      }

      @Override
      public void visitEnd()
      {
        LOG.debug("visitEnd");
      }
    });

    final Type t = Type.getType(clazz.node().signature);
    System.out.println("Type: " + t);
  }
}
