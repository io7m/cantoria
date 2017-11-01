package com.io7m.cantoria.api;

import io.vavr.collection.List;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.io7m.cantoria.api.CGGenericsType.CGReferenceType.Kind.REFERENCE_ARRAY;
import static com.io7m.cantoria.api.CGGenericsType.CGReferenceType.Kind.REFERENCE_CLASS;
import static com.io7m.cantoria.api.CGGenericsType.CGReferenceType.Kind.REFERENCE_VARIABLE;
import static com.io7m.cantoria.api.CGGenericsType.CGTypeArgumentType.Kind.TYPE_ARGUMENT_REFERENCE;
import static com.io7m.cantoria.api.CGGenericsType.CGTypeArgumentType.Kind.TYPE_ARGUMENT_WILDCARD;
import static com.io7m.cantoria.api.CGGenericsType.CGTypeBoundType.Kind.BOUND_CLASS;
import static com.io7m.cantoria.api.CGGenericsType.CGTypeBoundType.Kind.BOUND_VARIABLE;
import static com.io7m.cantoria.api.CGGenericsType.CGWildcardType.Kind.WILDCARD_EXTENDS;
import static com.io7m.cantoria.api.CGGenericsType.CGWildcardType.Kind.WILDCARD_SUPER;


public interface CGGenericsType
{
  /**
   * An array of primitive-typed values.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGArrayOfPrimitiveType extends CGArrayOfType
  {
    /**
     * @return The name of the primitive type of each element of the array
     */

    @Value.Parameter
    String primitive();

    @Override
    @Value.Parameter
    int dimensions();

    @Override
    default Kind kind()
    {
      return Kind.ARRAY_OF_PRIMITIVE;
    }

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.primitive());
      for (int index = 0; index < this.dimensions(); ++index) {
        sb.append("[]");
      }
      return sb.toString();
    }
  }

  /**
   * An array of reference-typed values.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGArrayOfReferenceType extends CGArrayOfType
  {
    /**
     * @return The type of the array elements
     */

    @Value.Parameter
    CGTypeClass classType();

    @Override
    @Value.Parameter
    int dimensions();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.classType().toJava());
      for (int index = 0; index < this.dimensions(); ++index) {
        sb.append("[]");
      }
      return sb.toString();
    }

    @Override
    default Kind kind()
    {
      return Kind.ARRAY_OF_REFERENCE;
    }
  }

  /**
   * Array types.
   *
   * See JLS 9 §4.3
   */

  interface CGArrayOfType extends CShowJavaType
  {
    /**
     * The available kinds of arrays
     */

    enum Kind
    {

      /**
       * An array of reference-typed values.
       */

      ARRAY_OF_REFERENCE,

      /**
       * An array of variable-typed values.
       */

      ARRAY_OF_VARIABLE,

      /**
       * An array of primitive-typed values.
       */

      ARRAY_OF_PRIMITIVE
    }

    /**
     * @return The kind of array
     */

    Kind kind();

    /**
     * @return The number of array dimensions
     */

    int dimensions();
  }

  /**
   * An array of variable-typed values.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGArrayOfVariableType extends CGArrayOfType
  {
    /**
     * @return The variable denoting the array element type
     */

    @Value.Parameter
    CGTypeVariable variable();

    @Override
    @Value.Parameter
    int dimensions();

    @Override
    default String toJava()
    {
      final StringBuilder sb = new StringBuilder(32);
      sb.append(this.variable().toJava());
      for (int index = 0; index < this.dimensions(); ++index) {
        sb.append("[]");
      }
      return sb.toString();
    }

    @Override
    default Kind kind()
    {
      return Kind.ARRAY_OF_VARIABLE;
    }
  }

  /**
   * The type of types.
   *
   * See JLS 9 §4.3
   */

  interface CGTypeType extends CShowJavaType
  {

  }

  /**
   * An array type.
   *
   * See JLS 9 §4.3
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArrayType extends CGTypeType
  {
    /**
     * @return The array type
     */

    @Value.Parameter
    CGArrayOfType array();

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
  interface CGTypeClassType extends CGTypeType
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
    List<CGTypeArgumentType> arguments();

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
   * A reference to an array.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGReferenceArrayType extends CGReferenceType
  {
    /**
     * @return The referred array
     */

    @Value.Parameter
    CGTypeArrayType array();

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
   * A reference to a class.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGReferenceClassType extends CGReferenceType
  {
    /**
     * @return The referred class
     */

    @Value.Parameter
    CGTypeClassType genericClass();

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
   * A reference to a generic type.
   *
   * <blockquote>There are four kinds of reference types: class types (§8.1),
   * interface types (§9.1), type variables (§4.4), and array types
   * (§10.1).</blockquote>
   *
   * See JLS 9 §4.3
   */

  interface CGReferenceType extends CShowJavaType
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
   * A reference to a variable.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGReferenceVariableType extends CGReferenceType
  {
    /**
     * @return The referred variable
     */

    @Value.Parameter
    CGTypeVariable variable();

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
   * A reference type argument.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentReferenceType extends CGTypeArgumentType
  {
    /**
     * @return The type
     */

    @Value.Parameter
    CGReferenceType reference();

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
   * A type argument.
   *
   * <blockquote>Type arguments may be either reference types or wildcards.
   * Wildcards are useful in situations where only partial knowledge about the
   * type parameter is required.</blockquote>
   *
   * See JLS 9 §4.5.1
   */

  interface CGTypeArgumentType extends CShowJavaType
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
   * A wildcard type argument.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeArgumentWildcardType extends CGTypeArgumentType
  {
    /**
     * @return The wildcard
     */

    @Value.Parameter
    CGWildcardType wildcard();

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
   * A type bound based on a class (or interface).
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGTypeBoundClassType extends CGTypeBoundType
  {
    /**
     * @return The bound class
     */

    @Value.Parameter
    CGTypeClass classType();

    /**
     * @return The extra bound intersection types
     */

    @Value.Parameter
    List<CGTypeClass> intersections();

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
   * A type bound.
   *
   * <blockquote>Every type variable declared as a type parameter has a bound.
   * If no bound is declared for a type variable, Object is
   * assumed.</blockquote>
   *
   * See JLS 9 §4.4
   */

  interface CGTypeBoundType extends CShowJavaType
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
  interface CGTypeBoundVariableType extends CGTypeBoundType
  {
    /**
     * @return The bound type variable
     */

    @Value.Parameter
    CGTypeVariable variable();

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
  interface CGTypeParameterType extends CShowJavaType
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
    Optional<CGTypeBoundType> bound();

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
  interface CGTypeVariableType extends CShowJavaType
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
   * A wildcard representing an upper bound.
   */

  @CImmutableStyleType
  @VavrEncodingEnabled
  @Value.Immutable
  interface CGWildcardExtendsType extends CGWildcardType
  {
    /**
     * @return The upper bound
     */

    @Value.Parameter
    CGReferenceType reference();

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
  interface CGWildcardSuperType extends CGWildcardType
  {
    /**
     * @return The lower bound
     */

    @Value.Parameter
    CGReferenceType reference();

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
   * The type of wildcards.
   *
   * <blockquote> Wildcards may be given explicit bounds, just like regular type
   * variable declarations. </blockquote>
   *
   * See JLS 9 §4.5.1
   */

  interface CGWildcardType extends CShowJavaType
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
}
