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
import com.io7m.jnull.NullCheck;
import io.vavr.collection.List;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.stream.Collectors;

/**
 * Types related to Java generics.
 *
 * See JVMS 9 §4.7.9.1
 */

public interface CGenericsType
{
  /**
   * The type of primitives.
   */

  enum Primitive implements CShowJavaType
  {
    /**
     * {@code int}
     */

    INTEGER('I', "int"),

    /**
     * {@code void}
     */

    VOID('V', "void"),

    /**
     * {@code boolean}
     */

    BOOLEAN('Z', "boolean"),

    /**
     * {@code byte}
     */

    BYTE('B', "byte"),

    /**
     * {@code char}
     */

    CHARACTER('C', "char"),

    /**
     * {@code short}
     */

    SHORT('S', "short"),

    /**
     * {@code double}
     */

    DOUBLE('D', "double"),

    /**
     * {@code float}
     */

    FLOAT('F', "float"),

    /**
     * {@code long}
     */

    LONG('J', "long");

    private final char descriptor;
    private final String name;

    Primitive(
      final char in_descriptor,
      final String in_name)
    {
      this.descriptor = in_descriptor;
      this.name = NullCheck.notNull(in_name, "name");
    }

    /**
     * Map a descriptor to a primitive.
     *
     * @param descriptor The type descriptor
     *
     * @return The primitive, if any
     *
     * @throws IllegalArgumentException For unknown descriptors
     */

    public static Primitive ofDescriptor(
      final char descriptor)
      throws IllegalArgumentException
    {
      final Primitive[] values = Primitive.values();
      for (int index = 0; index < values.length; ++index) {
        final Primitive v = values[index];
        if ((int) v.descriptor() == (int) descriptor) {
          return v;
        }
      }

      throw new IllegalArgumentException(
        "Unrecognized primitive descriptor: " + descriptor);
    }

    @Override
    public String toJava()
    {
      return this.name;
    }

    /**
     * @return The short descriptor for the type
     */

    public char descriptor()
    {
      return this.descriptor;
    }
  }

  /**
   * A type variable
   *
   * See JVMS 9 §4.7.9.1 {@code TypeVariableSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeVariableType extends CShowJavaType
  {
    /**
     * @return The name of the variable
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
   * The type of type signatures.
   */

  interface CGTypeSignatureType extends CShowJavaType
  {
    /**
     * @return The precise kind of type signature
     */

    Kind kind();

    /**
     * The possible kinds of type signatures
     */

    enum Kind
    {
      /**
       * @see CGTypeSignatureFieldType
       */

      FIELD,

      /**
       * @see CGTypeSignaturePrimitiveType
       */

      PRIMITIVE
    }
  }

  /**
   * A primitive.
   *
   * See JVMS 9 §4.7.9.1 {@code BaseType}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeSignaturePrimitiveType extends CGTypeSignatureType
  {
    /**
     * @return The primitive type
     */

    @Value.Parameter
    Primitive primitive();

    @Override
    default Kind kind()
    {
      return Kind.PRIMITIVE;
    }

