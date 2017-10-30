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

import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.SortedSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ModuleNode;
import org.objectweb.asm.tree.ModuleRequireNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Functions over module descriptors.
 */

public final class CModuleDescriptors
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CModuleDescriptors.class);

  private CModuleDescriptors()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Load a module node from the given stream.
   *
   * @param stream The input stream
   *
   * @return A loaded module node
   *
   * @throws IOException On I/O errors
   */

  public static ModuleNode loadModuleNode(
    final InputStream stream)
    throws IOException
  {
    final ClassReader reader = new ClassReader(stream);
    final ModuleReader visitor = new ModuleReader();
    reader.accept(visitor, 0);
    LOG.trace("module {}", visitor.module.name);
    return visitor.module;
  }

  /**
   * Load a module descriptor.
   *
   * @param module The module node
   *
   * @return A module descriptor
   *
   * @throws IOException On I/O errors
   */

  public static CModuleDescriptor loadModuleDescriptor(
    final ModuleNode module)
    throws IOException
  {
    final CModuleDescriptor.Builder b = CModuleDescriptor.builder();
    b.setName(module.name);

    if (module.provides != null) {
      final java.util.TreeMap<String, SortedSet<String>> map =
        new java.util.TreeMap<>();

      module.provides.forEach(
        providing -> {
          LOG.trace("providing: {}", providing.service);
          providing.providers.forEach(provider -> {
            LOG.trace("providing: {} -> {}", providing.service, provider);
            b.addProvides(CModuleProvides.of(
              CClassNames.toDottedName(providing.service),
              CClassNames.toDottedName(provider)));
          });
        });
    }

    if (module.exports != null) {
      module.exports.forEach(
        export -> {
          final String pack = export.packaze.replace('/', '.');
          if (export.modules != null) {
            export.modules.forEach(mod -> {
              LOG.trace("export qualified: {} -> {}", pack, mod);
              b.addExportsQualified(CModuleQualifiedExport.of(pack, mod));
            });
          } else {
            LOG.trace("export unqualified: {}", pack);
            b.addExportsUnqualified(pack);
          }
        });
    }

    if (module.opens != null) {
      module.opens.forEach(
        open -> {
          final String pack = open.packaze.replace('/', '.');
          if (open.modules != null) {
            open.modules.forEach(mod -> {
              LOG.trace("opens qualified: {} -> {}", pack, mod);
              b.addOpensQualified(CModuleQualifiedOpens.of(pack, mod));
            });
          } else {
            LOG.trace("opens unqualified: {}", pack);
            b.addOpensUnqualified(pack);
          }
        });
    }

    if (module.requires != null) {
      module.requires.forEach(requires -> {
        LOG.trace("requires: {}", requires.module);
        final CModuleRequires req =
          CModuleRequires.of(requires.module, requiresIsTransitive(requires));
        b.putRequires(requires.module, req);
      });
    }

    return b.build();
  }

  private static boolean requiresIsTransitive(
    final ModuleRequireNode requires)
  {
    return (requires.access & Opcodes.ACC_TRANSITIVE) == Opcodes.ACC_TRANSITIVE;
  }

  private static final class ModuleReader extends ClassVisitor
  {
    private ModuleNode module;

    ModuleReader()
    {
      super(Opcodes.ASM6);
    }

    @Override
    public ModuleVisitor visitModule(
      final String name,
      final int access,
      final String version)
    {
      LOG.trace("module: {} {} {}", name, Integer.valueOf(access), version);
      this.module = new ModuleNode(name, access, version);
      return this.module;
    }
  }
}
