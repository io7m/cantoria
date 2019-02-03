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
import com.io7m.cantoria.api.CFields;
import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGenericsType;
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

    this.types = addAllChanges(this);
  }

  private static HashMap<Class<?>, DescriberType> addAllChanges(
    final CPlainTextDescriber d)
  {
    final var types = new HashMap<Class<?>, DescriberType>(64);
    addClassChanges(d, types);
    addEnumChanges(d, types);
    addInterfaceMethodChanges(d, types);
    addModuleChanges(d, types);
    return types;
  }

  private static void addClassChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    addClassModifierChanges(d, types);
    add(types, CChangeClassBytecodeVersionChanged.class, d::onClassBytecodeVersionChanged);
    add(types, CChangeClassConstructorAdded.class, d::onClassConstructorAdded);
    add(types, CChangeClassConstructorRemoved.class, d::onClassConstructorRemoved);
    addClassFieldChanges(d, types);
    add(types, CChangeClassGenericsChanged.class, d::onClassGenericsChanged);
    addClassMethodChanges(d, types);
    add(types, CChangeClassRemovedPublic.class, d::onClassRemovedPublic);
    add(types, CChangeClassStaticInitializerAdded.class, d::onClassStaticInitializerAdded);
  }

  private static void addEnumChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeEnumAddedMembers.class, d::onEnumAddedMembers);
    add(types, CChangeEnumRemovedMembers.class, d::onEnumRemovedMembers);
  }

  private static void addClassFieldChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeClassFieldAddedPublic.class, d::onClassFieldAddedPublic);
    add(types, CChangeClassFieldBecameFinal.class, d::onClassFieldBecameFinal);
    add(types, CChangeClassFieldBecameLessAccessible.class, d::onClassFieldBecameLessAccessible);
    add(types, CChangeClassFieldBecameMoreAccessible.class, d::onClassFieldBecameMoreAccessible);
    add(types, CChangeClassFieldBecameNonFinal.class, d::onClassFieldBecameNonFinal);
    add(types, CChangeClassFieldBecameNonStatic.class, d::onClassFieldBecameNonStatic);
    add(types, CChangeClassFieldBecameStatic.class, d::onClassFieldBecameStatic);
    add(types, CChangeClassFieldMovedToSuperclass.class, d::onClassFieldMovedToSuperclass);
    add(types,
        CChangeClassFieldOverrideBecameLessAccessible.class,
        d::onChangeClassFieldOverrideBecameLessAccessible);
    add(types, CChangeClassFieldOverrideChangedStatic.class, d::onClassFieldOverrideChangedStatic);
    add(types, CChangeClassFieldRemovedPublic.class, d::onClassFieldRemovedPublic);
    add(types, CChangeClassFieldTypeChanged.class, d::onClassFieldTypeChanged);
  }

  private static void addModuleChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeModuleNoLongerRequired.class, d::onModuleNoLongerRequired);
    add(types,
        CChangeModulePackageNoLongerQualifiedExported.class, d::onModuleNoLongerQualifiedExported);
    add(types,
        CChangeModulePackageNoLongerQualifiedOpened.class, d::onModuleNoLongerQualifiedOpened);
    add(types,
        CChangeModulePackageNoLongerTransitivelyExported.class,
        d::onModuleNoLongerTransitivelyExported);
    add(types,
        CChangeModulePackageNoLongerUnqualifiedExported.class,
        d::onModuleNoLongerUnqualifiedExported);
    add(types,
        CChangeModulePackageNoLongerUnqualifiedOpened.class, d::onModuleNoLongerUnqualifiedOpened);
    add(types, CChangeModulePackageQualifiedExported.class, d::onModuleQualifiedExported);
    add(types, CChangeModulePackageQualifiedOpened.class, d::onModuleQualifiedOpened);
    add(types, CChangeModulePackageTransitivelyExported.class, d::onModuleTransitivelyExported);
    add(types, CChangeModulePackageUnqualifiedExported.class, d::onModuleUnqualifiedExported);
    add(types, CChangeModulePackageUnqualifiedOpened.class, d::onModuleUnqualifiedOpened);
    add(types, CChangeModuleRequired.class, d::onModuleRequired);
    add(types, CChangeModuleServiceNoLongerProvided.class, d::onModuleServiceNoLongerProvided);
    add(types, CChangeModuleServiceProvided.class, d::onModuleServiceProvided);
  }

  private static void addInterfaceMethodChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeInterfaceMethodAbstractAdded.class, d::onInterfaceMethodAbstractAdded);
    add(types, CChangeInterfaceMethodAbstractRemoved.class, d::onInterfaceMethodAbstractRemoved);
    add(types, CChangeInterfaceMethodDefaultAdded.class, d::onInterfaceMethodDefaultAdded);
    add(types, CChangeInterfaceMethodDefaultRemoved.class, d::onInterfaceMethodDefaultRemoved);
    add(types, CChangeInterfaceMethodStaticAdded.class, d::onInterfaceMethodStaticAdded);
    add(types, CChangeInterfaceMethodStaticRemoved.class, d::onInterfaceMethodStaticRemoved);
  }

  private static void addClassMethodChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeClassMethodAdded.class, d::onClassMethodAdded);
    add(types, CChangeClassMethodBecameLessAccessible.class, d::onClassMethodBecameLessAccessible);
    add(types, CChangeClassMethodBecameMoreAccessible.class, d::onClassMethodBecameMoreAccessible);
    add(types, CChangeClassMethodBecameFinal.class, d::onClassMethodBecameFinal);
    add(types, CChangeClassMethodBecameNonFinal.class, d::onClassMethodBecameNonFinal);
    add(types, CChangeClassMethodBecameVarArgs.class, d::onClassMethodBecameVarArgs);
    add(types, CChangeClassMethodBecameNonVarArgs.class, d::onClassMethodBecameNonVarArgs);
    add(types, CChangeClassMethodExceptionsChanged.class, d::onClassMethodExceptionsChanged);
    add(types, CChangeClassMethodMovedToSuperclass.class, d::onClassMethodMovedToSuperclass);
    add(types, CChangeClassMethodOverloadAdded.class, d::onClassMethodOverloadAdded);
    add(types, CChangeClassMethodOverloadRemoved.class, d::onClassMethodOverloadRemoved);
    add(types,
        CChangeClassMethodOverrideBecameLessAccessible.class,
        d::onClassMethodOverrideBecameLessAccessible);
    add(types,
        CChangeClassMethodOverrideChangedStatic.class,
        d::onClassMethodOverrideChangedStatic);
    add(types, CChangeClassMethodRemoved.class, d::onClassMethodRemoved);
  }

  private static void addClassModifierChanges(
    final CPlainTextDescriber d,
    final HashMap<Class<?>, DescriberType> types)
  {
    add(types, CChangeClassAddedPublic.class, d::onClassAddedPublic);
    add(types, CChangeClassBecameAbstract.class, d::onClassBecameAbstract);
    add(types, CChangeClassBecameEnum.class, d::onClassBecameEnum);
    add(types, CChangeClassBecameFinal.class, d::onClassBecameFinal);
    add(types, CChangeClassBecameInterface.class, d::onClassBecameInterface);
    add(types, CChangeClassBecameNonAbstract.class, d::onClassBecameNonAbstract);
    add(types, CChangeClassBecameNonEnum.class, d::onClassBecameNonEnum);
    add(types, CChangeClassBecameNonFinal.class, d::onClassBecameNonFinal);
    add(types, CChangeClassBecameNonInterface.class, d::onClassBecameNonInterface);
    add(types, CChangeClassBecameNonPublic.class, d::onClassBecameNonPublic);
    add(types, CChangeClassBecamePublic.class, d::onClassBecamePublic);
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
    final var cc = (CChangeClassGenericsChanged) c;
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
    final var cc = (CChangeClassMethodBecameLessAccessible) c;
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
    final var cc = (CChangeClassMethodBecameMoreAccessible) c;
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
    final var cc = (CChangeEnumAddedMembers) c;
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
    final var cc = (CChangeEnumRemovedMembers) c;
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
    final var cc = (CChangeModulePackageUnqualifiedOpened) c;
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
    final var cc = (CChangeModulePackageUnqualifiedExported) c;
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
    final var cc = (CChangeModulePackageTransitivelyExported) c;
    this.change(w, "a.module.is.now.transitively.exported.by.a.requires.directive");

    w.append(this.fieldStart("module.required"));
    w.append(cc.moduleRequired());
    w.newLine();
  }

  private void onModuleNoLongerRequired(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final var cc = (CChangeModuleNoLongerRequired) c;
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
    final var cc = (CChangeModulePackageNoLongerQualifiedExported) c;
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
    final var cc = (CChangeModulePackageNoLongerQualifiedOpened) c;
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
    final var cc = (CChangeModulePackageNoLongerTransitivelyExported) c;
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
    final var cc = (CChangeModulePackageNoLongerUnqualifiedExported) c;
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
    final var cc = (CChangeModulePackageNoLongerUnqualifiedOpened) c;
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
    final var cc = (CChangeModulePackageQualifiedExported) c;
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
    final var cc = (CChangeModulePackageQualifiedOpened) c;
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
    final var cc = (CChangeModuleRequired) c;
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
    final var cc = (CChangeModuleServiceProvided) c;
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
    final var cc = (CChangeModuleServiceNoLongerProvided) c;
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
    final var cc = (CChangeClassMethodOverrideBecameLessAccessible) c;
    final var method_ancestor = cc.methodAncestor();
    this.change(w, "a.method.declaration.reduces.the.accessibility.of.an.overridden.method");

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
    final var cc = (CChangeClassMethodOverrideChangedStatic) c;
    final var method_ancestor = cc.methodAncestor();
    final var method_current = cc.method();

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
    final var cc = (CChangeClassMethodOverloadRemoved) c;
    this.change(w, "a.method.overload.was.removed");
  }

  private void onClassMethodOverloadAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final var cc = (CChangeClassMethodOverloadAdded) c;
    this.change(w, "a.method.overload.was.added");
  }

  private void onClassMethodMovedToSuperclass(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final var cc = (CChangeClassMethodMovedToSuperclass) c;
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
    final var cc = (CChangeClassMethodExceptionsChanged) c;
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
    final var cc = (CChangeClassMethodBecameNonFinal) c;
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
    final var cc = (CChangeClassMethodBecameFinal) c;
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
    final var cc = (CChangeClassMethodBecameNonVarArgs) c;
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
    final var cc = (CChangeClassMethodBecameVarArgs) c;
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
    final var cc = (CChangeClassFieldTypeChanged) c;
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
    final var cc = (CChangeClassFieldMovedToSuperclass) c;
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
    final var cc = (CChangeClassFieldBecameStatic) c;
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
    final var cc = (CChangeClassFieldBecameNonStatic) c;
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
    final var cc = (CChangeClassFieldBecameNonFinal) c;
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
    final var cc = (CChangeClassFieldBecameMoreAccessible) c;
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
    final var cc = (CChangeClassFieldBecameLessAccessible) c;
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
    final var cc = (CChangeClassFieldBecameFinal) c;
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
    final var cc = (CChangeClassBytecodeVersionChanged) c;
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
    final var cc = (CChangeClassFieldOverrideBecameLessAccessible) c;
    final var field_ancestor = cc.fieldAncestor();
    this.change(w, "a.field.declaration.reduces.the.accessibility.of.an.overridden.field");

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
    final var cc = (CChangeClassFieldOverrideChangedStatic) c;
    final var field_ancestor = cc.fieldAncestor();
    final var field_current = cc.field();

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
        this.showChangeDetailsField((CChangeFieldType) change, w);
        break;
      }

      case CHANGE_CLASS: {
        this.showChangeDetailsClass((CChangeClassType) change, w);
        break;
      }

      case CHANGE_CONSTRUCTOR: {
        this.showChangeDetailsConstructor((CChangeConstructorType) change, w);
        break;
      }

      case CHANGE_METHOD: {
        this.showChangeDetailsMethod((CChangeMethodType) change, w);
        break;
      }

      case CHANGE_MODULE: {
        this.showChangeDetailsModule((CChangeModuleType) change, w);
        break;
      }

      case CHANGE_ENUM: {
        this.showChangeDetailsEnum((CChangeEnumType) change, w);
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

  private void showChangeDetailsEnum(
    final CChangeEnumType change,
    final BufferedWriter w)
    throws IOException
  {
    w.append(this.fieldStart("class"));
    w.append(CClassNames.show(change.enumType().name()));
    w.newLine();

    this.showGenerics(w, change.enumType().signature(), Revision.CURRENT);
  }

  private void showChangeDetailsModule(
    final CChangeModuleType change,
    final BufferedWriter w)
    throws IOException
  {
    w.append(this.fieldStart("module"));
    w.append(change.module());
    w.newLine();
  }

  private void showChangeDetailsMethod(
    final CChangeMethodType change,
    final BufferedWriter w)
    throws IOException
  {
    w.append(this.fieldStart("class"));
    w.append(CClassNames.show(change.className()));
    w.newLine();

    w.append(this.fieldStart("method.current"));
    w.append(CMethods.show(change.method()));
    w.newLine();
  }

  private void showChangeDetailsConstructor(
    final CChangeConstructorType change,
    final BufferedWriter w)
    throws IOException
  {
    w.append(this.fieldStart("class"));
    w.append(CClassNames.show(change.className()));
    w.newLine();

    w.append(this.fieldStart("constructor.current"));
    w.append(CConstructors.show(change.constructor()));
    w.newLine();
  }

  private void showChangeDetailsClass(
    final CChangeClassType change,
    final BufferedWriter w)
    throws IOException
  {
    w.append(this.fieldStart("class"));
    w.append(CClassNames.show(change.classValue().name()));
    w.newLine();

    this.showGenerics(w, change.classValue().signature(), Revision.CURRENT);
  }

  private void showChangeDetailsField(
    final CChangeFieldType change,
    final BufferedWriter w)
    throws IOException
  {
    final var f = change.field();

    w.append(this.fieldStart("class"));
    w.append(CClassNames.show(f.className()));
    w.newLine();

    w.append(this.fieldStart("field.current"));
    w.append(CFields.show(f));
    w.newLine();
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
      final var sig = sig_opt.get();
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
    final var title = this.titles.getString(Objects.requireNonNull(name, "Name"));
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

  private static void add(
    final HashMap<Class<?>, DescriberType> types,
    final Class<?> c,
    final DescriberType d)
  {
    Preconditions.checkPrecondition(
      !types.containsKey(c),
      "Describer for " + c + " must not already be registered");
    types.put(c, d);
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

    final var text =
      this.types.get(change.getClass());

    if (text == null) {
      throw new IllegalArgumentException(
        "No describer for type: " + change.getClass().getCanonicalName());
    }

    final var w = makeWriter(out);
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
