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

package com.io7m.cantoria.tests.driver.api;

import com.io7m.cantoria.api.CAccessibility;
import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CClassRegistry;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CConstructor;
import com.io7m.cantoria.api.CEnum;
import com.io7m.cantoria.api.CEnumMember;
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CFieldModifiers;
import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGClassTypeSignature;
import com.io7m.cantoria.api.CGFieldTypeSignatureClass;
import com.io7m.cantoria.api.CGTypeArgumentExactly;
import com.io7m.cantoria.api.CGTypeArguments;
import com.io7m.cantoria.api.CGTypeParameter;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.api.CModuleProvides;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.changes.api.CChangeType;
import com.io7m.cantoria.changes.spi.CChangeCheckType;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassAddedPublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameAbstract;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameEnum;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameInterface;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonAbstract;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonEnum;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonInterface;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecameNonPublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBecamePublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassBytecodeVersionChanged;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassConstructorAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassConstructorRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameMoreAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameNonFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameNonStatic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldBecameStatic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldMovedToSuperclass;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldOverrideBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldOverrideChangedStatic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldRemovedPublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldTypeChanged;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassGenericsChanged;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonVarArgs;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameVarArgs;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodExceptionsChanged;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverrideBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassRemovedPublic;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassStaticInitializerAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeEnumAddedMembers;
import com.io7m.cantoria.changes.vanilla.api.CChangeEnumRemovedMembers;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodAbstractAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodAbstractRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodDefaultAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodDefaultRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodStaticAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeInterfaceMethodStaticRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleNoLongerRequired;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerQualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerQualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerTransitivelyExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerUnqualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageNoLongerUnqualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageQualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageQualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageTransitivelyExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageUnqualifiedExported;
import com.io7m.cantoria.changes.vanilla.api.CChangeModulePackageUnqualifiedOpened;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleRequired;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleServiceNoLongerProvided;
import com.io7m.cantoria.changes.vanilla.api.CChangeModuleServiceProvided;
import com.io7m.cantoria.driver.api.CComparisonDriverType;
import com.io7m.cantoria.modules.api.CModuleLoaderType;
import com.io7m.cantoria.tests.CTestUtilities;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import mockit.Expectations;
import mockit.FullVerifications;
import mockit.Mocked;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;

public abstract class CDriverContract
{
  public static final CClassName CLASS_NAME_Y = CClassName.of(
    "x.y.z",
    "x.y.z.p",
    "Y");

  private static final CClassName CLASS_NAME_X =
    CClassName.of("x.y.z", "x.y.z.p", "X");

  protected abstract CModuleLoaderType moduleLoader();

  private CClassRegistryType classRegistry(
    final CModuleType... modules)
    throws IOException
  {
    return CClassRegistry.create(
      List.of(modules)
        .prepend(this.moduleLoader().openPlatformModule("java.base"))
        .map(CModuleWeaklyCaching::wrap));
  }

  private static MethodNode anyMethod()
  {
    return new MethodNode(Opcodes.ASM6);
  }

  private static FieldNode field(
    final String field_name,
    final String field_desc)
  {
    return new FieldNode(
      Opcodes.ACC_PUBLIC, field_name, field_desc, null, null);
  }

  private static ClassNode anyClass()
  {
    return new ClassNode(Opcodes.ASM6);
  }

  private static CGClassSignature enumSignature(
    final String long_name)
  {
    return CGClassSignature.builder()
      .setSuperclass(
        CGClassTypeSignature.builder()
          .setTypeName("java.lang.Enum")
          .setTypeArguments(CGTypeArguments.of(List.of(
            CGTypeArgumentExactly.of(
              CGFieldTypeSignatureClass.of(
                CGClassTypeSignature.builder()
                  .setTypeName(long_name)
                  .setTypeArguments(CGTypeArguments.of(List.empty()))
                  .build())))))
          .build())
      .build();
  }

  private static CGClassTypeSignature javaLangNumberSignature()
  {
    return CGClassTypeSignature.builder()
      .setTypeName("java.lang.Number")
      .setTypeArguments(CGTypeArguments.of(List.empty()))
      .build();
  }

  private static CGClassTypeSignature javaLangIntegerSignature()
  {
    return CGClassTypeSignature.builder()
      .setTypeName("java.lang.Integer")
      .setTypeArguments(CGTypeArguments.of(List.empty()))
      .build();
  }

