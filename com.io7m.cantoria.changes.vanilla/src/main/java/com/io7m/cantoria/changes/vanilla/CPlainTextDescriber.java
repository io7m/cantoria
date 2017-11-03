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

package com.io7m.cantoria.changes.vanilla;

import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CConstructors;
import com.io7m.cantoria.api.CField;
import com.io7m.cantoria.api.CFields;
import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGenericsType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CMethods;
import com.io7m.cantoria.api.CModifier;
import com.io7m.cantoria.changes.api.CChangeClassType;
import com.io7m.cantoria.changes.api.CChangeConstructorType;
import com.io7m.cantoria.changes.api.CChangeEnumType;
import com.io7m.cantoria.changes.api.CChangeFieldType;
import com.io7m.cantoria.changes.api.CChangeMethodType;
import com.io7m.cantoria.changes.api.CChangeModuleType;
import com.io7m.cantoria.changes.api.CChangeType;
import com.io7m.cantoria.changes.spi.CChangeCheckType;
import com.io7m.cantoria.changes.spi.CChangeDescriberType;
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
import com.io7m.cantoria.changes.vanilla.api.CChangeClassFieldAddedPublic;
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
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameMoreAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonFinal;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameNonVarArgs;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodBecameVarArgs;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodExceptionsChanged;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodMovedToSuperclass;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadAdded;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverloadRemoved;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverrideBecameLessAccessible;
import com.io7m.cantoria.changes.vanilla.api.CChangeClassMethodOverrideChangedStatic;
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
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A plain text provider for the vanilla types.
 */

public final class CPlainTextDescriber implements CChangeDescriberType
{
  private final HashMap<Class<?>, DescriberType> types;
  private final ResourceBundle descriptions;
  private final ResourceBundle titles;

  /**
   * Construct a describer.
   */

