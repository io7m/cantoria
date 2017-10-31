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

import com.io7m.cantoria.api.CClassModifiers;
import com.io7m.cantoria.api.CModifier;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;

import static com.io7m.cantoria.api.CAccessibility.PACKAGE_PRIVATE;
import static com.io7m.cantoria.api.CAccessibility.PRIVATE;
import static com.io7m.cantoria.api.CAccessibility.PROTECTED;
import static com.io7m.cantoria.api.CAccessibility.PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM6;

public final class CClassModifiersTest
{
  @Test
  public void testModifiers()
  {
    final ClassNode node = new ClassNode(ASM6);

    node.access = ACC_PUBLIC;
    Assertions.assertTrue(CClassModifiers.classIsPublic(node));
    Assertions.assertEquals(
      PUBLIC, CClassModifiers.classAccessibility(node));

    node.access = ACC_PRIVATE;
    Assertions.assertTrue(CClassModifiers.classIsPrivate(node));
    Assertions.assertEquals(
      PRIVATE, CClassModifiers.classAccessibility(node));

    node.access = ACC_PROTECTED;
    Assertions.assertTrue(CClassModifiers.classIsProtected(node));
    Assertions.assertEquals(
      PROTECTED, CClassModifiers.classAccessibility(node));

    node.access = 0;
    Assertions.assertTrue(CClassModifiers.classIsPackagePrivate(node));
    Assertions.assertEquals(
      PACKAGE_PRIVATE, CClassModifiers.classAccessibility(node));

    node.access = ACC_STATIC;
    Assertions.assertTrue(CClassModifiers.classIsStatic(node));
    node.access = ACC_ABSTRACT;
    Assertions.assertTrue(CClassModifiers.classIsAbstract(node));
    node.access = ACC_ENUM;
    Assertions.assertTrue(CClassModifiers.classIsEnum(node));
    node.access = ACC_FINAL;
    Assertions.assertTrue(CClassModifiers.classIsFinal(node));

    node.access = 0b11111111_11111111_11111111_11111111;
    final Set<CModifier> mods = CClassModifiers.classModifiers(node);
    Assertions.assertEquals(
      HashSet.of(
        CModifier.FINAL,
        CModifier.STATIC,
        CModifier.ABSTRACT,
        CModifier.ENUM,
        CModifier.INTERFACE), mods);
  }
}
