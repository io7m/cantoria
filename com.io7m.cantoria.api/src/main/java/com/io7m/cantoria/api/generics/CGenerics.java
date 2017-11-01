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

package com.io7m.cantoria.api.generics;

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CImmutableStyleType;
import com.io7m.cantoria.api.CShowJavaType;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.io7m.cantoria.api.generics.CGenerics.CReferenceType.Kind.REFERENCE_ARRAY;
import static com.io7m.cantoria.api.generics.CGenerics.CReferenceType.Kind.REFERENCE_CLASS;
import static com.io7m.cantoria.api.generics.CGenerics.CReferenceType.Kind.REFERENCE_VARIABLE;
import static com.io7m.cantoria.api.generics.CGenerics.CTypeArgumentType.Kind.TYPE_ARGUMENT_REFERENCE;
import static com.io7m.cantoria.api.generics.CGenerics.CTypeArgumentType.Kind.TYPE_ARGUMENT_WILDCARD;
import static com.io7m.cantoria.api.generics.CGenerics.CTypeBoundType.Kind.BOUND_CLASS;
import static com.io7m.cantoria.api.generics.CGenerics.CTypeBoundType.Kind.BOUND_VARIABLE;
import static com.io7m.cantoria.api.generics.CGenerics.CWildcardType.Kind.WILDCARD_EXTENDS;
import static com.io7m.cantoria.api.generics.CGenerics.CWildcardType.Kind.WILDCARD_SUPER;

/**
 * Functions for processing generic type parameters.
 */

public final class CGenerics
{
  private static final Logger LOG = LoggerFactory.getLogger(CGenerics.class);

  private CGenerics()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Parse the given type signature into a list of type parameters. The type
   * signature is assumed to have been taken from a class with type parameters.
   *
   * @param in_registry  A class registry
   * @param in_signature The type signature
   *
   * @return A list of type parameters
   */

  public static List<CTypeParameter> parseClassGenericParameters(
    final CClassRegistryType in_registry,
    final String in_signature)
  {
    NullCheck.notNull(in_registry, "Registry");
    NullCheck.notNull(in_signature, "Signature");

    LOG.trace("parseClassGenericParameters: {}", in_signature);

    final ClassGenericVisitor visitor = new ClassGenericVisitor(in_registry);
    new SignatureReader(in_signature).accept(visitor);
    return visitor.completed();
  }

  private static CClass lookupClass(
    final CClassRegistryType in_registry,
    final String name)
  {
    try {
      final Tuple2<String, String> pair =
        CClassNames.parseFullyQualifiedDotted(
          CClassNames.toDottedName(name));

      final Optional<CClass> class_opt =
        in_registry.findClass(pair._1, pair._2);

      if (!class_opt.isPresent()) {
        throw new UnimplementedCodeException();
      }

      return class_opt.get();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private enum ClassBoundKind
  {
    CLASS_BOUND,
    INTERFACE_BOUND
  }

  /**
   * A type parameter.
   *
   * <blockquote>A type variable is an unqualified identifier used as a type in
   * class, interface, method, and constructor bodies.
   *
   * A type variable is introduced by the declaration of a type parameter of a
   * generic class, interface, method, or constructor</blockquote>
   *
   * See JLS 9 §4.4
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeParameterType extends CShowJavaType
  {
    /**
     * @return The type parameter name
     */

    @Value.Parameter
    String name();

    /**
     * @return The type parameter bound, if any
     */

    @Value.Parameter
    Optional<CTypeBoundType> bound();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append(this.name());
      this.bound().ifPresent(b -> {
        sb.append(" ");
        sb.append(b.toJava());
      });
      return sb.toString();
    }
  }

  /**
   * A type bound.
   *
   * <blockquote>Every type variable declared as a type parameter has a bound.
   * If no bound is declared for a type variable, Object is
   * assumed.</blockquote>
   *
   * See JLS 9 §4.4
   */

  public interface CTypeBoundType extends CShowJavaType
  {
    /**
     * @return The precise kind of type bound
     */

    Kind kind();

    /**
     * The different kinds of type bounds.
     */

    enum Kind
    {
      /**
       * The bound is based on a variable.
       */

      BOUND_VARIABLE,

      /**
       * The bound is based on a class (or interface).
       */

      BOUND_CLASS
    }
  }

  /**
   * A type bound based on a variable.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeBoundVariableType extends CTypeBoundType
  {
    /**
     * @return The bound type variable
     */

