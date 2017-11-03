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
import com.io7m.cantoria.changes.spi.CChangeCheckType;
import com.io7m.cantoria.changes.spi.CChangeClassType;
import com.io7m.cantoria.changes.spi.CChangeConstructorType;
import com.io7m.cantoria.changes.spi.CChangeDescriberType;
import com.io7m.cantoria.changes.spi.CChangeEnumType;
import com.io7m.cantoria.changes.spi.CChangeFieldType;
import com.io7m.cantoria.changes.spi.CChangeMethodType;
import com.io7m.cantoria.changes.spi.CChangeModuleType;
import com.io7m.cantoria.changes.spi.CChangeType;
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
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnreachableCodeException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A plain text provider for the vanilla types.
 */

public final class CPlainTextDescriber implements CChangeDescriberType
{
  private final HashMap<Class<?>, DescriberType> types;

  /**
   * Construct a describer.
   */

  public CPlainTextDescriber()
  {
    this.types = new HashMap<>(64);
    this.add(
      CChangeClassAddedPublic.class,
      CPlainTextDescriber::onClassAddedPublic);
    this.add(
      CChangeClassBecameAbstract.class,
      CPlainTextDescriber::onClassBecameAbstract);
    this.add(
      CChangeClassBecameEnum.class,
      CPlainTextDescriber::onClassBecameEnum);
    this.add(
      CChangeClassBecameFinal.class,
      CPlainTextDescriber::onClassBecameFinal);
    this.add(
      CChangeClassBecameInterface.class,
      CPlainTextDescriber::onClassBecameInterface);
    this.add(
      CChangeClassBecameNonAbstract.class,
      CPlainTextDescriber::onClassBecameNonAbstract);
    this.add(
      CChangeClassBecameNonEnum.class,
      CPlainTextDescriber::onClassBecameNonEnum);
    this.add(
      CChangeClassBecameNonFinal.class,
      CPlainTextDescriber::onClassBecameNonFinal);
    this.add(
      CChangeClassBecameNonInterface.class,
      CPlainTextDescriber::onClassBecameNonInterface);
    this.add(
      CChangeClassBecameNonPublic.class,
      CPlainTextDescriber::onClassBecameNonPublic);
    this.add(
      CChangeClassBecamePublic.class,
      CPlainTextDescriber::onClassBecamePublic);

    this.add(
      CChangeClassBytecodeVersionChanged.class,
      CPlainTextDescriber::onClassBytecodeVersionChanged);

    this.add(
      CChangeClassConstructorAdded.class,
      CPlainTextDescriber::onClassConstructorAdded);
    this.add(
      CChangeClassConstructorRemoved.class,
      CPlainTextDescriber::onClassConstructorRemoved);

    this.add(
      CChangeClassFieldAddedPublic.class,
      CPlainTextDescriber::onClassFieldAddedPublic);
    this.add(
      CChangeClassFieldBecameFinal.class,
      CPlainTextDescriber::onClassFieldBecameFinal);
    this.add(
      CChangeClassFieldBecameLessAccessible.class,
      CPlainTextDescriber::onClassFieldBecameLessAccessible);
    this.add(
      CChangeClassFieldBecameMoreAccessible.class,
      CPlainTextDescriber::onClassFieldBecameMoreAccessible);
    this.add(
      CChangeClassFieldBecameNonFinal.class,
      CPlainTextDescriber::onClassFieldBecameNonFinal);
    this.add(
      CChangeClassFieldBecameNonStatic.class,
      CPlainTextDescriber::onClassFieldBecameNonStatic);
    this.add(
      CChangeClassFieldBecameStatic.class,
      CPlainTextDescriber::onClassFieldBecameStatic);
    this.add(
      CChangeClassFieldMovedToSuperclass.class,
      CPlainTextDescriber::onClassFieldMovedToSuperclass);
    this.add(
      CChangeClassFieldOverrideBecameLessAccessible.class,
      CPlainTextDescriber::onChangeClassFieldOverrideBecameLessAccessible);
    this.add(
      CChangeClassFieldOverrideChangedStatic.class,
      CPlainTextDescriber::onClassFieldOverrideChangedStatic);
    this.add(
      CChangeClassFieldRemovedPublic.class,
      CPlainTextDescriber::onClassFieldRemovedPublic);
    this.add(
      CChangeClassFieldTypeChanged.class,
      CPlainTextDescriber::onClassFieldTypeChanged);

    this.add(
      CChangeClassGenericsChanged.class,
      CPlainTextDescriber::onClassGenericsChanged);

    this.add(
      CChangeClassMethodAdded.class,
      CPlainTextDescriber::onClassMethodAdded);
    this.add(
      CChangeClassMethodBecameLessAccessible.class,
      CPlainTextDescriber::onClassMethodBecameLessAccessible);
    this.add(
      CChangeClassMethodBecameMoreAccessible.class,
      CPlainTextDescriber::onClassMethodBecameMoreAccessible);
    this.add(
      CChangeClassMethodBecameFinal.class,
      CPlainTextDescriber::onClassMethodBecameFinal);
    this.add(
      CChangeClassMethodBecameNonFinal.class,
      CPlainTextDescriber::onClassMethodBecameNonFinal);
    this.add(
      CChangeClassMethodBecameVarArgs.class,
      CPlainTextDescriber::onClassMethodBecameVarArgs);
    this.add(
      CChangeClassMethodBecameNonVarArgs.class,
      CPlainTextDescriber::onClassMethodBecameNonVarArgs);
    this.add(
      CChangeClassMethodExceptionsChanged.class,
      CPlainTextDescriber::onClassMethodExceptionsChanged);
    this.add(
      CChangeClassMethodMovedToSuperclass.class,
      CPlainTextDescriber::onClassMethodMovedToSuperclass);
    this.add(
      CChangeClassMethodOverloadAdded.class,
      CPlainTextDescriber::onClassMethodOverloadAdded);
    this.add(
      CChangeClassMethodOverloadRemoved.class,
      CPlainTextDescriber::onClassMethodOverloadRemoved);
    this.add(
      CChangeClassMethodOverrideBecameLessAccessible.class,
      CPlainTextDescriber::onClassMethodOverrideBecameLessAccessible);
    this.add(
      CChangeClassMethodOverrideChangedStatic.class,
      CPlainTextDescriber::onClassMethodOverrideChangedStatic);
    this.add(
      CChangeClassMethodRemoved.class,
      CPlainTextDescriber::onClassMethodRemoved);

    this.add(
      CChangeClassRemovedPublic.class,
      CPlainTextDescriber::onClassRemovedPublic);
    this.add(
      CChangeClassStaticInitializerAdded.class,
      CPlainTextDescriber::onClassStaticInitializerAdded);

    this.add(
      CChangeEnumAddedMembers.class,
      CPlainTextDescriber::onEnumAddedMembers);
    this.add(
      CChangeEnumRemovedMembers.class,
      CPlainTextDescriber::onEnumRemovedMembers);

    this.add(
      CChangeInterfaceMethodAbstractAdded.class,
      CPlainTextDescriber::onInterfaceMethodAbstractAdded);
    this.add(
      CChangeInterfaceMethodAbstractRemoved.class,
      CPlainTextDescriber::onInterfaceMethodAbstractRemoved);
    this.add(
      CChangeInterfaceMethodDefaultAdded.class,
      CPlainTextDescriber::onInterfaceMethodDefaultAdded);
    this.add(
      CChangeInterfaceMethodDefaultRemoved.class,
      CPlainTextDescriber::onInterfaceMethodDefaultRemoved);
    this.add(
      CChangeInterfaceMethodStaticAdded.class,
      CPlainTextDescriber::onInterfaceMethodStaticAdded);
    this.add(
      CChangeInterfaceMethodStaticRemoved.class,
      CPlainTextDescriber::onInterfaceMethodStaticRemoved);

    this.add(
      CChangeModuleNoLongerRequired.class,
      CPlainTextDescriber::onModuleNoLongerRequired);
    this.add(
      CChangeModulePackageNoLongerQualifiedExported.class,
      CPlainTextDescriber::onModuleNoLongerQualifiedExported);
    this.add(
      CChangeModulePackageNoLongerQualifiedOpened.class,
      CPlainTextDescriber::onModuleNoLongerQualifiedOpened);
    this.add(
      CChangeModulePackageNoLongerTransitivelyExported.class,
      CPlainTextDescriber::onModuleNoLongerTransitivelyExported);
    this.add(
      CChangeModulePackageNoLongerUnqualifiedExported.class,
      CPlainTextDescriber::onModuleNoLongerUnqualifiedExported);
    this.add(
      CChangeModulePackageNoLongerUnqualifiedOpened.class,
      CPlainTextDescriber::onModuleNoLongerUnqualifiedOpened);
    this.add(
      CChangeModulePackageQualifiedExported.class,
      CPlainTextDescriber::onModuleQualifiedExported);
    this.add(
      CChangeModulePackageQualifiedOpened.class,
      CPlainTextDescriber::onModuleQualifiedOpened);
    this.add(
      CChangeModulePackageTransitivelyExported.class,
      CPlainTextDescriber::onModuleTransitivelyExported);
    this.add(
      CChangeModulePackageUnqualifiedExported.class,
      CPlainTextDescriber::onModuleUnqualifiedExported);
    this.add(
      CChangeModulePackageUnqualifiedOpened.class,
      CPlainTextDescriber::onModuleUnqualifiedOpened);
    this.add(
      CChangeModuleRequired.class,
      CPlainTextDescriber::onModuleRequired);
    this.add(
      CChangeModuleServiceNoLongerProvided.class,
      CPlainTextDescriber::onModuleServiceNoLongerProvided);
    this.add(
      CChangeModuleServiceProvided.class,
      CPlainTextDescriber::onModuleServiceProvided);
  }

