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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.List;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Functions to parse generic type signatures.
 */

public final class CGenericsParsing
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CGenericsParsing.class);

  private CGenericsParsing()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Parse a class signature.
   *
   * See JVMS 9 §4.7.9.1
   *
   * @param signature The class signature
   *
   * @return A parsed class signature
   */

  public static CGClassSignature parseClassSignature(
    final String signature)
  {
    Objects.requireNonNull(signature, "Signature");

    LOG.trace("parseClassTypeSignature: {}", signature);

    final SignatureReader reader = new SignatureReader(signature);
    final ClassSignatureVisitor visitor = new ClassSignatureVisitor(0);
    reader.accept(visitor);
    return CGClassSignature.builder()
      .setParameters(visitor.parameters)
      .setSuperclass(visitor.superclass)
      .setInterfaces(visitor.interfaces)
      .build();
  }

  private static String loggerName(
    final Class<?> clazz,
    final int depth)
  {
    final StringBuilder sb = new StringBuilder(64);
    sb.append(clazz.getCanonicalName());
    sb.append("[");
    sb.append(depth);
    sb.append("]");
    return sb.toString();
  }

  private static CGenericsType.CGTypeArgumentType.Kind ofWildcard(
    final char wildcard_type)
  {
    switch (wildcard_type) {
      case SignatureVisitor.INSTANCEOF: {
        return CGenericsType.CGTypeArgumentType.Kind.EXACTLY;
      }
      case SignatureVisitor.SUPER: {
        return CGenericsType.CGTypeArgumentType.Kind.SUPER;
      }
      case SignatureVisitor.EXTENDS: {
        return CGenericsType.CGTypeArgumentType.Kind.EXTENDS;
      }
      default: {
        throw new IllegalArgumentException(
          "Unrecognized type argument kind: " + wildcard_type);
      }
    }
  }

  private static final class InterfaceVisitor
    extends BaseVisitor
  {
    private final Consumer<CGClassTypeSignature> on_completion;
    private List<CGenericsType.CGTypeArgumentType> type_arguments;
    private String class_name;

    InterfaceVisitor(
      final int in_depth,
      final Consumer<CGClassTypeSignature> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(
          InterfaceVisitor.class,
          in_depth)),
        in_depth);

      this.on_completion =
        Objects.requireNonNull(in_on_completion, "On completion");

      this.type_arguments = List.empty();
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");
      this.on_completion.accept(
        CGClassTypeSignature.builder()
          .setTypeName(this.class_name)
          .setTypeArguments(CGTypeArguments.of(this.type_arguments))
          .build());
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);
      this.class_name = CClassNames.toDottedName(name);
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard_type)
    {
      this.logger().trace(
        "visitTypeArgument: {}", Character.valueOf(wildcard_type));

      return new TypeArgumentVisitor(
        this.depth() + 1,
        ofWildcard(wildcard_type),
        type -> {
          this.logger().trace("visitTypeArgument: complete {}", type);
          this.type_arguments = this.type_arguments.append(type);
        });
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }
  }

  private static final class InterfaceBoundVisitor
    extends BaseVisitor
  {
    private final Consumer<CGenericsType.CGFieldTypeSignatureType> on_completion;
    private List<CGenericsType.CGTypeArgumentType> type_arguments;
    private String type_class;
    private String parameter_name;

    InterfaceBoundVisitor(
      final int in_depth,
      final Consumer<CGenericsType.CGFieldTypeSignatureType> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(
          InterfaceBoundVisitor.class,
          in_depth)),
        in_depth);

      this.type_arguments =
        List.empty();
      this.on_completion =
        Objects.requireNonNull(in_on_completion, "On completion");
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);
      this.type_class = CClassNames.toDottedName(name);
    }

    @Override
    public void visitTypeArgument()
    {
      this.logger().trace("visitTypeArgument: ?");
      this.type_arguments =
        this.type_arguments.append(CGTypeArgumentAny.builder().build());
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard_type)
    {
      this.logger().trace(
        "visitTypeArgument: {}", Character.valueOf(wildcard_type));

      return new TypeArgumentVisitor(
        this.depth() + 1,
        ofWildcard(wildcard_type),
        type -> {
          this.logger().trace("visitTypeArgument: complete {}", type);
          this.type_arguments = this.type_arguments.append(type);
        });
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");

      this.on_completion.accept(
        CGFieldTypeSignatureClass.of(
          CGClassTypeSignature.builder()
            .setTypeName(this.type_class)
            .setTypeArguments(CGTypeArguments.of(this.type_arguments))
            .build()));
    }
  }

  private static abstract class BaseVisitor extends SignatureVisitor
  {
    private final int depth;
    private final Logger logger;

    BaseVisitor(
      final Logger in_logger,
      final int in_depth)
    {
      super(Opcodes.ASM6);
      this.depth = in_depth;
      this.logger = Objects.requireNonNull(in_logger, "Logger");
      this.onStart();
    }

    protected final Logger logger()
    {
      return this.logger;
    }

    protected final int depth()
    {
      return this.depth;
    }

    protected abstract void onStart();

    @Override
    public void visitFormalTypeParameter(
      final String name)
    {
      this.logger().trace("visitFormalTypeParameter: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitClassBound()
    {
      this.logger().trace("visitClassBound");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitInterfaceBound()
    {
      this.logger().trace("visitInterfaceBound");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitSuperclass()
    {
      this.logger().trace("visitSuperclass");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitInterface()
    {
      this.logger().trace("visitInterface");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitParameterType()
    {
      this.logger().trace("visitParameterType");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitReturnType()
    {
      this.logger().trace("visitReturnType");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitExceptionType()
    {
      this.logger().trace("visitExceptionType");
      throw new UnreachableCodeException();
    }

    @Override
    public void visitBaseType(
      final char descriptor)
    {
      this.logger().trace("visitBaseType: {}", Character.valueOf(descriptor));
      throw new UnreachableCodeException();
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger().trace("visitTypeVariable: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger().trace("visitArrayType");
      throw new UnreachableCodeException();
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public void visitInnerClassType(
      final String name)
    {
      this.logger().trace("visitInnerClassType: {}", name);
      throw new UnreachableCodeException();
    }

    @Override
    public void visitTypeArgument()
    {
      this.logger().trace("visitTypeArgument: ?");
      throw new UnreachableCodeException();
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard)
    {
      this.logger().trace("visitTypeArgument: {}", Character.valueOf(wildcard));
      throw new UnreachableCodeException();
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");
      throw new UnreachableCodeException();
    }
  }

  private static final class ArrayTypeVisitor extends BaseVisitor
  {
    private final CGFieldTypeSignatureArray.Builder array_builder;
    private final Consumer<CGFieldTypeSignatureArray> on_completion;
    private int dimensions = 1;
    private CGenericsType.CGTypeSignatureType type;
    private String type_class;
    private List<CGenericsType.CGTypeArgumentType> type_arguments;

    ArrayTypeVisitor(
      final int in_depth,
      final Consumer<CGFieldTypeSignatureArray> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(ArrayTypeVisitor.class, in_depth)),
        in_depth);

      this.array_builder = CGFieldTypeSignatureArray.builder();
      this.on_completion = Objects.requireNonNull(in_on_completion, "Consumer");
      this.type_arguments = List.empty();
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");

      this.type =
        CGTypeSignatureField.of(CGFieldTypeSignatureClass.of(
          CGClassTypeSignature.builder()
            .setTypeName(this.type_class)
            .setTypeArguments(CGTypeArguments.of(this.type_arguments))
            .build()));

      this.onComplete();
    }

    private void onComplete()
    {
      this.array_builder.setDimensions(this.dimensions);
      this.array_builder.setType(this.type);
      final CGFieldTypeSignatureArray at = this.array_builder.build();
      this.logger().trace("onComplete: {}", at);
      this.on_completion.accept(at);
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger().trace("visitArrayType");
      ++this.dimensions;
      return this;
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);
      this.type_class = CClassNames.toDottedName(name);
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard)
    {
      this.logger().trace("visitTypeArgument: {}", Character.valueOf(wildcard));
      final TypeArgumentVisitor visitor =
        new TypeArgumentVisitor(
          this.depth() + 1,
          ofWildcard(wildcard),
          argument_type -> {
            this.logger().trace(
              "visitTypeArgument: complete {}",
              argument_type);
            this.type_arguments = this.type_arguments.append(argument_type);
          });
      return visitor;
    }

    @Override
    public void visitBaseType(
      final char descriptor)
    {
      this.logger().trace("visitBaseType: {}", Character.valueOf(descriptor));
      this.type = CGTypeSignaturePrimitive.of(
        CGenericsType.Primitive.ofDescriptor(descriptor));
      this.onComplete();
    }
  }

  private static final class ClassBoundVisitor
    extends BaseVisitor
  {
    private final Consumer<CGenericsType.CGFieldTypeSignatureType> on_completion;
    private List<CGenericsType.CGTypeArgumentType> type_arguments_inner;
    private List<CGenericsType.CGTypeArgumentType> type_arguments;
    private String type_class;
    private String type_class_inner;
    private List<CGClassTypeSignature> type_inners;
    private boolean collecting_inners;

    ClassBoundVisitor(
      final int in_depth,
      final Consumer<CGenericsType.CGFieldTypeSignatureType> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(ClassBoundVisitor.class, in_depth)),
        in_depth);

      this.type_arguments = List.empty();
      this.type_arguments_inner = List.empty();
      this.type_inners = List.empty();
      this.collecting_inners = false;

      this.on_completion =
        Objects.requireNonNull(in_on_completion, "On completion");
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard_type)
    {
      this.logger().trace(
        "visitTypeArgument: {}", Character.valueOf(wildcard_type));

      return new TypeArgumentVisitor(
        this.depth() + 1,
        ofWildcard(wildcard_type),
        type -> {
          this.logger().trace("visitTypeArgument: complete {}", type);

          if (this.collecting_inners) {
            this.type_arguments_inner = this.type_arguments_inner.append(type);
          } else {
            this.type_arguments = this.type_arguments.append(type);
          }
        });
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");

      this.completeInnerTypeIfNecessary();
      this.on_completion.accept(
        CGFieldTypeSignatureClass.of(
          CGClassTypeSignature.builder()
            .setTypeName(this.type_class)
            .setTypeArguments(CGTypeArguments.of(this.type_arguments))
            .setInnerTypes(this.type_inners)
            .build()));
    }

    @Override
    public void visitInnerClassType(
      final String name)
    {
      this.logger().trace("visitInnerClassType: {}", name);

      this.completeInnerTypeIfNecessary();
      this.collecting_inners = true;
      this.type_class_inner = CClassNames.toDottedName(name);
      this.type_arguments_inner = List.empty();
    }

    private void completeInnerTypeIfNecessary()
    {
      if (this.type_class_inner != null) {
        this.type_inners =
          this.type_inners.append(
            CGClassTypeSignature.of(
              this.type_class_inner,
              CGTypeArguments.of(this.type_arguments_inner),
              List.empty()));
      }
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);

      if (!this.collecting_inners) {
        this.type_class = CClassNames.toDottedName(name);
      } else {
        this.type_class_inner = CClassNames.toDottedName(name);
      }
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger().trace("visitTypeVariable: {}", name);

      Preconditions.checkPrecondition(
        !this.collecting_inners,
        "Inner classes cannot be type variables");

      this.on_completion.accept(
        CGFieldTypeSignatureVariable.of(CGTypeVariable.of(name)));
    }
  }

  private static final class ClassSignatureVisitor extends BaseVisitor
  {
    private List<CGenericsType.CGFieldTypeSignatureType> bounds;
    private String parameter_name;
    private List<CGTypeParameter> parameters;
    private CGClassSignature signature;
    private CGClassTypeSignature superclass;
    private List<CGClassTypeSignature> interfaces;

    ClassSignatureVisitor(
      final int in_depth)
    {
      super(
        LoggerFactory.getLogger(loggerName(
          ClassSignatureVisitor.class,
          in_depth)),
        in_depth);

      this.bounds = List.empty();
      this.parameters = List.empty();
      this.interfaces = List.empty();
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }

    @Override
    public void visitFormalTypeParameter(
      final String name)
    {
      this.logger().trace("visitFormalTypeParameter: {}", name);

      this.completeTypeParameterIfNecessary();
      this.parameter_name = name;
    }

    private void completeTypeParameterIfNecessary()
    {
      if (this.parameter_name != null) {
        final CGTypeParameter.Builder b = CGTypeParameter.builder();
        b.setName(this.parameter_name);
        b.setType(this.bounds.head());
        b.addAllIntersections(this.bounds.tail());
        this.parameters = this.parameters.append(b.build());
        this.parameter_name = null;
        this.bounds = List.empty();
      }
    }

    @Override
    public SignatureVisitor visitClassBound()
    {
      this.logger().trace("visitClassBound");
      return new ClassBoundVisitor(
        this.depth() + 1,
        type -> {
          this.logger().trace("visitClassBound: complete {}", type);
          this.bounds = this.bounds.append(type);
        });
    }

    @Override
    public SignatureVisitor visitInterfaceBound()
    {
      this.logger().trace("visitInterfaceBound");
      return new InterfaceBoundVisitor(
        this.depth() + 1,
        type -> {
          this.logger().trace("visitInterfaceBound: complete {}", type);
          this.bounds = this.bounds.append(type);
        });
    }

    @Override
    public SignatureVisitor visitSuperclass()
    {
      this.logger().trace("visitSuperclass");
      this.completeTypeParameterIfNecessary();
      return new SuperclassVisitor(
        this.depth() + 1,
        (parsed_superclass) -> {
          this.logger().trace(
            "visitSuperclass: complete {}",
            parsed_superclass);
          this.superclass = parsed_superclass;
        });
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger().trace("visitArrayType");

      return new ArrayTypeVisitor(this.depth() + 1, array -> {
        this.logger().trace("visitArrayType: complete {}", array);
        throw new UnimplementedCodeException();
      });
    }

    @Override
    public SignatureVisitor visitInterface()
    {
      this.logger().trace("visitInterface");
      return new InterfaceVisitor(this.depth() + 1, type -> {
        this.logger().trace("visitInterface: complete {}", type);
        this.interfaces = this.interfaces.append(type);
      });
    }
  }

  private static final class SuperclassVisitor extends BaseVisitor
  {
    private final Consumer<CGClassTypeSignature> on_completion;
    private boolean collecting_inners;
    private List<CGenericsType.CGTypeArgumentType> type_arguments_inner;
    private List<CGenericsType.CGTypeArgumentType> type_arguments;
    private String type_class;
    private String type_class_inner;
    private List<CGClassTypeSignature> type_inners;

    SuperclassVisitor(
      final int in_depth,
      final Consumer<CGClassTypeSignature> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(SuperclassVisitor.class, in_depth)),
        in_depth);

      this.on_completion =
        Objects.requireNonNull(in_on_completion, "On completion");

      this.type_arguments = List.empty();
      this.type_arguments_inner = List.empty();
      this.type_inners = List.empty();
      this.collecting_inners = false;
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");

      this.completeInnerTypeIfNecessary();
      this.on_completion.accept(
        CGClassTypeSignature.builder()
          .setTypeName(this.type_class)
          .setTypeArguments(CGTypeArguments.of(this.type_arguments))
          .setInnerTypes(this.type_inners)
          .build());
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);

      if (!this.collecting_inners) {
        this.type_class = CClassNames.toDottedName(name);
      } else {
        this.type_class_inner = CClassNames.toDottedName(name);
      }
    }

    private void completeInnerTypeIfNecessary()
    {
      if (this.type_class_inner != null) {
        this.type_inners =
          this.type_inners.append(
            CGClassTypeSignature.of(
              this.type_class_inner,
              CGTypeArguments.of(this.type_arguments_inner),
              List.empty()));
      }
    }

    @Override
    public void visitInnerClassType(
      final String name)
    {
      this.logger().trace("visitInnerClassType: {}", name);

      this.completeInnerTypeIfNecessary();
      this.collecting_inners = true;
      this.type_class_inner = CClassNames.toDottedName(name);
      this.type_arguments_inner = List.empty();
    }

    @Override
    public void visitTypeArgument()
    {
      this.logger().trace("visitTypeArgument: ?");

      this.type_arguments =
        this.type_arguments.append(CGTypeArgumentAny.builder().build());
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard_type)
    {
      this.logger().trace(
        "visitTypeArgument: {}", Character.valueOf(wildcard_type));

      return new TypeArgumentVisitor(
        this.depth() + 1,
        ofWildcard(wildcard_type),
        type -> {
          this.logger().trace("visitTypeArgument: complete {}", type);

          if (this.collecting_inners) {
            this.type_arguments_inner = this.type_arguments_inner.append(type);
          } else {
            this.type_arguments = this.type_arguments.append(type);
          }
        });
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }
  }

  private static final class TypeArgumentVisitor
    extends BaseVisitor
  {
    private final CGenericsType.CGTypeArgumentType.Kind argument_kind;
    private final Consumer<CGenericsType.CGTypeArgumentType> on_completion;
    private String class_name;
    private List<CGenericsType.CGTypeArgumentType> class_type_arguments;

    TypeArgumentVisitor(
      final int in_depth,
      final CGenericsType.CGTypeArgumentType.Kind in_wildcard_type,
      final Consumer<CGenericsType.CGTypeArgumentType> in_on_completion)
    {
      super(
        LoggerFactory.getLogger(loggerName(
          TypeArgumentVisitor.class,
          in_depth)),
        in_depth);

      this.argument_kind =
        Objects.requireNonNull(in_wildcard_type, "Kind");
      this.on_completion =
        Objects.requireNonNull(in_on_completion, "On completion");

      this.class_type_arguments = List.empty();
    }

    @Override
    protected void onStart()
    {
      this.logger().trace("onStart");
    }

    @Override
    public void visitTypeVariable(
      final String name)
    {
      this.logger().trace("visitTypeVariable: {}", name);
      this.onComplete(CGFieldTypeSignatureVariable.of(CGTypeVariable.of(name)));
    }

    @Override
    public SignatureVisitor visitArrayType()
    {
      this.logger().trace("visitArrayType");
      return new ArrayTypeVisitor(
        this.depth() + 1,
        this::onComplete);
    }

    private void onComplete(
      final CGenericsType.CGFieldTypeSignatureType type)
    {
      this.logger().trace("onComplete: {}", type);

      switch (this.argument_kind) {
        case ANY: {
          throw new UnreachableCodeException();
        }
        case EXTENDS: {
          this.on_completion.accept(CGTypeArgumentExtends.of(type));
          break;
        }
        case EXACTLY: {
          this.on_completion.accept(CGTypeArgumentExactly.of(type));
          break;
        }
        case SUPER: {
          this.on_completion.accept(CGTypeArgumentSuper.of(type));
          break;
        }
      }
    }

    @Override
    public void visitClassType(
      final String name)
    {
      this.logger().trace("visitClassType: {}", name);
      this.class_name = CClassNames.toDottedName(name);
    }

    @Override
    public SignatureVisitor visitTypeArgument(
      final char wildcard_type)
    {
      this.logger().trace(
        "visitTypeArgument: {}", Character.valueOf(wildcard_type));

      return new TypeArgumentVisitor(
        this.depth() + 1,
        ofWildcard(wildcard_type),
        argument_type -> {
          this.logger().trace("visitTypeArgument: add {}", argument_type);
          this.class_type_arguments =
            this.class_type_arguments.append(argument_type);
        });
    }

    @Override
    public void visitEnd()
    {
      this.logger().trace("visitEnd");
      this.logger().trace("class name: {}", this.class_name);
      this.logger().trace("class arguments: {}", this.class_type_arguments);

      final CGFieldTypeSignatureClass t =
        CGFieldTypeSignatureClass.builder()
          .setClassType(
            CGClassTypeSignature.builder()
              .setTypeName(this.class_name)
              .setTypeArguments(CGTypeArguments.of(this.class_type_arguments))
              .build())
          .build();

      this.onComplete(t);
    }
  }
}