    @Value.Parameter
    CTypeVariable variable();

    @Override
    default Kind kind()
    {
      return BOUND_VARIABLE;
    }

    @Override
    default String toJava()
    {
      return new StringBuilder(64)
        .append("extends ")
        .append(this.variable().toJava())
        .toString();
    }
  }

  /**
   * A type bound based on a class (or interface).
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeBoundClassType extends CTypeBoundType
  {
    /**
     * @return The bound class
     */

    @Value.Parameter
    CGenericClass classType();

    /**
     * @return The extra bound intersection types
     */

    @Value.Parameter
    List<CGenericClass> intersections();

    @Override
    default Kind kind()
    {
      return BOUND_CLASS;
    }

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append("extends ");
      sb.append(this.classType().toJava());
      this.intersections().forEach(c -> {
        sb.append(" & ");
        sb.append(c.toJava());
      });
      return sb.toString();
    }
  }

  /**
   * A reference to a generic type.
   *
   * <blockquote>There are four kinds of reference types: class types (§8.1),
   * interface types (§9.1), type variables (§4.4), and array types
   * (§10.1).</blockquote>
   *
   * See JLS 9 §4.3
   */

  public interface CReferenceType extends CShowJavaType
  {
    /**
     * @return The kind of reference type
     */

    Kind kind();

    /**
     * The different kinds of reference types.
     */

    enum Kind
    {
      /**
       * A reference to a class (or interface)
       */

      REFERENCE_CLASS,

      /**
       * A reference to a variable
       */

      REFERENCE_VARIABLE,

      /**
       * A reference to an array
       */

      REFERENCE_ARRAY
    }
  }

  /**
   * A reference to a class.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CReferenceClassType extends CReferenceType
  {
    /**
     * @return The referred class
     */

    @Value.Parameter
    CGenericClassType genericClass();

    @Override
    default Kind kind()
    {
      return REFERENCE_CLASS;
    }

    @Override
    default String toJava()
    {
      return this.genericClass().toJava();
    }
  }

  /**
   * A reference to a variable.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CReferenceVariableType extends CReferenceType
  {
    /**
     * @return The referred variable
     */

    @Value.Parameter
    CTypeVariable variable();

    @Override
    default Kind kind()
    {
      return REFERENCE_VARIABLE;
    }

    @Override
    default String toJava()
    {
      return this.variable().toJava();
    }
  }

  /**
   * A reference to an array.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CReferenceArrayType extends CReferenceType
  {
    /**
     * @return The referred array
     */

    @Value.Parameter
    CGenericArrayType array();

    @Override
    default Kind kind()
    {
      return REFERENCE_ARRAY;
    }

    @Override
    default String toJava()
    {
      return this.array().toJava();
    }
  }

  /**
   * A class (or interface) type.
   *
   * See JLS 9 §4.3
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CGenericClassType extends CShowJavaType
  {
    /**
     * @return The name of the class
     */

    @Value.Parameter
    CClassName name();

    /**
     * @return The type arguments
     */

    @Value.Parameter
    List<CTypeArgumentType> arguments();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append(this.name().packageName());
      sb.append(".");
      sb.append(this.name().className());
      if (!this.arguments().isEmpty()) {
        sb.append("<");
        sb.append(
          this.arguments()
            .map(CShowJavaType::toJava)
            .collect(Collectors.joining(",")));
        sb.append(">");
      }
      return sb.toString();
    }
  }

  /**
   * An array type.
   *
   * See JLS 9 §4.3
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CGenericArrayType extends CShowJavaType
  {
    /**
     * @return TODO: Return something sensible
     */

    @Value.Parameter
    Object placeholder();

    @Override
    default String toJava()
    {
      // TODO: Generated method stub
      throw new UnimplementedCodeException();
    }
  }

  /**
   * The type of wildcards.
   *
   * <blockquote> Wildcards may be given explicit bounds, just like regular type
   * variable declarations. </blockquote>
   *
   * See JLS 9 §4.5.1
   */

  public interface CWildcardType extends CShowJavaType
  {
    /**
     * @return The kind of wildcard
     */

    Kind kind();

    /**
     * The different kind of wildcards
     */

    enum Kind
    {
      /**
       * An upper bound
       */

      WILDCARD_EXTENDS,

      /**
       * A lower bound
       */

      WILDCARD_SUPER
    }
  }

  /**
   * A wildcard representing an upper bound.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CWildcardExtendsType extends CWildcardType
  {
    /**
     * @return The upper bound
     */

    @Value.Parameter
    CReferenceType reference();

    @Override
    default Kind kind()
    {
      return WILDCARD_EXTENDS;
    }

    @Override
    default String toJava()
    {
      return new StringBuilder(64)
        .append("? extends ")
        .append(this.reference().toJava())
        .toString();
    }
  }

  /**
   * A wildcard representing a lower bound.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CWildcardSuperType extends CWildcardType
  {
    /**
     * @return The lower bound
     */

    @Value.Parameter
    CReferenceType reference();

    @Override
    default Kind kind()
    {
      return WILDCARD_SUPER;
    }

    @Override
    default String toJava()
    {
      return new StringBuilder(64)
        .append("? super ")
        .append(this.reference().toJava())
        .toString();
    }
  }

  /**
   * A type variable.
   *
   * <blockquote>A type variable is introduced by the declaration of a type
   * parameter of a generic class, interface, method, or constructor (§8.1.2,
   * §9.1.2, §8.4.4, §8.8.4).</blockquote>
   *
   * See JLS 9 §4.4
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeVariableType extends CShowJavaType
  {
    /**
     * @return The name of the type variable
     */

    @Value.Parameter
    String name();

    @Override
    default String toJava()
    {
      return this.name();
    }
  }

  /**
   * A type argument.
   *
   * <blockquote>Type arguments may be either reference types or wildcards.
   * Wildcards are useful in situations where only partial knowledge about the
   * type parameter is required.</blockquote>
   *
   * See JLS 9 §4.5.1
   */

  public interface CTypeArgumentType extends CShowJavaType
  {
    /**
     * @return The kind of type argument
     */

    Kind kind();

    /**
     * The different kinds of type argument
     */

    enum Kind
    {
      /**
       * The type argument is a reference
       */

      TYPE_ARGUMENT_REFERENCE,

      /**
       * The type argument is a wildcard
       */

      TYPE_ARGUMENT_WILDCARD
    }
  }

  /**
   * A reference type argument.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeArgumentReferenceType extends CTypeArgumentType
  {
    /**
     * @return The type
     */

    @Value.Parameter
    CReferenceType reference();

    @Override
    default Kind kind()
    {
      return TYPE_ARGUMENT_REFERENCE;
    }

    @Override
    default String toJava()
    {
      return this.reference().toJava();
    }
  }

  /**
   * A wildcard type argument.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  public interface CTypeArgumentWildcardType extends CTypeArgumentType
  {
    /**
     * @return The wildcard
     */

    @Value.Parameter
    CWildcardType wildcard();

    @Override
    default Kind kind()
    {
      return TYPE_ARGUMENT_WILDCARD;
    }

    @Override
    default String toJava()
    {
      return this.wildcard().toJava();
    }
  }

  /**
   * A visitor that inspects superclass information. Currently unused (but
   * necessary to complete the {@link ClassGenericVisitor} implementation).
   */

  private static final class SuperclassVisitor extends UnreachableVisitor
  {
    private final Logger logger;

    SuperclassVisitor()
    {
      this.logger = LoggerFactory.getLogger(SuperclassVisitor.class);
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger.trace("visitClassType: {}", name);
    }

    @Override
    public void visitEnd()
    {
      this.logger.trace("visitEnd");
    }

    @Override
    protected Logger log()
    {
      return this.logger;
    }
  }

  private static final class ClassGenericVisitor extends UnreachableVisitor
  {
    private final Logger logger;
    private final CClassRegistryType registry;
    private final CTypeParameter.Builder parameter_builder;
    private boolean parameter_builder_clean;
    private int bound_visitors;
    private List<CTypeParameter> completed;
    private String current_name;
    private ClassBoundVisitor bound_visitor;

    @Override
    public SignatureVisitor visitSuperclass()
    {
      this.logger.trace("visitSuperclass");

      this.completeInProgressTypeParameter();
      return new SuperclassVisitor();
    }

    private ClassGenericVisitor(
      final CClassRegistryType in_registry)
    {
      this.registry = NullCheck.notNull(in_registry, "Registry");
      this.logger = LoggerFactory.getLogger(ClassGenericVisitor.class);
      this.completed = List.empty();
      this.parameter_builder = CTypeParameter.builder();
      this.parameter_builder_clean = true;
      this.bound_visitors = 0;
    }

    @Override
    protected Logger log()
    {
      return this.logger;
    }

    @Override
    public void visitFormalTypeParameter(
      final String name)
    {
      this.logger.trace("visitFormalTypeParameter: {}", name);

      this.completeInProgressTypeParameter();
      this.parameter_builder.setBound(Optional.empty());
      this.parameter_builder.setName(name);
      this.parameter_builder_clean = false;
      this.current_name = name;
    }

    private void completeInProgressTypeParameter()
    {
      this.logger.trace("completeInProgressTypeParameter");

      if (!this.parameter_builder_clean) {
        if (this.bound_visitor.class_main.isPresent()) {
          Invariants.checkInvariant(
            !this.bound_visitor.variable.isPresent(),
            "Bound must either be a class or a type variable");

          final CTypeBoundClass b = CTypeBoundClass.of(
            this.bound_visitor.class_main.get(),
            this.bound_visitor.class_intersections);
          this.parameter_builder.setBound(b);
        } else {
          Invariants.checkInvariant(
            this.bound_visitor.variable.isPresent(),
            "Bound must either be a class or a type variable");

          final CTypeBoundVariable b =
            CTypeBoundVariable.of(this.bound_visitor.variable.get());
          this.parameter_builder.setBound(b);
        }

        final CTypeParameter p = this.parameter_builder.build();
        this.logger.trace("completeInProgressTypeParameter: completed {}", p);
        this.completed = this.completed.append(p);
      }

      this.bound_visitor =
        new ClassBoundVisitor(this.registry, this.bound_visitors);
      ++this.bound_visitors;
    }

    @Override
    public SignatureVisitor visitClassBound()
    {
      this.logger.trace("visitClassBound");
      return this.bound_visitor;
    }

    @Override
    public SignatureVisitor visitInterfaceBound()
    {
      this.logger.trace("visitInterfaceBound");
      return this.bound_visitor;
    }

    public List<CTypeParameter> completed()
    {
      return this.completed;
    }
  }

  /**
   * A visitor that constructs a single type argument.
   */

  private static final class TypeArgumentVisitor extends UnreachableVisitor
  {
    private final Logger logger;
    private final char wildcard;
    private final Consumer<CTypeArgumentType> consumer;
    private final CGenericClass.Builder class_builder;
    private final CClassRegistryType registry;

    TypeArgumentVisitor(
      final CClassRegistryType in_registry,
      final char in_wildcard,
      final Consumer<CTypeArgumentType> in_consumer)
    {
      this.registry = NullCheck.notNull(in_registry, "Registry");
      this.wildcard = in_wildcard;
      this.consumer = NullCheck.notNull(in_consumer, "Consumer");
      this.class_builder = CGenericClass.builder();
      this.logger = LoggerFactory.getLogger(TypeArgumentVisitor.class);
      this.logger.trace("created ({})", Character.valueOf(this.wildcard));
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger.trace("visitClassType: {}", name);
      final CClass c = lookupClass(this.registry, name);
      this.class_builder.setName(c.name());
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger.trace("visitTypeVariable: {}", name);
      this.consumer.accept(CTypeArgumentReference.of(
        CReferenceVariable.of(CTypeVariable.of(name))));
    }

    @Override
    public void visitEnd()
    {
      this.logger.trace("visitEnd");

      final CGenericClass clazz = this.class_builder.build();
      switch (this.wildcard) {
        case SignatureVisitor.INSTANCEOF: {
          this.consumer.accept(CTypeArgumentReference.of(
            CReferenceClass.of(clazz)));
          return;
        }

        case SignatureVisitor.EXTENDS: {
          this.consumer.accept(CTypeArgumentWildcard.of(
            CWildcardExtends.of(CReferenceClass.of(clazz))));
          return;
        }

        case SignatureVisitor.SUPER: {
          this.consumer.accept(CTypeArgumentWildcard.of(
            CWildcardSuper.of(CReferenceClass.of(clazz))));
          return;
        }

        default: {
          this.logger.error(
            "unrecognized wildcard type: {}",
            Character.valueOf(this.wildcard));
          throw new UnimplementedCodeException();
        }
      }
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char in_wildcard)
    {
      this.logger.trace("visitTypeArgument: {}",
                        Character.valueOf(in_wildcard));
      return new TypeArgumentVisitor(
        this.registry, in_wildcard, this.class_builder::addArguments);
    }

    @Override
    public void visitTypeArgument()
    {
      this.logger.trace("visitTypeArgument");

      /*
       * An unbounded wildcard implicitly has java.lang.Object as its bound.
       */

      try {
        this.logger.trace("visitTypeArgument");
        this.class_builder.addArguments(
          CTypeArgumentWildcard.of(
            CWildcardExtends.of(
              CReferenceClass.of(
                CGenericClass.of(
                  this.registry.javaLangObject().name(), List.empty())))));
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    protected Logger log()
    {
      return this.logger;
    }
  }

  private static final class ClassBoundVisitor extends UnreachableVisitor
  {
    private final Logger logger;
    private final CClassRegistryType registry;
    private final CGenericClass.Builder class_builder;
    private List<CGenericClass> class_intersections;
    private CClassName class_name_last;
    private Optional<CGenericClass> class_main;
    private Optional<CTypeVariable> variable;

    ClassBoundVisitor(
      final CClassRegistryType in_registry,
      final int id)
    {
      this.registry =
        NullCheck.notNull(in_registry, "Registry");
      this.logger =
        LoggerFactory.getLogger(loggerName(id));

      this.class_main = Optional.empty();
      this.class_intersections = List.empty();
      this.class_builder = CGenericClass.builder();
      this.variable = Optional.empty();
      this.logger.trace("created");
    }

    private static String loggerName(
      final int id)
    {
      return ClassBoundVisitor.class + "[" + id + "]";
    }

    @Override
    public void visitEnd()
    {
      this.logger.trace("visitEnd");

      final CGenericClass clazz = this.class_builder.build();
      if (this.class_main.isPresent()) {
        this.logger.trace("visitEnd: saved intersection {}", clazz);
        this.class_intersections = this.class_intersections.append(clazz);
      } else {
        this.logger.trace("visitEnd: saved main class {}", clazz);
        this.class_main = Optional.of(clazz);
      }
    }

    @Override
    public void visitTypeArgument()
    {
      /*
       * An unbounded wildcard implicitly has java.lang.Object as its bound.
       */

      try {
        this.logger.trace("visitTypeArgument");
        this.class_builder.addArguments(
          CTypeArgumentWildcard.of(
            CWildcardExtends.of(
              CReferenceClass.of(
                CGenericClass.of(
                  this.registry.javaLangObject().name(), List.empty())))));
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard)
    {
      this.logger.trace("visitTypeArgument: {}", Character.valueOf(wildcard));
      return new TypeArgumentVisitor(
        this.registry, wildcard, this.class_builder::addArguments);
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger.trace("visitClassType: {}", name);

      final CClass c = lookupClass(this.registry, name);
      this.class_name_last = c.name();
      this.class_builder.setName(this.class_name_last);
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger.trace("visitTypeVariable: {}", name);

      Preconditions.checkPrecondition(
        !this.class_main.isPresent(),
        "Type variables cannot be members of intersections");

      this.variable = Optional.of(CTypeVariable.of(name));
    }

    @Override
    protected Logger log()
    {
      return this.logger;
    }
  }

  private static abstract class UnreachableVisitor extends SignatureVisitor
  {
    UnreachableVisitor()
    {
      super(Opcodes.ASM6);
    }

    protected abstract Logger log();

    @Override
    public void visitFormalTypeParameter(
      final String name)
    {
      this.log().trace("visitFormalTypeParameter: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitClassBound()
    {
      this.log().trace("visitClassBound");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitInterfaceBound()
    {
      this.log().trace("visitInterfaceBound");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitSuperclass()
    {
      this.log().trace("visitSuperclass");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitInterface()
    {
      this.log().trace("visitInterface");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitParameterType()
    {
      this.log().trace("visitParameterType");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitReturnType()
    {
      this.log().trace("visitReturnType");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitExceptionType()
    {
      this.log().trace("visitExceptionType");
      throw new UnreachableCodeException();
    }

    @Override
    public void visitBaseType(
      final char descriptor)
    {
      this.log().trace("visitBaseType: {}", Character.valueOf(descriptor));
      throw new UnreachableCodeException();
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.log().trace("visitTypeVariable: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.log().trace("visitArrayType");
      throw new UnreachableCodeException();
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.log().trace("visitClassType: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public void visitInnerClassType(
      final String name)
    {
      this.log().trace("visitInnerClassType: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public void visitTypeArgument()
    {
      this.log().trace("visitTypeArgument");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard)
    {
      this.log().trace("visitTypeArgument: {}", Character.valueOf(wildcard));
      throw new UnreachableCodeException();
    }

    @Override
    public void visitEnd()
    {
      this.log().trace("visitEnd");
      throw new UnreachableCodeException();
    }
  }
}