    @Override
    default String toJava()
    {
      return this.primitive().toJava();
    }
  }

  /**
   * A field signature.
   *
   * See JVMS 9 §4.7.9.1 {@code FieldSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeSignatureFieldType extends CGTypeSignatureType
  {
    /**
     * @return The field type signature
     */

    @Value.Parameter
    CGFieldTypeSignatureType field();

    @Override
    default Kind kind()
    {
      return Kind.FIELD;
    }

    @Override
    default String toJava()
    {
      return this.field().toJava();
    }
  }

  /**
   * A class type signature.
   *
   * See JVMS 9 §4.7.9.1 {@code ClassTypeSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGClassTypeSignatureType extends CShowJavaType
  {
    /**
     * @return The class type name
     */

    @Value.Parameter
    String typeName();

    /**
     * @return The class type arguments
     */

    @Value.Parameter
    CGTypeArguments typeArguments();

    /**
     * @return The inner types that follow the main class
     */

    @Value.Parameter
    List<CGClassTypeSignature> innerTypes();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append(this.typeName());
      sb.append(this.typeArguments().toJava());
      this.innerTypes().forEach(p -> {
        sb.append(".");
        sb.append(p.typeName());
        sb.append(p.typeArguments().toJava());
      });
      return sb.toString();
    }
  }

  /**
   * A list of type arguments.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArguments}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentsType extends CShowJavaType
  {
    /**
     * @return The type arguments
     */

    @Value.Parameter
    List<CGTypeArgumentType> arguments();

    @Override
    default String toJava()
    {
      if (this.arguments().isEmpty()) {
        return "";
      }

      final StringBuilder sb = new StringBuilder(64);
      sb.append("<");
      sb.append(this.arguments()
                  .map(CShowJavaType::toJava)
                  .collect(Collectors.joining(",")));
      sb.append(">");
      return sb.toString();
    }
  }

  /**
   * A field signature.
   *
   * See JVMS 9 §4.7.9.1 {@code ReferenceTypeSignature}
   */

  interface CGFieldTypeSignatureType extends CShowJavaType
  {
    /**
     * @return The exact kind of signature
     */

    Kind kind();

    /**
     * The kind of signatures.
     */

    enum Kind
    {
      /**
       * @see CGFieldTypeSignatureClassType
       */

      FIELD_TYPE_SIGNATURE_CLASS,

      /**
       * @see CGFieldTypeSignatureArrayType
       */

      FIELD_TYPE_SIGNATURE_ARRAY,

      /**
       * @see CGFieldTypeSignatureVariableType
       */

      FIELD_TYPE_SIGNATURE_VARIABLE
    }
  }

  /**
   * A field signature referring to a class.
   *
   * See JVMS 9 §4.7.9.1 {@code ReferenceTypeSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGFieldTypeSignatureClassType extends CGFieldTypeSignatureType
  {
    /**
     * @return The class type signature
     */

    @Value.Parameter
    CGClassTypeSignature classType();

    @Override
    default Kind kind()
    {
      return CGFieldTypeSignatureType.Kind.FIELD_TYPE_SIGNATURE_CLASS;
    }

    @Override
    default String toJava()
    {
      return this.classType().toJava();
    }
  }

  /**
   * A field signature referring to an array.
   *
   * See JVMS 9 §4.7.9.1 {@code ArrayTypeSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGFieldTypeSignatureArrayType extends CGFieldTypeSignatureType
  {
    /**
     * @return The base array type
     */

    @Value.Parameter
    CGTypeSignatureType type();

    /**
     * @return The number of array dimensions
     */

    @Value.Parameter
    int dimensions();

    /**
     * Check preconditions for the type.
     */

    @Value.Check
    default void checkPreconditions()
    {
      Preconditions.checkPreconditionI(
        this.dimensions(),
        this.dimensions() >= 1,
        x -> "Dimensions must be positive");
    }

    @Override
    default Kind kind()
    {
      return Kind.FIELD_TYPE_SIGNATURE_ARRAY;
    }

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append(this.type().toJava());
      for (int index = 0; index < this.dimensions(); ++index) {
        sb.append("[]");
      }
      return sb.toString();
    }
  }

  /**
   * A field signature referring to a type variable.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeVariableSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGFieldTypeSignatureVariableType extends CGFieldTypeSignatureType
  {
    /**
     * @return The type variable
     */

    @Value.Parameter
    CGTypeVariable variable();

    @Override
    default Kind kind()
    {
      return Kind.FIELD_TYPE_SIGNATURE_VARIABLE;
    }

    @Override
    default String toJava()
    {
      return this.variable().toJava();
    }
  }

  /**
   * A type argument.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArgument}
   */

  interface CGTypeArgumentType extends CShowJavaType
  {
    /**
     * @return The kind of type argument
     */

    Kind kind();

    /**
     * The kind of type arguments
     */

    enum Kind
    {
      /**
       * @see CGTypeArgumentAny
       */

      ANY,

      /**
       * @see CGTypeArgumentExtends
       */

      EXTENDS,

      /**
       * @see CGTypeArgumentExactly
       */

      EXACTLY,

      /**
       * @see CGTypeArgumentSuper
       */

      SUPER
    }
  }

  /**
   * A type argument referring to an unbounded wildcard.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArgument}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentAnyType extends CGTypeArgumentType
  {
    @Override
    default CGTypeArgumentType.Kind kind()
    {
      return CGTypeArgumentType.Kind.ANY;
    }

    @Override
    default String toJava()
    {
      return "?";
    }
  }

  /**
   * A type argument referring to a lower-bounded wildcard.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArgument}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentExtendsType extends CGTypeArgumentType
  {
    /**
     * @return The field signature of the bound
     */

    @Value.Parameter
    CGFieldTypeSignatureType fieldSignature();

    @Override
    default CGTypeArgumentType.Kind kind()
    {
      return CGTypeArgumentType.Kind.EXTENDS;
    }

    @Override
    default String toJava()
    {
      return "? extends " + this.fieldSignature().toJava();
    }
  }

  /**
   * A type argument referring to an exact bound.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArgument}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentExactlyType extends CGTypeArgumentType
  {
    /**
     * @return The field signature of the bound
     */

    @Value.Parameter
    CGFieldTypeSignatureType fieldSignature();

    @Override
    default CGTypeArgumentType.Kind kind()
    {
      return CGTypeArgumentType.Kind.EXACTLY;
    }

    @Override
    default String toJava()
    {
      return this.fieldSignature().toJava();
    }
  }

  /**
   * A type argument referring to an upper-bounded wildcard.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeArgument}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentSuperType extends CGTypeArgumentType
  {
    /**
     * @return The field signature of the bound
     */

    @Value.Parameter
    CGFieldTypeSignatureType fieldSignature();

    @Override
    default CGTypeArgumentType.Kind kind()
    {
      return CGTypeArgumentType.Kind.SUPER;
    }

    @Override
    default String toJava()
    {
      return "? super " + this.fieldSignature().toJava();
    }
  }

  /**
   * A formal type parameter.
   *
   * See JVMS 9 §4.7.9.1 {@code TypeParameter}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeParameterType extends CShowJavaType
  {
    /**
     * @return The name of the formal type parameter
     */

    @Value.Parameter
    String name();

    /**
     * @return The signature of the parameter
     */

    @Value.Parameter
    CGFieldTypeSignatureType type();

    /**
     * @return The interface intersections of the parameter, if any
     */

    @Value.Parameter
    List<CGFieldTypeSignatureType> intersections();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);
      sb.append(this.name());
      sb.append(" extends ");
      sb.append(this.type().toJava());
      this.intersections().forEach(t -> {
        sb.append(" & ");
        sb.append(t.toJava());
      });
      return sb.toString();
    }
  }

  /**
   * A class signature.
   *
   * See JVMS 9 §4.7.9.1 {@code ClassSignature}
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGClassSignatureType extends CShowJavaType
  {
    /**
     * @return The formal type parameters
     */

    @Value.Parameter
    List<CGTypeParameter> parameters();

    /**
     * @return The superclass
     */

    @Value.Parameter
    CGClassTypeSignature superclass();

    /**
     * @return The implemented interfaces, if any
     */

    @Value.Parameter
    List<CGClassTypeSignature> interfaces();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(64);

      if (!this.parameters().isEmpty()) {
        sb.append("<");
        sb.append(this.parameters()
                    .map(CGTypeParameterType::toJava)
                    .collect(Collectors.joining(",")));
        sb.append("> ");
      }

      sb.append("extends ");
      sb.append(this.superclass().toJava());

      if (!this.interfaces().isEmpty()) {
        sb.append(" implements ");
        sb.append(this.interfaces()
                    .map(CGClassTypeSignatureType::toJava)
                    .collect(Collectors.joining(",")));
      }
      return sb.toString();
    }
  }
}