  private static void onClassGenericsChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassGenericsChanged cc =
      (CChangeClassGenericsChanged) c;

    w.append(fieldStart("Change"));
    w.append("The generic type parameters of a class have changed");
    w.newLine();

    showGenerics(w, cc.classPrevious().signature(), "(Then)");
  }

  private static void onClassMethodBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameLessAccessible cc =
      (CChangeClassMethodBecameLessAccessible) c;

    w.append(fieldStart("Change"));
    w.append("A method became less accessible");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassMethodBecameMoreAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameMoreAccessible cc =
      (CChangeClassMethodBecameMoreAccessible) c;

    w.append(fieldStart("Change"));
    w.append("A method became more accessible");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassBecameEnum(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassBecameEnum cc = (CChangeClassBecameEnum) c;

    w.append(fieldStart("Change"));
    w.append("A class became an enum");
    w.newLine();
  }

  private static void onClassBecameNonEnum(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassBecameNonEnum cc = (CChangeClassBecameNonEnum) c;

    w.append(fieldStart("Change"));
    w.append("An enum was changed to a non-enum type");
    w.newLine();
  }

  private static void onEnumAddedMembers(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeEnumAddedMembers cc = (CChangeEnumAddedMembers) c;

    w.append(fieldStart("Change"));
    w.append("One or more members/cases were added to an enum");
    w.newLine();

    w.append(fieldStart("Added members"));
    w.append(cc.addedMembers().collect(Collectors.joining(",")));
    w.newLine();
  }

  private static void onEnumRemovedMembers(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeEnumRemovedMembers cc = (CChangeEnumRemovedMembers) c;

    w.append(fieldStart("Change"));
    w.append("One or more members/cases were removed from an enum");
    w.newLine();

    w.append(fieldStart("Removed members"));
    w.append(cc.removedMembers().collect(Collectors.joining(",")));
    w.newLine();
  }

  private static void onModuleUnqualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageUnqualifiedOpened cc =
      (CChangeModulePackageUnqualifiedOpened) c;

    w.append(fieldStart("Change"));
    w.append("A package is now opened unqualified");
    w.newLine();

    w.append(fieldStart("Opened package"));
    w.append(cc.packageName());
    w.newLine();
  }

  private static void onModuleUnqualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageUnqualifiedExported cc =
      (CChangeModulePackageUnqualifiedExported) c;

    w.append(fieldStart("Change"));
    w.append("A package is now exported unqualified");
    w.newLine();

    w.append(fieldStart("Exported package"));
    w.append(cc.packageName());
    w.newLine();
  }

  private static void onModuleTransitivelyExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageTransitivelyExported cc =
      (CChangeModulePackageTransitivelyExported) c;

    w.append(fieldStart("Change"));
    w.append("A module is now transitively exported by a 'requires' directive");
    w.newLine();

    w.append(fieldStart("Required module"));
    w.append(cc.moduleRequired());
    w.newLine();
  }

  private static void onModuleNoLongerRequired(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleNoLongerRequired cc =
      (CChangeModuleNoLongerRequired) c;

    w.append(fieldStart("Change"));
    w.append("A module no longer requires another module");
    w.newLine();

    w.append(fieldStart("Required module"));
    w.append(cc.moduleTarget());
    w.append(cc.isTransitive() ? " (transitive)" : "");
    w.newLine();
  }

  private static void onModuleNoLongerQualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerQualifiedExported cc =
      (CChangeModulePackageNoLongerQualifiedExported) c;

    w.append(fieldStart("Change"));
    w.append("A package is no longer exported qualified by a module");
    w.newLine();

    w.append(fieldStart("Exported package"));
    w.append(cc.packageName());
    w.append(" to ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private static void onModuleNoLongerQualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerQualifiedOpened cc =
      (CChangeModulePackageNoLongerQualifiedOpened) c;

    w.append(fieldStart("Change"));
    w.append("A package is no longer opened qualified by a module");
    w.newLine();

    w.append(fieldStart("Opened package"));
    w.append(cc.packageName());
    w.append(" to ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private static void onModuleNoLongerTransitivelyExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerTransitivelyExported cc =
      (CChangeModulePackageNoLongerTransitivelyExported) c;

    w.append(fieldStart("Change"));
    w.append("A package is no transitively exported by a module");
    w.newLine();

    w.append(fieldStart("Exported module"));
    w.append(cc.moduleRequired());
    w.newLine();
  }

  private static void onModuleNoLongerUnqualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerUnqualifiedExported cc =
      (CChangeModulePackageNoLongerUnqualifiedExported) c;

    w.append(fieldStart("Change"));
    w.append("A package is no longer exported unqualified by a module");
    w.newLine();

    w.append(fieldStart("Exported package"));
    w.append(cc.packageName());
    w.newLine();
  }

  private static void onModuleNoLongerUnqualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageNoLongerUnqualifiedOpened cc =
      (CChangeModulePackageNoLongerUnqualifiedOpened) c;

    w.append(fieldStart("Change"));
    w.append("A package is no longer opened unqualified by a module");
    w.newLine();

    w.append(fieldStart("Opened package"));
    w.append(cc.packageName());
    w.newLine();
  }

  private static void onModuleQualifiedExported(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageQualifiedExported cc =
      (CChangeModulePackageQualifiedExported) c;

    w.append(fieldStart("Change"));
    w.append("A package is exported qualified by a module");
    w.newLine();

    w.append(fieldStart("Exported package"));
    w.append(cc.packageName());
    w.append(" to ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private static void onModuleQualifiedOpened(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModulePackageQualifiedOpened cc =
      (CChangeModulePackageQualifiedOpened) c;

    w.append(fieldStart("Change"));
    w.append("A package is opened qualified by a module");
    w.newLine();

    w.append(fieldStart("Opened package"));
    w.append(cc.packageName());
    w.append(" to ");
    w.append(cc.moduleTarget());
    w.newLine();
  }

  private static void onModuleRequired(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleRequired cc =
      (CChangeModuleRequired) c;

    w.append(fieldStart("Change"));
    w.append("A module now requires another module");
    w.newLine();

    w.append(fieldStart("Required module"));
    w.append(cc.moduleTarget());
    w.append(cc.isTransitive() ? " (transitive)" : "");
    w.newLine();
  }

  private static void onModuleServiceProvided(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleServiceProvided cc =
      (CChangeModuleServiceProvided) c;

    w.append(fieldStart("Change"));
    w.append("A module now provides a service");
    w.newLine();

    w.append(fieldStart("Provided service"));
    w.append(cc.provides().service());
    w.append(" with ");
    w.append(cc.provides().provider());
    w.newLine();
  }

  private static void onModuleServiceNoLongerProvided(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeModuleServiceNoLongerProvided cc =
      (CChangeModuleServiceNoLongerProvided) c;

    w.append(fieldStart("Change"));
    w.append("A module no longer provides a service");
    w.newLine();

    w.append(fieldStart("Provided service"));
    w.append(cc.provides().service());
    w.append(" with ");
    w.append(cc.provides().provider());
    w.newLine();
  }

  private static void onInterfaceMethodAbstractAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("An abstract method was added to an interface");
    w.newLine();
  }

  private static void onInterfaceMethodAbstractRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("An abstract method was removed from an interface");
    w.newLine();
  }

  private static void onInterfaceMethodDefaultAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A default method was added to an interface");
    w.newLine();
  }

  private static void onInterfaceMethodDefaultRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A default method was removed from an interface");
    w.newLine();
  }

  private static void onInterfaceMethodStaticAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A static method was added to an interface");
    w.newLine();
  }

  private static void onInterfaceMethodStaticRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A static method was removed from an interface");
    w.newLine();
  }

  private static void onClassMethodRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private method was removed");
    w.newLine();
  }

  private static void onClassMethodOverrideBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverrideBecameLessAccessible cc =
      (CChangeClassMethodOverrideBecameLessAccessible) c;

    final CMethod method_ancestor = cc.methodAncestor();
    w.append(fieldStart("Change"));
    w.append(
      "A method declaration reduces the accessibility of an overridden method");
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(method_ancestor.className()));
    w.newLine();
    w.append(fieldStart("Ancestor method"));
    w.append(CMethods.show(method_ancestor));
    w.newLine();
  }

  private static void onClassMethodOverrideChangedStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverrideChangedStatic cc =
      (CChangeClassMethodOverrideChangedStatic) c;

    final CMethod method_ancestor = cc.methodAncestor();
    final CMethod method_current = cc.method();

    w.append(fieldStart("Change"));
    if (method_current.modifiers().contains(CModifier.STATIC)
      && !method_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("A static method attempts to override a non-static method");
    }

    if (!method_current.modifiers().contains(CModifier.STATIC)
      && method_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("A non-static method attempts to override a static method");
    }
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(method_ancestor.className()));
    w.newLine();
    w.append(fieldStart("Ancestor method"));
    w.append(CMethods.show(method_ancestor));
    w.newLine();
  }

  private static void onClassMethodOverloadRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverloadRemoved cc =
      (CChangeClassMethodOverloadRemoved) c;

    w.append(fieldStart("Change"));
    w.append("A method overload was removed");
    w.newLine();
  }

  private static void onClassMethodOverloadAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodOverloadAdded cc =
      (CChangeClassMethodOverloadAdded) c;

    w.append(fieldStart("Change"));
    w.append("A method overload was added");
    w.newLine();
  }

  private static void onClassMethodMovedToSuperclass(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodMovedToSuperclass cc =
      (CChangeClassMethodMovedToSuperclass) c;

    w.append(fieldStart("Change"));
    w.append("A method was moved to an ancestor class");
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(cc.methodAncestor().className()));
    w.newLine();

    w.append(fieldStart("Ancestor method"));
    w.append(CMethods.show(cc.methodAncestor()));
    w.newLine();
  }

  private static void onClassMethodExceptionsChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodExceptionsChanged cc =
      (CChangeClassMethodExceptionsChanged) c;

    w.append(fieldStart("Change"));
    w.append("The declared thrown exceptions for a method have changed");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassMethodBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameNonFinal cc =
      (CChangeClassMethodBecameNonFinal) c;

    w.append(fieldStart("Change"));
    w.append("A previously final method became non-final");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassMethodBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameFinal cc =
      (CChangeClassMethodBecameFinal) c;

    w.append(fieldStart("Change"));
    w.append("A previously non-final method became final");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassMethodBecameNonVarArgs(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameNonVarArgs cc =
      (CChangeClassMethodBecameNonVarArgs) c;

    w.append(fieldStart("Change"));
    w.append("A previously final method became non-variadic");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassMethodBecameVarArgs(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassMethodBecameVarArgs cc =
      (CChangeClassMethodBecameVarArgs) c;

    w.append(fieldStart("Change"));
    w.append("A previously non-final method became variadic");
    w.newLine();

    w.append(fieldStart("Previous method"));
    w.append(CMethods.show(cc.methodPrevious()));
    w.newLine();
  }

  private static void onClassFieldTypeChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldTypeChanged cc =
      (CChangeClassFieldTypeChanged) c;

    w.append(fieldStart("Change"));
    w.append("The type of a non-private field changed");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldRemovedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private field was removed");
    w.newLine();
  }

  private static void onClassFieldMovedToSuperclass(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldMovedToSuperclass cc =
      (CChangeClassFieldMovedToSuperclass) c;

    w.append(fieldStart("Change"));
    w.append("A field moved to an ancestor class");
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(cc.fieldAncestor().className()));
    w.newLine();

    w.append(fieldStart("Ancestor field"));
    w.append(CFields.show(cc.fieldAncestor()));
    w.newLine();
  }

  private static void onClassFieldBecameStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameStatic cc =
      (CChangeClassFieldBecameStatic) c;

    w.append(fieldStart("Change"));
    w.append("A previously non-static field became static");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldBecameNonStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameNonStatic cc =
      (CChangeClassFieldBecameNonStatic) c;

    w.append(fieldStart("Change"));
    w.append("A previously static field became non-static");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameNonFinal cc =
      (CChangeClassFieldBecameNonFinal) c;

    w.append(fieldStart("Change"));
    w.append("A previously final field became non-final");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldBecameMoreAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameMoreAccessible cc =
      (CChangeClassFieldBecameMoreAccessible) c;

    w.append(fieldStart("Change"));
    w.append("The accessibility of a field was increased");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameLessAccessible cc =
      (CChangeClassFieldBecameLessAccessible) c;

    w.append(fieldStart("Change"));
    w.append("The accessibility of a field was reduced");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldBecameFinal cc =
      (CChangeClassFieldBecameFinal) c;

    w.append(fieldStart("Change"));
    w.append("A non-final field became final");
    w.newLine();

    w.append(fieldStart("Previous field"));
    w.append(CFields.show(cc.fieldPrevious()));
    w.newLine();
  }

  private static void onClassFieldAddedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private field was added");
    w.newLine();
  }

  private static void onClassBytecodeVersionChanged(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassBytecodeVersionChanged cc =
      (CChangeClassBytecodeVersionChanged) c;

    w.append(fieldStart("Change"));
    w.append("The bytecode version of a public class has changed");
    w.newLine();

    w.append(fieldStart("Previous bytecode"));
    w.append(bytecodeVersion(cc.classPrevious().bytecodeVersion()));
    w.newLine();

    w.append(fieldStart("Current bytecode"));
    w.append(bytecodeVersion(cc.classValue().bytecodeVersion()));
    w.newLine();
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

  private static void onClassAddedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A public class was added to an exported package");
    w.newLine();
  }

  private static void onClassRemovedPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A public class was removed from an exported package");
    w.newLine();
  }

  private static void onClassBecameAbstract(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-abstract class was made abstract");
    w.newLine();
  }

  private static void onClassBecamePublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-public class was made public");
    w.newLine();
  }

  private static void onClassBecameFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-final class was made final");
    w.newLine();
  }

  private static void onClassBecameInterface(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-interface class was changed into an interface");
    w.newLine();
  }

  private static void onClassBecameNonAbstract(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("An abstract class was made non-abstract");
    w.newLine();
  }

  private static void onClassBecameNonFinal(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A final class was made non-final");
    w.newLine();
  }

  private static void onClassBecameNonInterface(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("An interface was changed into a non-interface class");
    w.newLine();
  }

  private static void onClassBecameNonPublic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A public class was made non-public");
    w.newLine();
  }

  private static void onClassConstructorAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private constructor was added");
    w.newLine();
  }

  private static void onClassConstructorRemoved(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private constructor was removed");
    w.newLine();
  }

  private static void onClassMethodAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A non-private method was added");
    w.newLine();
  }

  private static void onClassStaticInitializerAdded(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    w.append(fieldStart("Change"));
    w.append("A static initializer was added");
    w.newLine();
  }

  private static void onChangeClassFieldOverrideBecameLessAccessible(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldOverrideBecameLessAccessible cc =
      (CChangeClassFieldOverrideBecameLessAccessible) c;

    final CField field_ancestor = cc.fieldAncestor();
    w.append(fieldStart("Change"));
    w.append(
      "A field declaration reduces the accessibility of an overridden field");
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(field_ancestor.className()));
    w.newLine();
    w.append(fieldStart("Ancestor field"));
    w.append(CFields.show(field_ancestor));
    w.newLine();
  }

  private static void onClassFieldOverrideChangedStatic(
    final BufferedWriter w,
    final CChangeType c)
    throws IOException
  {
    final CChangeClassFieldOverrideChangedStatic cc =
      (CChangeClassFieldOverrideChangedStatic) c;

    final CField field_ancestor = cc.fieldAncestor();
    final CField field_current = cc.field();

    w.append(fieldStart("Change"));
    if (field_current.modifiers().contains(CModifier.STATIC)
      && !field_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("A static field attempts to override a non-static field");
    }

    if (!field_current.modifiers().contains(CModifier.STATIC)
      && field_ancestor.modifiers().contains(CModifier.STATIC)) {
      w.append("A non-static field attempts to override a static field");
    }
    w.newLine();

    w.append(fieldStart("Ancestor class"));
    w.append(CClassNames.show(field_ancestor.className()));
    w.newLine();
    w.append(fieldStart("Ancestor field"));
    w.append(CFields.show(field_ancestor));
    w.newLine();
  }

  private static BufferedWriter makeWriter(
    final OutputStream out)
  {
    return new BufferedWriter(new OutputStreamWriter(out, UTF_8));
  }

  private static void showChangeDetails(
    final CChangeCheckType originator,
    final CChangeType change,
    final BufferedWriter w)
    throws IOException
  {
    switch (change.category()) {
      case CHANGE_FIELD: {
        final CChangeFieldType c = (CChangeFieldType) change;
        final CField f = c.field();

        w.append(fieldStart("Class"));
        w.append(CClassNames.show(f.className()));
        w.newLine();

        w.append(fieldStart("Field"));
        w.append(CFields.show(f));
        w.newLine();
        break;
      }

      case CHANGE_CLASS: {
        final CChangeClassType c = (CChangeClassType) change;

        w.append(fieldStart("Class"));
        w.append(CClassNames.show(c.classValue().name()));
        w.newLine();

        showGenerics(w, c.classValue().signature(), "(Now)");
        break;
      }

      case CHANGE_CONSTRUCTOR: {
        final CChangeConstructorType c = (CChangeConstructorType) change;

        w.append(fieldStart("Class"));
        w.append(CClassNames.show(c.className()));
        w.newLine();

        w.append(fieldStart("Constructor"));
        w.append(CConstructors.show(c.constructor()));
        w.newLine();
        break;
      }

      case CHANGE_METHOD: {
        final CChangeMethodType c = (CChangeMethodType) change;

        w.append(fieldStart("Class"));
        w.append(CClassNames.show(c.className()));
        w.newLine();

        w.append(fieldStart("Method"));
        w.append(CMethods.show(c.method()));
        w.newLine();
        break;
      }

      case CHANGE_MODULE: {
        final CChangeModuleType c = (CChangeModuleType) change;

        w.append(fieldStart("Module"));
        w.append(c.module());
        w.newLine();
        break;
      }

      case CHANGE_ENUM: {
        final CChangeEnumType c = (CChangeEnumType) change;

        w.append(fieldStart("Class"));
        w.append(CClassNames.show(c.enumType().name()));
        w.newLine();

        showGenerics(w, c.enumType().signature(), "(Now)");
        break;
      }
    }

    w.append(fieldStart("Compatibility"));
    w.append(compatibility(change));
    w.newLine();

    w.append(fieldStart("Semantic versioning"));
    w.append(semanticVersioning(change));
    w.newLine();

    w.append(fieldStart("Check"));
    w.append(originator.name());
    w.newLine();

    if (!originator.jlsReferences().isEmpty()) {
      w.append(fieldStart("Specifications"));
      w.append(originator.jlsReferences().collect(Collectors.joining(",")));
      w.newLine();
    }
  }

  private static void showGenerics(
    final BufferedWriter w,
    final Optional<CGClassSignature> sig_opt,
    final String period)
    throws IOException
  {
    w.append(fieldStart("Generic type parameters " + period));
    if (sig_opt.isPresent()) {
      final CGClassSignature sig = sig_opt.get();
      if (!sig.parameters().isEmpty()) {
        w.append("<");
        w.append(sig.parameters()
                   .map(CGenericsType.CGTypeParameterType::toJava)
                   .collect(Collectors.joining(",")));
        w.append(">");
      } else {
        w.append("(None)");
      }
    } else {
      w.append("(None)");
    }
    w.newLine();
  }

  private static String fieldStart(
    final String name)
  {
    NullCheck.notNull(name, "Name");
    return String.format("%-32s ", name + ":");
  }

  private static String semanticVersioning(
    final CChangeType change)
  {
    switch (change.semanticVersioning()) {
      case SEMANTIC_MAJOR:
        return "Requires major version number increment";
      case SEMANTIC_MINOR:
        return "Requires minor version number increment";
      case SEMANTIC_NONE:
        return "Requires no version number increment";
    }

    throw new UnreachableCodeException();
  }

  private static String compatibility(
    final CChangeType change)
  {
    switch (change.binaryCompatibility()) {
      case BINARY_COMPATIBLE: {
        switch (change.sourceCompatibility()) {
          case SOURCE_COMPATIBLE: {
            return "Source and binary compatible";
          }
          case SOURCE_INCOMPATIBLE: {
            return "Binary compatible but source incompatible";
          }
        }
        break;
      }
      case BINARY_INCOMPATIBLE: {
        switch (change.sourceCompatibility()) {
          case SOURCE_COMPATIBLE: {
            return "Source compatible but binary incompatible";
          }
          case SOURCE_INCOMPATIBLE: {
            return "Source and binary incompatible";
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
    NullCheck.notNull(originator, "Originator");
    NullCheck.notNull(change, "Change");
    NullCheck.notNull(out, "Output");

    final DescriberType text =
      this.types.get(change.getClass());

    if (text == null) {
      throw new IllegalArgumentException(
        "No describer for type: " + change.getClass().getCanonicalName());
    }

    final BufferedWriter w = makeWriter(out);
    text.describe(w, change);
    showChangeDetails(originator, change, w);
    w.newLine();
    w.flush();
  }

  private interface DescriberType
  {
    void describe(
      BufferedWriter w,
      CChangeType c)
      throws IOException;
  }
}
