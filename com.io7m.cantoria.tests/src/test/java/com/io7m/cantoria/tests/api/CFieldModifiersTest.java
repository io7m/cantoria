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

import com.io7m.cantoria.api.CFieldModifiers;
import com.io7m.cantoria.api.CModifier;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.FieldNode;

import static com.io7m.cantoria.api.CAccessibility.PACKAGE_PRIVATE;
import static com.io7m.cantoria.api.CAccessibility.PRIVATE;
import static com.io7m.cantoria.api.CAccessibility.PROTECTED;
import static com.io7m.cantoria.api.CAccessibility.PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ASM6;

public final class CFieldModifiersTest
{
  @Test
  public void testModifiers()
  {
    final FieldNode node =
      new FieldNode(ASM6, "x", "int", null, null);

    node.access = ACC_PUBLIC;
    Assertions.assertTrue(CFieldModifiers.fieldIsPublic(node));
    Assertions.assertEquals(
      PUBLIC, CFieldModifiers.fieldAccessibility(node));

    node.access = ACC_PRIVATE;
    Assertions.assertTrue(CFieldModifiers.fieldIsPrivate(node));
    Assertions.assertEquals(
      PRIVATE, CFieldModifiers.fieldAccessibility(node));

    node.access = ACC_PROTECTED;
    Assertions.assertTrue(CFieldModifiers.fieldIsProtected(node));
    Assertions.assertEquals(
      PROTECTED, CFieldModifiers.fieldAccessibility(node));

    node.access = 0;
    Assertions.assertTrue(CFieldModifiers.fieldIsPackagePrivate(node));
    Assertions.assertEquals(
      PACKAGE_PRIVATE, CFieldModifiers.fieldAccessibility(node));

    node.access = ACC_STATIC;
    Assertions.assertTrue(CFieldModifiers.fieldIsStatic(node));
    node.access = ACC_FINAL;
    Assertions.assertTrue(CFieldModifiers.fieldIsFinal(node));

    node.access = 0b11111111_11111111_11111111_11111111;
    final Set<CModifier> mods = CFieldModifiers.fieldModifiers(node);
    Assertions.assertEquals(
      HashSet.of(
        CModifier.FINAL,
        CModifier.STATIC,
        CModifier.TRANSIENT,
        CModifier.VOLATILE), mods);
  }
}