  private static CGClassTypeSignature javaLangObjectSignature()
  {
    return CGClassTypeSignature.builder()
      .setTypeName("java.lang.Object")
      .setTypeArguments(CGTypeArguments.of(List.empty()))
      .build();
  }

  protected abstract CComparisonDriverType driver();

  @Test
  public final void testModuleRequiresTransitiveAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_requires_transitive_added/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_requires_transitive_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageTransitivelyExported.of(
          "x.y.z", "java.logging"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModuleRequiresTransitiveRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_requires_transitive_removed/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_requires_transitive_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerTransitivelyExported.of(
          "x.y.z", "java.logging"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModuleRequiresRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_requires_removed/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_requires_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModuleNoLongerRequired.of(
          "x.y.z", "java.logging", false));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModuleRequiresAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_requires_added/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_requires_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModuleRequired.of(
          "x.y.z", "java.logging", false));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportUnqualifiedRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_export_unqualified_removed/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_export_unqualified_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerUnqualifiedExported.of(
          "x.y.z", "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportUnqualifiedAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_export_unqualified_added/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_export_unqualified_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageUnqualifiedExported.of(
          "x.y.z", "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportQualifiedAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_export_qualified_added/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_export_qualified_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageQualifiedExported.of(
          "x.y.z", "x.y.z.p", "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportQualifiedRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_export_qualified_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_export_qualified_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerQualifiedExported.of(
          "x.y.z", "x.y.z.p", "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportQualifiedToUnqualified(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_export_qualified_to_unqualified/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_export_qualified_to_unqualified/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageUnqualifiedExported.of(
          "x.y.z", "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageExportUnqualifiedToQualified(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_export_unqualified_to_qualified/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_export_unqualified_to_qualified/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerUnqualifiedExported.of(
          "x.y.z",
          "x.y.z.p"));
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageQualifiedExported.of(
          "x.y.z",
          "x.y.z.p",
          "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testModulePackageOpensUnqualifiedRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_opens_unqualified_removed/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_opens_unqualified_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerUnqualifiedOpened.of(
          "x.y.z",
          "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageOpensUnqualifiedAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_opens_unqualified_added/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_opens_unqualified_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageUnqualifiedOpened.of(
          "x.y.z", "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageOpensQualifiedAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_opens_qualified_added/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_opens_qualified_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageQualifiedOpened.of(
          "x.y.z", "x.y.z.p", "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageOpensQualifiedRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_package_opens_qualified_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("module_package_opens_qualified_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerQualifiedOpened.of(
          "x.y.z", "x.y.z.p", "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageOpensQualifiedToUnqualified(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_opens_qualified_to_unqualified/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_opens_qualified_to_unqualified/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageUnqualifiedOpened.of(
          "x.y.z", "x.y.z.p"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModulePackageOpensUnqualifiedToQualified(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module(
        "module_package_opens_unqualified_to_qualified/before");
    final CModuleType module1 =
      CTestUtilities.module(
        "module_package_opens_unqualified_to_qualified/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageNoLongerUnqualifiedOpened.of(
          "x.y.z", "x.y.z.p"));
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModulePackageQualifiedOpened.of(
          "x.y.z", "x.y.z.p", "a.b.c"));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testModuleServiceProvided(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_service_provided/before");
    final CModuleType module1 =
      CTestUtilities.module("module_service_provided/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModuleServiceProvided.of(
          "x.y.z",
          CModuleProvides.of("java.io.Serializable", "x.y.z.p.X")));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testModuleServiceNoLongerProvided(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("module_service_no_longer_provided/before");
    final CModuleType module1 =
      CTestUtilities.module("module_service_no_longer_provided/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeModuleServiceNoLongerProvided.of(
          "x.y.z",
          CModuleProvides.of("java.io.Serializable", "x.y.z.p.X")));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_added/before");
    final CModuleType module1 =
      CTestUtilities.module("class_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassAddedPublic.of(
          CClass.builder()
            .setAccessibility(CAccessibility.PUBLIC)
            .setBytecodeVersion(53)
            .setModule(module0)
            .setName(CLASS_NAME_Y)
            .setNode(anyClass())
            .build()));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("class_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassRemovedPublic.of(
          CClass.builder()
            .setAccessibility(CAccessibility.PUBLIC)
            .setBytecodeVersion(53)
            .setModule(module0)
            .setName(CLASS_NAME_Y)
            .setNode(anyClass())
            .build()));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameNonPublic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_no_longer_public/before");
    final CModuleType module1 =
      CTestUtilities.module("class_no_longer_public/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonPublic.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecamePublic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_public/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_public/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecamePublic.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameInterface(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_interface/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_interface/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameInterface.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .addModifiers(CModifier.INTERFACE)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameAbstract.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .addModifiers(CModifier.INTERFACE)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testClassBecameNonInterface(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_no_longer_interface/before");
    final CModuleType module1 =
      CTestUtilities.module("class_no_longer_interface/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorAdded.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setName("<init>")
                  .setReturnType("void")
                  .setNode(anyMethod())
                  .build())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonAbstract.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .addModifiers(CModifier.INTERFACE)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonInterface.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .addModifiers(CModifier.INTERFACE)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 3;
    }};
  }

  @Test
  public final void testClassRemovedNotPublic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_private_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("class_private_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{

    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 0;
    }};
  }

  @Test
  public final void testClassBytecodeChanged(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_bytecode_changed/before");
    final CModuleType module1 =
      CTestUtilities.module("class_bytecode_changed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBytecodeVersionChanged.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(51)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameEnum(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_enum/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_enum/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameEnum.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .addModifiers(CModifier.ENUM)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(enumSignature("x.y.z.p.X"))
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameFinal.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .addModifiers(CModifier.ENUM)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(enumSignature("x.y.z.p.X"))
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodAdded.of(
          CMethod.builder()
            .setClassName(CLASS_NAME_X)
            .setName("values")
            .setNode(anyMethod())
            .setReturnType("x.y.z.p.X[]")
            .addModifiers(CModifier.STATIC)
            .setAccessibility(CAccessibility.PUBLIC)
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodAdded.of(
          CMethod.builder()
            .setClassName(CLASS_NAME_X)
            .setName("valueOf")
            .setNode(anyMethod())
            .addParameterTypes("java.lang.String")
            .setReturnType("x.y.z.p.X")
            .addModifiers(CModifier.STATIC)
            .setAccessibility(CAccessibility.PUBLIC)
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorRemoved.of(
          CConstructor.builder()
            .setMethod(
              CMethod.builder()
                .setClassName(CLASS_NAME_X)
                .setName("<init>")
                .setAccessibility(CAccessibility.PUBLIC)
                .setReturnType("void")
                .setNode(anyMethod())
                .build())
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassStaticInitializerAdded.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .addModifiers(CModifier.ENUM)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(enumSignature("x.y.z.p.X"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 7;
    }};
  }

  @Test
  public final void testClassBecameNonEnum(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_non_enum/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_non_enum/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonEnum.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .addModifiers(CModifier.ENUM)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(enumSignature("x.y.z.p.X"))
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonFinal.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .addModifiers(CModifier.ENUM)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(enumSignature("x.y.z.p.X"))
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodRemoved.of(
          CMethod.builder()
            .setClassName(CLASS_NAME_X)
            .setName("values")
            .setNode(anyMethod())
            .setReturnType("x.y.z.p.X[]")
            .addModifiers(CModifier.STATIC)
            .setAccessibility(CAccessibility.PUBLIC)
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodRemoved.of(
          CMethod.builder()
            .setClassName(CLASS_NAME_X)
            .setName("valueOf")
            .setNode(anyMethod())
            .addParameterTypes("java.lang.String")
            .setReturnType("x.y.z.p.X")
            .addModifiers(CModifier.STATIC)
            .setAccessibility(CAccessibility.PUBLIC)
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorAdded.of(
          CConstructor.builder()
            .setMethod(
              CMethod.builder()
                .setClassName(CLASS_NAME_X)
                .setReturnType("void")
                .setName("<init>")
                .setAccessibility(CAccessibility.PUBLIC)
                .setNode(anyMethod())
                .build())
            .build()));
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 7;
    }};
  }

  @Test
  public final void testClassBecameFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_final/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameFinal.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameNonFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_no_longer_final/before");
    final CModuleType module1 =
      CTestUtilities.module("class_no_longer_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonFinal.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.FINAL)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameAbstract(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_became_abstract/before");
    final CModuleType module1 =
      CTestUtilities.module("class_became_abstract/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameAbstract.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassBecameNonAbstract(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_no_longer_abstract/before");
    final CModuleType module1 =
      CTestUtilities.module("class_no_longer_abstract/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassBecameNonAbstract.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .addModifiers(CModifier.ABSTRACT)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module1)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodBecameNonFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_became_non_final/before");
    final CModuleType module1 =
      CTestUtilities.module("method_became_non_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodBecameNonFinal.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.FINAL))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodBecameFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_became_final/before");
    final CModuleType module1 =
      CTestUtilities.module("method_became_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,

        CChangeClassMethodBecameFinal.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.FINAL))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodBecameNonVarArgs(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_became_non_varargs/before");
    final CModuleType module1 =
      CTestUtilities.module("method_became_non_varargs/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodBecameNonVarArgs.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.VARARGS))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("int[]"))
              .setReturnType("int")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("int[]"))
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodBecameVarArgs(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_became_varargs/before");
    final CModuleType module1 =
      CTestUtilities.module("method_became_varargs/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodBecameVarArgs.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("int[]"))
              .setReturnType("int")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.VARARGS))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("int[]"))
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodPublicAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_public_added/before");
    final CModuleType module1 =
      CTestUtilities.module("method_public_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodAdded.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodPublicRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_public_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("method_public_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodRemoved.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodPrivateAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_private_added/before");
    final CModuleType module1 =
      CTestUtilities.module("method_private_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{

    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 0;
    }};
  }

  @Test
  public final void testMethodPrivateRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_private_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("method_private_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{

    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 0;
    }};
  }

  @Test
  public final void testMethodAddedExceptions(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_added_exceptions/before");
    final CModuleType module1 =
      CTestUtilities.module("method_added_exceptions/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodExceptionsChanged.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.of("java.io.IOException"))
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodRemovedExceptions(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_removed_exceptions/before");
    final CModuleType module1 =
      CTestUtilities.module("method_removed_exceptions/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodExceptionsChanged.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.of("java.io.IOException"))
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodOverloadAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_overload_added/before");
    final CModuleType module1 =
      CTestUtilities.module("method_overload_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodOverloadAdded.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("double"))
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodOverloadRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_overload_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("method_overload_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodOverloadRemoved.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.of("double"))
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodOverrideBecameLessAccessible(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_override_became_less_accessible/before");
    final CModuleType module1 =
      CTestUtilities.module("method_override_became_less_accessible/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodOverrideBecameLessAccessible.builder()
          .setMethodAncestor(
            CMethod.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setClassName(CClassName.of("x", "x", "S"))
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CClassName.of("x", "x", "T"))
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("int")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testFieldPublicRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("field_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldRemovedPublic.builder()
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldMovedToSuperclass(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_moved_to_superclass/before");
    final CModuleType module1 =
      CTestUtilities.module("field_moved_to_superclass/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldMovedToSuperclass.builder()
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_Y)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setFieldAncestor(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameLessAccessible0(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_less_accessible_0/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_less_accessible_0/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameLessAccessible.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameLessAccessible1(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_less_accessible_1/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_less_accessible_1/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameLessAccessible.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameStatic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_static/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_static/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameStatic.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.of(CModifier.STATIC))
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameNonStatic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_non_static/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_non_static/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameNonStatic.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.of(CModifier.STATIC))
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_final/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameFinal.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.of(CModifier.FINAL))
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameNonFinal(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_non_final/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_non_final/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameNonFinal.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.of(CModifier.FINAL))
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameMoreAccessible0(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_more_accessible_0/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_more_accessible_0/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameMoreAccessible.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldBecameMoreAccessible1(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_became_more_accessible_1/before");
    final CModuleType module1 =
      CTestUtilities.module("field_became_more_accessible_1/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldBecameMoreAccessible.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldChangedType(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_changed_type/before");
    final CModuleType module1 =
      CTestUtilities.module("field_changed_type/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldTypeChanged.builder()
          .setFieldPrevious(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("double")
              .setName("x")
              .setNode(field("x", "double"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("x")
              .setNode(field("x", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testFieldOverrideChangedStatic(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_override_changed_static/before");
    final CModuleType module1 =
      CTestUtilities.module("field_override_changed_static/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassStaticInitializerAdded.of(
          CClass.builder()
            .setAccessibility(CAccessibility.PUBLIC)
            .setBytecodeVersion(53)
            .setModule(module0)
            .setName(CClassName.of("x", "x", "T"))
            .setNode(anyClass())
            .build(),
          CClass.builder()
            .setAccessibility(CAccessibility.PUBLIC)
            .setBytecodeVersion(53)
            .setModule(module1)
            .setName(CClassName.of("x", "x", "T"))
            .setNode(anyClass())
            .build()));

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldOverrideChangedStatic.builder()
          .setFieldAncestor(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CClassName.of("x", "x", "S"))
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("f")
              .setNode(field("f", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CClassName.of("x", "x", "T"))
              .setModifiers(HashSet.of(CModifier.STATIC))
              .setType("int")
              .setName("f")
              .setNode(field("f", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testFieldOverrideBecameLessAccessible(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("field_override_became_less_accessible/before");
    final CModuleType module1 =
      CTestUtilities.module("field_override_became_less_accessible/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassFieldOverrideBecameLessAccessible.builder()
          .setFieldAncestor(
            CField.builder()
              .setAccessibility(CAccessibility.PACKAGE_PRIVATE)
              .setClassName(CClassName.of("x", "x", "S"))
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("f")
              .setNode(field("f", "int"))
              .build())
          .setField(
            CField.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CClassName.of("x", "x", "T"))
              .setModifiers(HashSet.empty())
              .setType("int")
              .setName("f")
              .setNode(field("f", "int"))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodAbstractAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_abstract_added/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_abstract_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodAbstractAdded.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.ABSTRACT))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodAbstractRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_abstract_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_abstract_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodAbstractRemoved.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.ABSTRACT))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodDefaultAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_default_added/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_default_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodDefaultAdded.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodDefaultRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_default_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_default_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodDefaultRemoved.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.empty())
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodStaticAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_static_added/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_static_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodStaticAdded.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.STATIC))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testInterfaceMethodStaticRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("interface_method_static_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("interface_method_static_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeInterfaceMethodStaticRemoved.builder()
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setExceptions(List.empty())
              .setModifiers(HashSet.of(CModifier.STATIC))
              .setName("f")
              .setNode(anyMethod())
              .setParameterTypes(List.empty())
              .setReturnType("void")
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testConstructorAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("constructor_added/before");
    final CModuleType module1 =
      CTestUtilities.module("constructor_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorAdded.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setParameterTypes(List.of("int"))
                  .setName("<init>")
                  .setReturnType("void")
                  .setNode(anyMethod())
                  .build())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorRemoved.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setNode(anyMethod())
                  .setName("<init>")
                  .setReturnType("void")
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testConstructorAddedOverload(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("constructor_added_overload/before");
    final CModuleType module1 =
      CTestUtilities.module("constructor_added_overload/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorAdded.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setParameterTypes(List.of("double"))
                  .setName("<init>")
                  .setReturnType("void")
                  .setNode(anyMethod())
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testConstructorRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("constructor_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("constructor_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorRemoved.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setParameterTypes(List.of("int"))
                  .setName("<init>")
                  .setReturnType("void")
                  .setNode(anyMethod())
                  .build())
              .build())
          .build());

      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorAdded.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setNode(anyMethod())
                  .setReturnType("void")
                  .setName("<init>")
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 2;
    }};
  }

  @Test
  public final void testConstructorRemovedOverload(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("constructor_removed_overload/before");
    final CModuleType module1 =
      CTestUtilities.module("constructor_removed_overload/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassConstructorRemoved.builder()
          .setConstructor(
            CConstructor.builder()
              .setMethod(
                CMethod.builder()
                  .setAccessibility(CAccessibility.PUBLIC)
                  .setClassName(CLASS_NAME_X)
                  .setParameterTypes(List.of("double"))
                  .setName("<init>")
                  .setReturnType("void")
                  .setNode(anyMethod())
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testEnumMemberAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("enum_member_added/before");
    final CModuleType module1 =
      CTestUtilities.module("enum_member_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    final CClass class0 =
      module0.classValue("x.y.z.p", "X").get();
    final CClass class1 =
      module0.classValue("x.y.z.p", "X").get();

    final FieldNode field =
      new FieldNode(CFieldModifiers.fieldEnumMemberModifiers(),
                    "A", null, null, null);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeEnumAddedMembers.builder()
          .setEnumPrevious(
            CEnum.builder()
              .setClassValue(class0)
              .setMembers(HashMap.empty())
              .build())
          .setEnumType(
            CEnum.builder()
              .setClassValue(class1)
              .setMembers(HashMap.of("A", CEnumMember.of("A", field)))
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testEnumMemberRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("enum_member_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("enum_member_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    final CClass class0 =
      module0.classValue("x.y.z.p", "X").get();
    final CClass class1 =
      module0.classValue("x.y.z.p", "X").get();

    final FieldNode field =
      new FieldNode(CFieldModifiers.fieldEnumMemberModifiers(),
                    "A", null, null, null);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeEnumRemovedMembers.builder()
          .setEnumPrevious(
            CEnum.builder()
              .setClassValue(class0)
              .setMembers(HashMap.of("A", CEnumMember.of("A", field)))
              .build())
          .setEnumType(
            CEnum.builder()
              .setClassValue(class1)
              .setMembers(HashMap.empty())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testMethodBecameLessAccessible0(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("method_became_less_accessible/before");
    final CModuleType module1 =
      CTestUtilities.module("method_became_less_accessible/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassMethodBecameLessAccessible.builder()
          .setMethodPrevious(
            CMethod.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setReturnType("void")
              .setName("f")
              .setNode(anyMethod())
              .build())
          .setMethod(
            CMethod.builder()
              .setAccessibility(CAccessibility.PROTECTED)
              .setClassName(CLASS_NAME_X)
              .setModifiers(HashSet.empty())
              .setReturnType("void")
              .setName("f")
              .setNode(anyMethod())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassGenericsAdded(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_generics_added/before");
    final CModuleType module1 =
      CTestUtilities.module("class_generics_added/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassGenericsChanged.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(
                CGClassSignature.builder()
                  .setSuperclass(javaLangObjectSignature())
                  .setParameters(List.of(
                    CGTypeParameter.builder()
                      .setName("A")
                      .setType(CGFieldTypeSignatureClass.of(
                        javaLangObjectSignature()))
                      .build()))
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassGenericsRemoved(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_generics_removed/before");
    final CModuleType module1 =
      CTestUtilities.module("class_generics_removed/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassGenericsChanged.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(
                CGClassSignature.builder()
                  .setSuperclass(javaLangObjectSignature())
                  .setParameters(List.of(
                    CGTypeParameter.builder()
                      .setName("A")
                      .setType(CGFieldTypeSignatureClass.of(
                        javaLangObjectSignature()))
                      .build()))
                  .build())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }

  @Test
  public final void testClassGenericsChangedCompatible(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_generics_changed_compatible/before");
    final CModuleType module1 =
      CTestUtilities.module("class_generics_changed_compatible/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{

    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 0;
    }};
  }

  @Test
  public final void testClassGenericsChangedIncompatible(
    final @Mocked CChangeReceiverType receiver)
    throws Exception
  {
    final CModuleType module0 =
      CTestUtilities.module("class_generics_changed_incompatible/before");
    final CModuleType module1 =
      CTestUtilities.module("class_generics_changed_incompatible/after");

    final CClassRegistryType er = this.classRegistry(module0, module1);

    new Expectations()
    {{
      receiver.onChange(
        (CChangeCheckType) this.any,
        CChangeClassGenericsChanged.builder()
          .setClassPrevious(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(
                CGClassSignature.builder()
                  .setSuperclass(javaLangObjectSignature())
                  .setParameters(List.of(
                    CGTypeParameter.builder()
                      .setName("A")
                      .setType(CGFieldTypeSignatureClass.of(
                        javaLangIntegerSignature()))
                      .build()))
                  .build())
              .build())
          .setClassValue(
            CClass.builder()
              .setAccessibility(CAccessibility.PUBLIC)
              .setBytecodeVersion(53)
              .setModule(module0)
              .setName(CLASS_NAME_X)
              .setNode(anyClass())
              .setSignature(
                CGClassSignature.builder()
                  .setSuperclass(javaLangObjectSignature())
                  .setParameters(List.of(
                    CGTypeParameter.builder()
                      .setName("A")
                      .setType(CGFieldTypeSignatureClass.of(
                        javaLangNumberSignature()))
                      .build()))
                  .build())
              .build())
          .build());
    }};

    this.driver().compareModules(receiver, er, module0, module1);

    new FullVerifications()
    {{
      receiver.onChange((CChangeCheckType) this.any, (CChangeType) this.any);
      this.times = 1;
    }};
  }
}
