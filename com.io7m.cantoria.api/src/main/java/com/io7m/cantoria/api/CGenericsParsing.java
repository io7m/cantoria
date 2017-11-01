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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jnull.NullCheck;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Functions for processing generic type parameters.
 */

public final class CGenericsParsing
{
  private static final Logger LOG = LoggerFactory.getLogger(CGenericsParsing.class);

  private CGenericsParsing()
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

  public static List<CGTypeParameter> parseClassGenericParameters(
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
    private final CGTypeParameter.Builder parameter_builder;
    private boolean parameter_builder_clean;
    private int bound_visitors;
    private List<CGTypeParameter> completed;
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
      this.parameter_builder = CGTypeParameter.builder();
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

          final CGTypeBoundClass b = CGTypeBoundClass.of(
            this.bound_visitor.class_main.get(),
            this.bound_visitor.class_intersections);
          this.parameter_builder.setBound(b);
        } else {
          Invariants.checkInvariant(
            this.bound_visitor.variable.isPresent(),
            "Bound must either be a class or a type variable");

          final CGTypeBoundVariable b =
            CGTypeBoundVariable.of(this.bound_visitor.variable.get());
          this.parameter_builder.setBound(b);
        }

        final CGTypeParameter p = this.parameter_builder.build();
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

    public List<CGTypeParameter> completed()
    {
      return this.completed;
    }
  }

  private static final class ArrayVisitor extends UnreachableVisitor
  {
    private final Logger logger;
    private final CClassRegistryType registry;
    private final CGTypeArray.Builder array_builder;
    private final CGArrayOfReference.Builder array_ref_builder;
    private int dimensions;
    private CClass array_class;
    private CGGenericsType.CGArrayOfType.Kind array_kind;
    private CGTypeVariable array_variable;
    private CGGenericsType.CGArrayOfType array;

    ArrayVisitor(
      final CClassRegistryType in_registry)
    {
      this.registry = NullCheck.notNull(in_registry, "Registry");

      this.array_builder = CGTypeArray.builder();
      this.array_ref_builder = CGArrayOfReference.builder();
      this.dimensions = 1;

      this.logger = LoggerFactory.getLogger(ArrayVisitor.class);
      this.logger.trace("created");
    }

    private static String baseTypeToName(
      final char c)
    {
      switch (c) {
        case 'B':
          return "byte";
        case 'C':
          return "char";
        case 'D':
          return "double";
        case 'F':
          return "float";
        case 'I':
          return "integer";
        case 'J':
          return "long";
        case 'S':
          return "short";
        case 'V':
          return "void";
        case 'Z':
          return "boolean";
        default:
          throw new UnreachableCodeException();
      }
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger.trace("visitArrayType: {}", Integer.valueOf(this.dimensions));
      ++this.dimensions;
      return this;
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger.trace("visitTypeVariable: {}", name);
      this.array_kind =
        CGGenericsType.CGArrayOfType.Kind.ARRAY_OF_VARIABLE;
      this.array_variable =
        CGTypeVariable.of(name);
      this.array =
        CGArrayOfVariable.of(this.array_variable, this.dimensions);
    }

    @Override
    public void visitBaseType(
      final char descriptor)
    {
      this.logger.trace("visitBaseType: {}", Character.valueOf(descriptor));

      this.array_kind =
        CGGenericsType.CGArrayOfType.Kind.ARRAY_OF_PRIMITIVE;
      this.array =
        CGArrayOfPrimitive.of(baseTypeToName(descriptor), this.dimensions);
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger.trace("visitClassType: {}", name);
      final CClass c = lookupClass(this.registry, name);
      this.array_kind = CGGenericsType.CGArrayOfType.Kind.ARRAY_OF_REFERENCE;
      this.array_class = c;
    }

    @Override
    public void visitEnd()
    {
      this.logger.trace("visitEnd");
      this.logger.trace(
        "array dimensions: {}",
        Integer.valueOf(this.dimensions));
    }

    @Override
    protected Logger log()
    {
      return this.logger;
    }
  }

  /**
   * A visitor that constructs a single type argument.
   */

  private static final class TypeArgumentVisitor extends UnreachableVisitor
  {
    private final Logger logger;
    private final char wildcard;
    private final Consumer<CGGenericsType.CGTypeArgumentType> consumer;
    private final CGTypeClass.Builder class_builder;
    private final CClassRegistryType registry;
    private final ArrayVisitor array_visitor;

    TypeArgumentVisitor(
      final CClassRegistryType in_registry,
      final char in_wildcard,
      final Consumer<CGGenericsType.CGTypeArgumentType> in_consumer)
    {
      this.registry = NullCheck.notNull(in_registry, "Registry");
      this.wildcard = in_wildcard;
      this.consumer = NullCheck.notNull(in_consumer, "Consumer");
      this.class_builder = CGTypeClass.builder();
      this.logger = LoggerFactory.getLogger(TypeArgumentVisitor.class);
      this.logger.trace("created ({})", Character.valueOf(this.wildcard));
      this.array_visitor = new ArrayVisitor(this.registry);
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger.trace("visitArrayType");
      return this.array_visitor;
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
      this.consumer.accept(CGTypeArgumentReference.of(
        CGReferenceVariable.of(CGTypeVariable.of(name))));
    }

    @Override
    public void visitEnd()
    {
      this.logger.trace("visitEnd");

      final CGTypeClass clazz = this.class_builder.build();
      switch (this.wildcard) {
        case SignatureVisitor.INSTANCEOF: {
          this.consumer.accept(CGTypeArgumentReference.of(
            CGReferenceClass.of(clazz)));
          return;
        }

        case SignatureVisitor.EXTENDS: {
          this.consumer.accept(CGTypeArgumentWildcard.of(
            CGWildcardExtends.of(CGReferenceClass.of(clazz))));
          return;
        }

        case SignatureVisitor.SUPER: {
          this.consumer.accept(CGTypeArgumentWildcard.of(
            CGWildcardSuper.of(CGReferenceClass.of(clazz))));
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
      this.logger.trace(
        "visitTypeArgument: {}",
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
          CGTypeArgumentWildcard.of(
            CGWildcardExtends.of(
              CGReferenceClass.of(
                CGTypeClass.of(
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
    private final CGTypeClass.Builder class_builder;
    private List<CGTypeClass> class_intersections;
    private CClassName class_name_last;
    private Optional<CGTypeClass> class_main;
    private Optional<CGTypeVariable> variable;

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
      this.class_builder = CGTypeClass.builder();
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

      final CGTypeClass clazz = this.class_builder.build();
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
          CGTypeArgumentWildcard.of(
            CGWildcardExtends.of(
              CGReferenceClass.of(
                CGTypeClass.of(
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

      this.variable = Optional.of(CGTypeVariable.of(name));
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