  public CPlainTextDescriber()
  {
    this.descriptions =
      ResourceBundle.getBundle("com.io7m.cantoria.changes.vanilla.Descriptions");
    this.titles =
      ResourceBundle.getBundle("com.io7m.cantoria.changes.vanilla.Titles");

    this.types = new HashMap<>(64);
    this.add(
      CChangeClassAddedPublic.class,
      this::onClassAddedPublic);
    this.add(
      CChangeClassBecameAbstract.class,
      this::onClassBecameAbstract);
    this.add(
      CChangeClassBecameEnum.class,
      this::onClassBecameEnum);
    this.add(
      CChangeClassBecameFinal.class,
      this::onClassBecameFinal);
    this.add(
      CChangeClassBecameInterface.class,
      this::onClassBecameInterface);
    this.add(
      CChangeClassBecameNonAbstract.class,
      this::onClassBecameNonAbstract);
    this.add(
      CChangeClassBecameNonEnum.class,
      this::onClassBecameNonEnum);
    this.add(
      CChangeClassBecameNonFinal.class,
      this::onClassBecameNonFinal);
    this.add(
      CChangeClassBecameNonInterface.class,
      this::onClassBecameNonInterface);
    this.add(
      CChangeClassBecameNonPublic.class,
      this::onClassBecameNonPublic);
    this.add(
      CChangeClassBecamePublic.class,
      this::onClassBecamePublic);

    this.add(
      CChangeClassBytecodeVersionChanged.class,
      this::onClassBytecodeVersionChanged);

    this.add(
      CChangeClassConstructorAdded.class,
      this::onClassConstructorAdded);
    this.add(
      CChangeClassConstructorRemoved.class,
      this::onClassConstructorRemoved);

    this.add(
      CChangeClassFieldAddedPublic.class,
      this::onClassFieldAddedPublic);
    this.add(
      CChangeClassFieldBecameFinal.class,
      this::onClassFieldBecameFinal);
    this.add(
      CChangeClassFieldBecameLessAccessible.class,
      this::onClassFieldBecameLessAccessible);
    this.add(
      CChangeClassFieldBecameMoreAccessible.class,
      this::onClassFieldBecameMoreAccessible);
    this.add(
      CChangeClassFieldBecameNonFinal.class,
      this::onClassFieldBecameNonFinal);
    this.add(
      CChangeClassFieldBecameNonStatic.class,
      this::onClassFieldBecameNonStatic);
    this.add(
      CChangeClassFieldBecameStatic.class,
      this::onClassFieldBecameStatic);
    this.add(
      CChangeClassFieldMovedToSuperclass.class,
      this::onClassFieldMovedToSuperclass);
    this.add(
      CChangeClassFieldOverrideBecameLessAccessible.class,
      this::onChangeClassFieldOverrideBecameLessAccessible);
    this.add(
      CChangeClassFieldOverrideChangedStatic.class,
      this::onClassFieldOverrideChangedStatic);
    this.add(
      CChangeClassFieldRemovedPublic.class,
      this::onClassFieldRemovedPublic);
    this.add(
      CChangeClassFieldTypeChanged.class,
      this::onClassFieldTypeChanged);

    this.add(
      CChangeClassGenericsChanged.class,
      this::onClassGenericsChanged);

    this.add(
      CChangeClassMethodAdded.class,
      this::onClassMethodAdded);
    this.add(
      CChangeClassMethodBecameLessAccessible.class,
      this::onClassMethodBecameLessAccessible);
    this.add(
      CChangeClassMethodBecameMoreAccessible.class,
      this::onClassMethodBecameMoreAccessible);
    this.add(
      CChangeClassMethodBecameFinal.class,
      this::onClassMethodBecameFinal);
    this.add(
      CChangeClassMethodBecameNonFinal.class,
      this::onClassMethodBecameNonFinal);
    this.add(
      CChangeClassMethodBecameVarArgs.class,
      this::onClassMethodBecameVarArgs);
    this.add(
      CChangeClassMethodBecameNonVarArgs.class,
      this::onClassMethodBecameNonVarArgs);
    this.add(
      CChangeClassMethodExceptionsChanged.class,
      this::onClassMethodExceptionsChanged);
    this.add(
      CChangeClassMethodMovedToSuperclass.class,
      this::onClassMethodMovedToSuperclass);
    this.add(
      CChangeClassMethodOverloadAdded.class,
      this::onClassMethodOverloadAdded);
    this.add(
      CChangeClassMethodOverloadRemoved.class,
      this::onClassMethodOverloadRemoved);
    this.add(
      CChangeClassMethodOverrideBecameLessAccessible.class,
      this::onClassMethodOverrideBecameLessAccessible);
    this.add(
      CChangeClassMethodOverrideChangedStatic.class,
      this::onClassMethodOverrideChangedStatic);
    this.add(
      CChangeClassMethodRemoved.class,
      this::onClassMethodRemoved);

    this.add(
      CChangeClassRemovedPublic.class,
      this::onClassRemovedPublic);
    this.add(
      CChangeClassStaticInitializerAdded.class,
      this::onClassStaticInitializerAdded);

    this.add(
      CChangeEnumAddedMembers.class,
      this::onEnumAddedMembers);
    this.add(
      CChangeEnumRemovedMembers.class,
      this::onEnumRemovedMembers);

    this.add(
      CChangeInterfaceMethodAbstractAdded.class,
      this::onInterfaceMethodAbstractAdded);
    this.add(
      CChangeInterfaceMethodAbstractRemoved.class,
      this::onInterfaceMethodAbstractRemoved);
    this.add(
      CChangeInterfaceMethodDefaultAdded.class,
      this::onInterfaceMethodDefaultAdded);
    this.add(
      CChangeInterfaceMethodDefaultRemoved.class,
      this::onInterfaceMethodDefaultRemoved);
    this.add(
      CChangeInterfaceMethodStaticAdded.class,
      this::onInterfaceMethodStaticAdded);
    this.add(
      CChangeInterfaceMethodStaticRemoved.class,
      this::onInterfaceMethodStaticRemoved);

    this.add(
      CChangeModuleNoLongerRequired.class,
      this::onModuleNoLongerRequired);
    this.add(
      CChangeModulePackageNoLongerQualifiedExported.class,
      this::onModuleNoLongerQualifiedExported);
    this.add(
      CChangeModulePackageNoLongerQualifiedOpened.class,
      this::onModuleNoLongerQualifiedOpened);
    this.add(
      CChangeModulePackageNoLongerTransitivelyExported.class,
      this::onModuleNoLongerTransitivelyExported);
    this.add(
      CChangeModulePackageNoLongerUnqualifiedExported.class,
      this::onModuleNoLongerUnqualifiedExported);
    this.add(
      CChangeModulePackageNoLongerUnqualifiedOpened.class,
      this::onModuleNoLongerUnqualifiedOpened);
    this.add(
      CChangeModulePackageQualifiedExported.class,
      this::onModuleQualifiedExported);
    this.add(
      CChangeModulePackageQualifiedOpened.class,
      this::onModuleQualifiedOpened);
    this.add(
      CChangeModulePackageTransitivelyExported.class,
      this::onModuleTransitivelyExported);
    this.add(
      CChangeModulePackageUnqualifiedExported.class,
      this::onModuleUnqualifiedExported);
    this.add(
      CChangeModulePackageUnqualifiedOpened.class,
      this::onModuleUnqualifiedOpened);
    this.add(
      CChangeModuleRequired.class,
      this::onModuleRequired);
    this.add(
      CChangeModuleServiceNoLongerProvided.class,
      this::onModuleServiceNoLongerProvided);
    this.add(
      CChangeModuleServiceProvided.class,
      this::onModuleServiceProvided);
  }

