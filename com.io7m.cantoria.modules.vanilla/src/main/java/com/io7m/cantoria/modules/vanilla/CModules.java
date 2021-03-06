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

package com.io7m.cantoria.modules.vanilla;

import com.io7m.cantoria.api.CArchiveDescriptor;
import com.io7m.cantoria.api.CArchiveType;
import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassName;
import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CClasses;
import com.io7m.cantoria.api.CModuleDescriptor;
import com.io7m.cantoria.api.CModuleDescriptors;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.api.CVersions;
import com.io7m.cantoria.modules.api.CModuleLoaderType;
import com.io7m.jaffirm.core.Preconditions;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.ModuleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Functions to load modules.
 */

public final class CModules implements CModuleLoaderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CModules.class);

  private static final Pattern MATCH_CLASS_NAME =
    Pattern.compile("\\.class$");

  /**
   * Instantiate a module loader. This method is intended to be used by
   * {@link java.util.ServiceLoader}.
   */

  public CModules()
  {

  }

  /**
   * Open an archive and parse the module descriptor.
   *
   * @param path    The path to the file, for diagnostic purposes
   * @param version The archive version
   * @param input   The archive input
   *
   * @return An opened module
   *
   * @throws IOException On I/O errors
   */

  @Override
  public CModuleType openFromZip(
    final Path path,
    final CVersion version,
    final ZipFile input)
    throws IOException
  {
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(version, "Version");
    Objects.requireNonNull(input, "Input");

    final CArchiveDescriptor archive_descriptor =
      CArchiveDescriptor.of(path, version);

    final ZipEntry entry = input.getEntry("module-info.class");
    try (InputStream stream = input.getInputStream(entry)) {
      final ModuleNode module_node =
        CModuleDescriptors.loadModuleNode(stream);
      final CModuleDescriptor module =
        CModuleDescriptors.loadModuleDescriptor(module_node);

      final ZipArchive zip_archive =
        new ZipArchive(input, archive_descriptor);
      return new OrdinaryModule(zip_archive, module, module_node);
    }
  }

  /**
   * Open an archive and parse the module descriptor.
   *
   * @param path    The path to the file, for diagnostic purposes
   * @param version The archive version
   *
   * @return An opened module
   *
   * @throws IOException On I/O errors
   */

  @Override
  public CModuleType open(
    final Path path,
    final CVersion version)
    throws IOException
  {
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(version, "Version");
    return this.openFromZip(path, version, new ZipFile(path.toFile()));
  }

  @Override
  public String name()
  {
    return CModules.class.getCanonicalName();
  }

  /**
   * List the available platform modules.
   *
   * @return The list of available platform modules
   *
   * @throws IOException On I/O errors
   */

  @Override
  public SortedSet<String> listPlatformModules()
    throws IOException
  {
    TreeSet<String> names = TreeSet.empty();

    final FileSystem filesystem =
      FileSystems.getFileSystem(URI.create("jrt:/"));
    final Path modules_root =
      filesystem.getPath("modules");

    for (final Path path : TreeSet.ofAll(Files.list(modules_root))) {
      names = names.add(path.getFileName().toString());
    }

    return names;
  }

  /**
   * Open the named platform modules.
   *
   * @param name The modules name
   *
   * @return An opened modules
   *
   * @throws IOException On I/O errors
   */

  @Override
  public CModuleType openPlatformModule(
    final String name)
    throws IOException
  {
    final FileSystem filesystem =
      FileSystems.getFileSystem(URI.create("jrt:/"));
    final Path module_path =
      filesystem.getPath("modules", name);
    final Path module_descriptor_path =
      module_path.resolve("module-info.class");

    if (LOG.isTraceEnabled()) {
      LOG.trace("loading module descriptor from {}", module_descriptor_path);
    }

    try (InputStream stream = Files.newInputStream(module_descriptor_path)) {
      final ModuleNode module_node =
        CModuleDescriptors.loadModuleNode(stream);
      final CModuleDescriptor module_desc =
        CModuleDescriptors.loadModuleDescriptor(module_node);

      final PlatformArchive archive =
        new PlatformArchive(CArchiveDescriptor.of(
          module_path, CVersions.parseNullable(module_node.version)));

      return new PlatformModule(
        archive,
        module_path,
        module_node,
        module_desc);
    }
  }

  private static final class ZipArchive implements CArchiveType
  {
    private final ZipFile zip;
    private final CArchiveDescriptor descriptor;
    private boolean closed;

    ZipArchive(
      final ZipFile in_zip,
      final CArchiveDescriptor in_descriptor)
    {
      this.zip =
        Objects.requireNonNull(in_zip, "Zip");
      this.descriptor =
        Objects.requireNonNull(in_descriptor, "Descriptor");
    }

    @Override
    public boolean isClosed()
    {
      return this.closed;
    }

    @Override
    public CArchiveDescriptor descriptor()
    {
      return this.descriptor;
    }

    @Override
    public void close()
      throws IOException
    {
      try {
        if (!this.closed) {
          LOG.debug("close: {}", this.descriptor.path());
          this.zip.close();
        }
      } finally {
        this.closed = true;
      }
    }
  }

  private static final class PlatformArchive implements CArchiveType
  {
    private final CArchiveDescriptor descriptor;
    private boolean closed;

    PlatformArchive(
      final CArchiveDescriptor in_descriptor)
    {
      this.descriptor =
        Objects.requireNonNull(in_descriptor, "Descriptor");
    }

    @Override
    public boolean isClosed()
    {
      return this.closed;
    }

    @Override
    public CArchiveDescriptor descriptor()
    {
      return this.descriptor;
    }

    @Override
    public void close()
      throws IOException
    {
      try {
        if (!this.closed) {
          LOG.debug("close: {}", this.descriptor.path());
        }
      } finally {
        this.closed = true;
      }
    }
  }

  private static final class PlatformModule implements CModuleType
  {
    private final ModuleNode module_node;
    private final CModuleDescriptor module_desc;
    private final Path module_path;
    private final CArchiveType archive;

    PlatformModule(
      final CArchiveType in_archive,
      final Path in_module_path,
      final ModuleNode in_module_node,
      final CModuleDescriptor in_module_desc)
    {
      this.archive =
        Objects.requireNonNull(in_archive, "Archive");
      this.module_path =
        Objects.requireNonNull(in_module_path, "Module path");
      this.module_node =
        Objects.requireNonNull(in_module_node, "Module node");
      this.module_desc =
        Objects.requireNonNull(in_module_desc, "Module descriptor");
    }

    private static boolean looksLikeClassFile(
      final Path path)
    {
      return path.getFileName().toString().endsWith(".class");
    }

    @Override
    public boolean isClosed()
    {
      return this.archive.isClosed();
    }

    @Override
    public CModuleDescriptor descriptor()
    {
      return this.module_desc;
    }

    @Override
    public CArchiveType archive()
    {
      return this.archive;
    }

    @Override
    public ModuleNode node()
    {
      return this.module_node;
    }

    @Override
    public Optional<InputStream> classBytes(
      final String package_name,
      final String class_name)
      throws IOException
    {
      Objects.requireNonNull(package_name, "Package");
      Objects.requireNonNull(class_name, "Class");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final Path path =
        this.module_path.resolve(CClassNames.toDashedName(package_name))
          .resolve(class_name + ".class");

      if (Files.exists(path)) {
        return Optional.of(Files.newInputStream(path));
      }

      return Optional.empty();
    }

    @Override
    public Optional<CClass> classValue(
      final String package_name,
      final String class_name)
      throws IOException
    {
      Objects.requireNonNull(package_name, "Package");
      Objects.requireNonNull(class_name, "Class");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final Optional<InputStream> opt_stream =
        this.classBytes(package_name, class_name);

      if (opt_stream.isPresent()) {
        try (InputStream stream = opt_stream.get()) {
          final ClassNode node = CClasses.classNodeFromStream(stream);
          final CClassName name =
            CClassName.of(this.module_desc.name(), package_name, class_name);
          return Optional.of(CClasses.classOf(name, this, node));
        }
      }

      return Optional.empty();
    }

    @Override
    public SortedSet<String> classesInPackage(
      final String package_name)
    {
      Objects.requireNonNull(package_name, "Package");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final Path path =
        this.module_path.resolve(CClassNames.toDashedName(package_name));

      try {
        return TreeSet.ofAll(
          Files.list(path)
            .filter(PlatformModule::looksLikeClassFile)
            .map(Path::toString));
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    @Override
    public void close()
      throws IOException
    {
      this.archive.close();
    }
  }

  private static final class OrdinaryModule implements CModuleType
  {
    private final ZipArchive archive;
    private final CModuleDescriptor module_desc;
    private final ModuleNode module_node;

    OrdinaryModule(
      final ZipArchive in_archive,
      final CModuleDescriptor in_module_descriptor,
      final ModuleNode in_module_node)
    {
      this.archive =
        Objects.requireNonNull(in_archive, "Archive");
      this.module_desc =
        Objects.requireNonNull(in_module_descriptor, "Module descriptor");
      this.module_node =
        Objects.requireNonNull(in_module_node, "Module node");
    }

    private static String parseClassName(
      final String file_name)
    {
      final String no_class =
        MATCH_CLASS_NAME.matcher(file_name).replaceAll("");
      return CClassNames.parseFullyQualifiedDotted(
        CClassNames.toDottedName(no_class))._2;
    }

    private static boolean looksLikeClassInPackage(
      final String package_slash,
      final ZipEntry entry)
    {
      return entry.getName().startsWith(package_slash)
        && entry.getName().endsWith(".class");
    }

    @Override
    public String toString()
    {
      return new StringBuilder(128)
        .append("[")
        .append(this.module_desc.name())
        .append(" (")
        .append(this.archive.descriptor.path())
        .append(")")
        .append("]")
        .toString();
    }

    @Override
    public Optional<InputStream> classBytes(
      final String package_name,
      final String class_name)
      throws IOException
    {
      Objects.requireNonNull(package_name, "Package name");
      Objects.requireNonNull(class_name, "Class name");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final StringBuilder sb = new StringBuilder(64);
      if (!package_name.isEmpty()) {
        sb.append(package_name.replace(".", "/"));
        sb.append("/");
      }
      sb.append(class_name);
      sb.append(".class");

      final String name = sb.toString();
      LOG.debug("classBytes: {}: {}", this.archive.zip.getName(), name);

      final ZipEntry e = this.archive.zip.getEntry(name);
      if (e == null) {
        return Optional.empty();
      }
      return Optional.of(this.archive.zip.getInputStream(e));
    }

    @Override
    public Optional<CClass> classValue(
      final String package_name,
      final String class_name)
      throws IOException
    {
      Objects.requireNonNull(package_name, "Package");
      Objects.requireNonNull(class_name, "Class");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final Optional<InputStream> opt_stream =
        this.classBytes(package_name, class_name);

      if (opt_stream.isPresent()) {
        try (InputStream stream = opt_stream.get()) {
          final ClassNode node = CClasses.classNodeFromStream(stream);
          final CClassName name =
            CClassName.of(this.module_desc.name(), package_name, class_name);
          return Optional.of(CClasses.classOf(name, this, node));
        }
      }

      return Optional.empty();
    }

    @Override
    public SortedSet<String> classesInPackage(
      final String package_name)
    {
      Objects.requireNonNull(package_name, "Package");

      Preconditions.checkPrecondition(
        !this.isClosed(), "Module archive must be open");

      final String package_slash =
        CClassNames.toDashedName(package_name);
      final Spliterator<ZipEntry> split =
        Spliterators.spliteratorUnknownSize(
          this.archive.zip.entries().asIterator(),
          Spliterator.ORDERED);

      return TreeSet.ofAll(
        StreamSupport.stream(split, false)
          .filter(e -> looksLikeClassInPackage(package_slash, e))
          .map(e -> parseClassName(e.getName())));
    }

    @Override
    public boolean isClosed()
    {
      return this.archive.isClosed();
    }

    @Override
    public CModuleDescriptor descriptor()
    {
      return this.module_desc;
    }

    @Override
    public CArchiveType archive()
    {
      return this.archive;
    }

    @Override
    public ModuleNode node()
    {
      return this.module_node;
    }

    @Override
    public void close()
      throws IOException
    {
      this.archive.close();
    }
  }
}