  private static String bytecodeVersion(
    final int version)
  {
    switch (version) {
      case 41: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 1.1");
      }
      case 46: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 1.2");
      }
      case 47: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 1.3");
      }
      case 49: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 5");
      }
      case 50: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 6");
      }
      case 51: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 7");
      }
      case 52: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 8");
      }
      case 53: {
        return String.format("%d (%s)", Integer.valueOf(version), "Java 9");
      }
      default: {
        return String.format("%d", Integer.valueOf(version));
      }
    }
  }

  private static BufferedWriter makeWriter(
    final OutputStream out)
  {
    return new BufferedWriter(new OutputStreamWriter(out, UTF_8));
  }

  private void onClassGenericsChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassGenericsChanged cc = (CChangeClassGenericsChanged) c;

    this.change(w, "the.generic.type.parameters.of.a.class.have.changed");
    this.showGenerics(w, cc.classPrevious().signature(), Revision.PREVIOUS);
  }

  private void change(
    final BufferedWriter w,
    final String key)
    throws IOException
  {
    w.append(this.fieldStart("change"));
    w.append(this.descriptions.getString(key));
    w.newLine();
  }

  private void onClassMethodBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameLessAccessible cc =
      (CChangeClassMethodBecameLessAccessible) c;

    this.change(w, "a.method.became.less.accessible");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassMethodBecameMoreAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameMoreAccessible cc =
      (CChangeClassMethodBecameMoreAccessible) c;

    this.change(w, "a.method.became.more.accessible");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassBecameEnum(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.class.became.an.enum");
  }

  private void onClassBecameNonEnum(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "an.enum.was.changed.to.a.non.enum.type");
  }

  private void onEnumAddedMembers(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeEnumAddedMembers cc = (CChangeEnumAddedMembers) c;

    this.change(w, "one.or.more.members.cases.were.added.to.an.enum");

    w.append(this.fieldStart("enum.added.members"));
    w.append(cc.addedMembers().collect(Collectors.joining(",")));
    w.newLine();
  }

  private void onEnumRemovedMembers(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeEnumRemovedMembers cc = (CChangeEnumRemovedMembers) c;

    this.change(w, "one.or.more.members.cases.were.removed.from.an.enum");

    w.append(this.fieldStart("enum.removed.members"));
    w.append(cc.removedMembers().collect(Collectors.joining(",")));
    w.newLine();
  }

  private void onModuleUnqualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageUnqualifiedOpened cc =
      (CChangeModulePackageUnqualifiedOpened) c;

    this.change(w, "a.package.is.now.opened.unqualified");

    w.append(this.fieldStart("package.opened"));
    w.append(cc.packageName());
    w.newLine();
  }

  private void onModuleUnqualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageUnqualifiedExported cc =
      (CChangeModulePackageUnqualifiedExported) c;

    this.change(w, "a.package.is.now.exported.unqualified");

    w.append(this.fieldStart("package.exported"));
    w.append(cc.packageName());
    w.newLine();
  }

  private void onModuleTransitivelyExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageTransitivelyExported cc =
      (CChangeModulePackageTransitivelyExported) c;

    this.change(
      w, "a.module.is.now.transitively.exported.by.a.requires.directive");

    w.append(this.fieldStart("module.required"));
    w.append(cc.moduleRequired());
    w.newLine();
  }

  private void onModuleNoLongerRequired(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleNoLongerRequired cc =
      (CChangeModuleNoLongerRequired) c;

    this.change(w, "a.module.no.longer.requires.another.module");

    w.append(this.fieldStart("module.required"));
    w.append(cc.moduleTarget());
    w.append(cc.isTransitive() ? " (transitive)" : "");
    w.newLine();
  }

  private void onModuleNoLongerQualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerQualifiedExported cc =
      (CChangeModulePackageNoLongerQualifiedExported) c;

    this.change(w, "a.package.is.no.longer.exported.qualified.by.a.module");

    w.append(this.fieldStart("package.exported"));
    w.append(cc.packageName());
    w.append(" → ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private void onModuleNoLongerQualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerQualifiedOpened cc =
      (CChangeModulePackageNoLongerQualifiedOpened) c;

    this.change(w, "a.package.is.no.longer.opened.qualified.by.a.module");

    w.append(this.fieldStart("package.opened"));
    w.append(cc.packageName());
    w.append(" → ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private void onModuleNoLongerTransitivelyExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerTransitivelyExported cc =
      (CChangeModulePackageNoLongerTransitivelyExported) c;

    this.change(w, "a.package.is.no.transitively.exported.by.a.module");

    w.append(this.fieldStart("module.exported"));
    w.append(cc.moduleRequired());
    w.newLine();
  }

  private void onModuleNoLongerUnqualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerUnqualifiedExported cc =
      (CChangeModulePackageNoLongerUnqualifiedExported) c;

    this.change(w, "a.package.is.no.longer.exported.unqualified.by.a.module");

    w.append(this.fieldStart("package.exported"));
    w.append(cc.packageName());
    w.newLine();
  }

  private void onModuleNoLongerUnqualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerUnqualifiedOpened cc =
      (CChangeModulePackageNoLongerUnqualifiedOpened) c;

    this.change(w, "a.package.is.no.longer.opened.unqualified.by.a.module");

    w.append(this.fieldStart("package.opened"));
    w.append(cc.packageName());
    w.newLine();
  }

  private void onModuleQualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageQualifiedExported cc =
      (CChangeModulePackageQualifiedExported) c;

    this.change(w, "a.package.is.exported.qualified.by.a.module");

    w.append(this.fieldStart("package.exported"));
    w.append(cc.packageName());
    w.append(" → ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private void onModuleQualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageQualifiedOpened cc =
      (CChangeModulePackageQualifiedOpened) c;

    this.change(w, "a.package.is.opened.qualified.by.a.module");

    w.append(this.fieldStart("package.opened"));
    w.append(cc.packageName());
    w.append(" → ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private void onModuleRequired(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleRequired cc =
      (CChangeModuleRequired) c;

    this.change(w, "a.module.now.requires.another.module");

    w.append(this.fieldStart("module.required"));
    w.append(cc.moduleTarget());
    w.append(cc.isTransitive() ? " (transitive)" : "");
    w.newLine();
  }

  private void onModuleServiceProvided(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleServiceProvided cc =
      (CChangeModuleServiceProvided) c;

    this.change(w, "a.module.now.provides.a.service");

    w.append(this.fieldStart("service.provided"));
    w.append(cc.provides().service());
    w.append(" with ");
    w.append(cc.provides().provider());
    w.newLine();
  }

  private void onModuleServiceNoLongerProvided(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleServiceNoLongerProvided cc =
      (CChangeModuleServiceNoLongerProvided) c;

    this.change(w, "a.module.no.longer.provides.a.service");

    w.append(this.fieldStart("service.provided"));
    w.append(cc.provides().service());
    w.append(" with ");
    w.append(cc.provides().provider());
    w.newLine();
  }

  private void onInterfaceMethodAbstractAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "an.abstract.method.was.added.to.an.interface");
  }

  private void onInterfaceMethodAbstractRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "an.abstract.method.was.removed.from.an.interface");
  }

  private void onInterfaceMethodDefaultAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.default.method.was.added.to.an.interface");
  }

  private void onInterfaceMethodDefaultRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.default.method.was.removed.from.an.interface");
  }

  private void onInterfaceMethodStaticAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.static.method.was.added.to.an.interface");
  }

  private void onInterfaceMethodStaticRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.static.method.was.removed.from.an.interface");
  }

  private void onClassMethodRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.method.was.removed");
  }

  private void onClassMethodOverrideBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverrideBecameLessAccessible cc =
      (CChangeClassMethodOverrideBecameLessAccessible) c;

    final CMethod method_ancestor = cc.methodAncestor();
    this.change(
      w,
      "a.method.declaration.reduces.the.accessibility.of.an.overridden.method");

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(method_ancestor.className()));
    w.newLine();
    w.append(this.fieldStart("method.ancestor"));
    w.append(CMethods.show(method_ancestor));
    w.newLine();
  }

  private void onClassMethodOverrideChangedStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverrideChangedStatic cc =
      (CChangeClassMethodOverrideChangedStatic) c;

    final CMethod method_ancestor = cc.methodAncestor();
    final CMethod method_current = cc.method();

    w.append(this.fieldStart("change"));
    if (method_current.modifiers().contains(CModifier.STATIC)
      && !method_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("a.static.method.attempts.to.override.a.non.static.method");
    }

    if (!method_current.modifiers().contains(CModifier.STATIC)
      && method_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("a.non.static.method.attempts.to.override.a.static.method");
    }
    w.newLine();

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(method_ancestor.className()));
    w.newLine();
    w.append(this.fieldStart("method.ancestor"));
    w.append(CMethods.show(method_ancestor));
    w.newLine();
  }

  private void onClassMethodOverloadRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverloadRemoved cc =
      (CChangeClassMethodOverloadRemoved) c;

    this.change(w, "a.method.overload.was.removed");
  }

  private void onClassMethodOverloadAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverloadAdded cc =
      (CChangeClassMethodOverloadAdded) c;

    this.change(w, "a.method.overload.was.added");
  }

  private void onClassMethodMovedToSuperclass(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodMovedToSuperclass cc =
      (CChangeClassMethodMovedToSuperclass) c;

    this.change(w, "a.method.was.moved.to.an.ancestor.class");

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(cc.methodAncestor().className()));
    w.newLine();

    w.append(this.fieldStart("method.ancestor"));
    w.append(CMethods.show(cc.methodAncestor()));
    w.newLine();
  }

  private void onClassMethodExceptionsChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodExceptionsChanged cc =
      (CChangeClassMethodExceptionsChanged) c;

    this.change(w, "the.declared.thrown.exceptions.for.a.method.have.changed");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassMethodBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameNonFinal cc =
      (CChangeClassMethodBecameNonFinal) c;

    this.change(w, "a.previously.final.method.became.non.final");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassMethodBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameFinal cc =
      (CChangeClassMethodBecameFinal) c;

    this.change(w, "a.previously.non.final.method.became.final");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassMethodBecameNonVarArgs(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameNonVarArgs cc =
      (CChangeClassMethodBecameNonVarArgs) c;

    this.change(w, "a.previously.final.method.became.non.variadic");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassMethodBecameVarArgs(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameVarArgs cc =
      (CChangeClassMethodBecameVarArgs) c;

    this.change(w, "a.previously.non.final.method.became.variadic");

    w.append(this.fieldStart("method.previous"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private void onClassFieldTypeChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldTypeChanged cc =
      (CChangeClassFieldTypeChanged) c;

    this.change(w, "the.type.of.a.non.private.field.changed");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldRemovedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.field.was.removed");
  }

  private void onClassFieldMovedToSuperclass(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldMovedToSuperclass cc =
      (CChangeClassFieldMovedToSuperclass) c;

    this.change(w, "a.field.moved.to.an.ancestor.class");

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(cc.fieldAncestor().className()));
    w.newLine();

    w.append(this.fieldStart("field.ancestor"));
    w.append(CFields.show(cc.fieldAncestor()));
    w.newLine();
  }

  private void onClassFieldBecameStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameStatic cc =
      (CChangeClassFieldBecameStatic) c;

    this.change(w, "a.previously.non.static.field.became.static");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldBecameNonStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameNonStatic cc =
      (CChangeClassFieldBecameNonStatic) c;

    this.change(w, "a.previously.static.field.became.non.static");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameNonFinal cc =
      (CChangeClassFieldBecameNonFinal) c;

    this.change(w, "a.previously.final.field.became.non.final");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldBecameMoreAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameMoreAccessible cc =
      (CChangeClassFieldBecameMoreAccessible) c;

    this.change(w, "the.accessibility.of.a.field.was.increased");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameLessAccessible cc =
      (CChangeClassFieldBecameLessAccessible) c;

    this.change(w, "the.accessibility.of.a.field.was.reduced");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameFinal cc =
      (CChangeClassFieldBecameFinal) c;

    this.change(w, "a.non.final.field.became.final");

    w.append(this.fieldStart("field.previous"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private void onClassFieldAddedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.field.was.added");
  }

  private void onClassBytecodeVersionChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassBytecodeVersionChanged cc =
      (CChangeClassBytecodeVersionChanged) c;

    this.change(w, "the.bytecode.version.of.a.public.class.has.changed");

    w.append(this.fieldStart("bytecode.version.previous"));
    w.append(bytecodeVersion(cc.classPrevious().bytecodeVersion()));
    w.newLine();

    w.append(this.fieldStart("bytecode.version.current"));
    w.append(bytecodeVersion(cc.classValue().bytecodeVersion()));
    w.newLine();
  }

  private void onClassAddedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.public.class.was.added.to.an.exported.package");
  }

  private void onClassRemovedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.public.class.was.removed.from.an.exported.package");
  }

  private void onClassBecameAbstract(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.abstract.class.was.made.abstract");
  }

  private void onClassBecamePublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.public.class.was.made.public");
  }

  private void onClassBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.final.class.was.made.final");
  }

  private void onClassBecameInterface(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.interface.class.was.changed.into.an.interface");
  }

  private void onClassBecameNonAbstract(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "an.abstract.class.was.made.non.abstract");
  }

  private void onClassBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.final.class.was.made.non.final");
  }

  private void onClassBecameNonInterface(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "an.interface.was.changed.into.a.non.interface.class");
  }

  private void onClassBecameNonPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.public.class.was.made.non.public");
  }

  private void onClassConstructorAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.constructor.was.added");
  }

  private void onClassConstructorRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.constructor.was.removed");
  }

  private void onClassMethodAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.non.private.method.was.added");
  }

  private void onClassStaticInitializerAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    this.change(w, "a.static.initializer.was.added");
  }

  private void onChangeClassFieldOverrideBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldOverrideBecameLessAccessible cc =
      (CChangeClassFieldOverrideBecameLessAccessible) c;

    final CField field_ancestor = cc.fieldAncestor();
    this.change(
      w,
      "a.field.declaration.reduces.the.accessibility.of.an.overridden.field");

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(field_ancestor.className()));
    w.newLine();
    w.append(this.fieldStart("field.ancestor"));
    w.append(CFields.show(field_ancestor));
    w.newLine();
  }

  private void onClassFieldOverrideChangedStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldOverrideChangedStatic cc =
      (CChangeClassFieldOverrideChangedStatic) c;

    final CField field_ancestor = cc.fieldAncestor();
    final CField field_current = cc.field();

    w.append(this.fieldStart("change"));
    if (field_current.modifiers().contains(CModifier.STATIC)
      && !field_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append(this.descriptions.getString(
        "a.static.field.attempts.to.override.a.non.static.field"));
    }

    if (!field_current.modifiers().contains(CModifier.STATIC)
      && field_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append(this.descriptions.getString(
        "a.non.static.field.attempts.to.override.a.static.field"));
    }
    w.newLine();

    w.append(this.fieldStart("class.ancestor"));
    w.append(CClassNames.show(field_ancestor.className()));
    w.newLine();
    w.append(this.fieldStart("field.ancestor"));
    w.append(CFields.show(field_ancestor));
    w.newLine();
  }

  private void showChangeDetails(
    final CChangeCheckType originator,
    final CChangeType change,
    final BufferedWriter w)
    throws IOException
  {
    switch (change.category()) {
      case CHANGE_FIELD: {
        final CChangeFieldType c = (CChangeFieldType) change;
        final CField f = c.field();

        w.append(this.fieldStart("class"));
        w.append(CClassNames.show(f.className()));
        w.newLine();

        w.append(this.fieldStart("field.current"));
        w.append(CFields.show(f));
        w.newLine();
        break;
      }

      case CHANGE_CLASS: {
        final CChangeClassType c = (CChangeClassType) change;

        w.append(this.fieldStart("class"));
        w.append(CClassNames.show(c.classValue().name()));
        w.newLine();

        this.showGenerics(w, c.classValue().signature(), Revision.CURRENT);
        break;
      }

      case CHANGE_CONSTRUCTOR: {
        final CChangeConstructorType c = (CChangeConstructorType) change;

        w.append(this.fieldStart("class"));
        w.append(CClassNames.show(c.className()));
        w.newLine();

        w.append(this.fieldStart("constructor.current"));
        w.append(CConstructors.show(c.constructor()));
        w.newLine();
        break;
      }

      case CHANGE_METHOD: {
        final CChangeMethodType c = (CChangeMethodType) change;

        w.append(this.fieldStart("class"));
        w.append(CClassNames.show(c.className()));
        w.newLine();

        w.append(this.fieldStart("method.current"));
        w.append(CMethods.show(c.method()));
        w.newLine();
        break;
      }

      case CHANGE_MODULE: {
        final CChangeModuleType c = (CChangeModuleType) change;

        w.append(this.fieldStart("module"));
        w.append(c.module());
        w.newLine();
        break;
      }

      case CHANGE_ENUM: {
        final CChangeEnumType c = (CChangeEnumType) change;

        w.append(this.fieldStart("class"));
        w.append(CClassNames.show(c.enumType().name()));
        w.newLine();

        this.showGenerics(w, c.enumType().signature(), Revision.CURRENT);
        break;
      }
    }

    w.append(this.fieldStart("compatibility"));
    w.append(this.compatibility(change));
    w.newLine();

    w.append(this.fieldStart("semantic.versioning"));
    w.append(this.semanticVersioning(change));
    w.newLine();

    w.append(this.fieldStart("check"));
    w.append(originator.name());
    w.newLine();

    if (!originator.jlsReferences().isEmpty()) {
      w.append(this.fieldStart("specifications"));
      w.append(originator.jlsReferences().collect(Collectors.joining(",")));
      w.newLine();
    }
  }

  private void showGenerics(
    final BufferedWriter w,
    final Optional<CGClassSignature> sig_opt,
    final Revision revision)
    throws IOException
  {
    switch (revision) {
      case PREVIOUS: {
        w.append(this.fieldStart("generic.type.parameters.previous"));
        break;
      }
      case CURRENT: {
        w.append(this.fieldStart("generic.type.parameters.current"));
        break;
      }
    }

    if (sig_opt.isPresent()) {
      final CGClassSignature sig = sig_opt.get();
      if (!sig.parameters().isEmpty()) {
        w.append("<");
        w.append(sig.parameters()
                   .map(CGenericsType.CGTypeParameterType::toJava)
                   .collect(Collectors.joining(",")));
        w.append(">");
      } else {
        w.append(this.descriptions.getString("generics.none"));
      }
    } else {
      w.append(this.descriptions.getString("generics.none"));
    }
    w.newLine();
  }

  private String fieldStart(
    final String name)
  {
    final String title =
      this.titles.getString(Objects.requireNonNull(name, "Name"));
    return String.format("%-36s ", title + ":");
  }

  private String semanticVersioning(
    final CChangeType change)
  {
    switch (change.semanticVersioning()) {
      case SEMANTIC_MAJOR:
        return this.descriptions.getString(
          "requires.major.version.number.increment");
      case SEMANTIC_MINOR:
        return this.descriptions.getString(
          "requires.minor.version.number.increment");
      case SEMANTIC_NONE:
        return this.descriptions.getString(
          "requires.no.version.number.increment");
    }

    throw new UnreachableCodeException();
  }

  private String compatibility(
    final CChangeType change)
  {
    switch (change.binaryCompatibility()) {
      case BINARY_COMPATIBLE: {
        switch (change.sourceCompatibility()) {
          case SOURCE_COMPATIBLE: {
            return this.descriptions.getString(
              "source.and.binary.compatible");
          }
          case SOURCE_INCOMPATIBLE: {
            return this.descriptions.getString(
              "binary.compatible.but.source.incompatible");
          }
        }
        break;
      }
      case BINARY_INCOMPATIBLE: {
        switch (change.sourceCompatibility()) {
          case SOURCE_COMPATIBLE: {
            return this.descriptions.getString(
              "source.compatible.but.binary.incompatible");
          }
          case SOURCE_INCOMPATIBLE: {
            return this.descriptions.getString(
              "source.and.binary.incompatible");
          }
        }
        break;
      }
    }

    throw new UnreachableCodeException();
  }

  private void add(
    final Class<?> c,
    final DescriberType d)
  {
    Preconditions.checkPrecondition(
      !this.types.containsKey(c),
      "Describer for " + c + " must not already be registered");
    this.types.put(c, d);
  }

  @Override
  public boolean canDescribe(
    final CChangeType change)
  {
    return this.types.containsKey(change.getClass());
  }

  @Override
  public String format()
  {
    return "com.io7m.cantoria.format.text";
  }

  @Override
  public void describe(
    final CChangeCheckType originator,
    final CChangeType change,
    final OutputStream out)
    throws IOException
  {
    Objects.requireNonNull(originator, "Originator");
    Objects.requireNonNull(change, "Change");
    Objects.requireNonNull(out, "Output");

    final DescriberType text =
      this.types.get(change.getClass());

    if (text == null) {
      throw new IllegalArgumentException(
        "No describer for type: " + change.getClass().getCanonicalName());
    }

    final BufferedWriter w = makeWriter(out);
    text.describe(w, change);
    this.showChangeDetails(originator, change, w);
    w.newLine();
    w.flush();
  }

  private enum Revision
  {
    PREVIOUS,
    CURRENT
  }

  private interface DescriberType
  {
    void describe(
      BufferedWriter w,
      CChangeType c)
      throws IOException;
  }
}
