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

package com.io7m.cantoria.cmdline;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.cantoria.api.CClassRegistry;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CModuleType;
import com.io7m.cantoria.api.CModuleWeaklyCaching;
import com.io7m.cantoria.api.CModules;
import com.io7m.cantoria.api.CVersion;
import com.io7m.cantoria.api.CVersions;
import com.io7m.cantoria.changes.api.CChangeType;
import com.io7m.cantoria.changes.api.CCompatibilityTracker;
import com.io7m.cantoria.changes.spi.CChangeCheckType;
import com.io7m.cantoria.changes.spi.CChangeDescriberType;
import com.io7m.cantoria.driver.api.CComparisonDriverProviderType;
import com.io7m.cantoria.driver.api.CComparisonDriverType;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import static com.io7m.cantoria.changes.api.CChangeBinaryCompatibility.BINARY_COMPATIBLE;
import static com.io7m.cantoria.changes.api.CChangeSourceCompatibility.SOURCE_COMPATIBLE;
import static com.io7m.cantoria.cmdline.CommandStatus.COMMAND_FAILURE;
import static com.io7m.cantoria.cmdline.CommandStatus.COMMAND_SUCCESS;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A command to compare two modules.
 */

@Parameters(
  commandNames = "compare",
  commandDescription = "Compare modules")
public final class CommandCompare implements CCommandType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CommandCompare.class);

  private final ArrayList<CModuleType> opened_modules;

  @Parameter(
    names = "--module-old",
    description = "A jar file containing the old version of the module",
    required = true)
  private String module_old;
  @Parameter(
    names = "--module-old-version",
    description = "The version string for the old module",
    required = true)
  private String module_old_version;
  @Parameter(
    names = "--module-new",
    description = "A jar file containing the new version of the module",
    required = true)
  private String module_new;
  @Parameter(
    names = "--module-new-version",
    description = "The version string for the new module",
    required = true)
  private String module_new_version;
  @Parameter(
    names = "--add-module",
    description = "Load extra platform modules used to resolve classes (may be specified multiple times)",
    required = false)
  private List<String> platform_modules = List.of("java.base");
  @Parameter(
    names = "--module-path",
    description = "Specify directories containing modules used to resolve classes (may be specified multiple times)",
    required = false)
  private Iterable<String> extra_module_directories = new ArrayList<>();

  /**
   * Construct a command.
   */

  public CommandCompare()
  {
    this.opened_modules = new ArrayList<>(32);
  }

  private static CommandStatus compareModules(
    final CClassRegistryType registry,
    final CComparisonDriverType driver,
    final CModuleType module_old,
    final CModuleType module_new,
    final ArrayList<Tuple2<CChangeCheckType, CChangeType>> changes)
  {
    try {
      driver.compareModules(
        (originator, change) -> changes.add(Tuple.of(originator, change)),
        registry,
        module_old,
        module_new);
      return COMMAND_SUCCESS;
    } catch (final IOException e) {
      LOG.error(
        "I/O error during module comparison: {}: {}: ",
        e.getClass().getCanonicalName(),
        e.getMessage(),
        e);
      return COMMAND_FAILURE;
    }
  }

  private static CommandStatus writeReport(
    final CVersion module_old_version,
    final CVersion module_new_version,
    final Iterable<Tuple2<CChangeCheckType, CChangeType>> changes)
  {
    try {
      final ServiceLoader<CChangeDescriberType> providers =
        ServiceLoader.load(CChangeDescriberType.class);

      final CCompatibilityTracker tracker = CCompatibilityTracker.create();

      final String format = "com.io7m.cantoria.format.text";
      for (final Tuple2<CChangeCheckType, CChangeType> change_pair : changes) {
        final CChangeType change = change_pair._2;
        tracker.onChange(change);

        boolean described = false;
        final Iterator<CChangeDescriberType> iter = providers.iterator();
        while (iter.hasNext()) {
          final CChangeDescriberType describer = iter.next();
          if (Objects.equals(describer.format(), format)
            && describer.canDescribe(change)) {
            try {
              describer.describe(change_pair._1, change, System.out);
              described = true;
            } catch (final IOException e) {
              throw new UncheckedIOException(e);
            }
          }
        }

        if (!described) {
          LOG.error(
            "No available describer of format {} for change type {}",
            format,
            change.getClass().getCanonicalName());
          return COMMAND_FAILURE;
        }
      }

      writeVersionCompatibilityReport(module_old_version, tracker);

      return COMMAND_SUCCESS;
    } catch (final IOException e) {
      LOG.error(
        "I/O error during report generation comparison: {}: {}: ",
        e.getClass().getCanonicalName(),
        e.getMessage(),
        e);
      return COMMAND_FAILURE;
    } catch (final UncheckedIOException e) {
      final IOException ec = e.getCause();
      LOG.error(
        "I/O error during report generation comparison: {}: {}: ",
        ec.getClass().getCanonicalName(),
        ec.getMessage(),
        ec);
      return COMMAND_FAILURE;
    }
  }

  private static void writeVersionCompatibilityReport(
    final CVersion module_old_version,
    final CCompatibilityTracker tracker)
    throws IOException
  {
    final BufferedWriter w =
      new BufferedWriter(new OutputStreamWriter(System.out, UTF_8));

    w.append("Modules are binary compatible: ");
    w.append(tracker.binaryCompatibility() == BINARY_COMPATIBLE ? "Yes" : "No");
    w.newLine();

    w.append("Modules are source compatible: ");
    w.append(tracker.sourceCompatibility() == SOURCE_COMPATIBLE ? "Yes" : "No");
    w.newLine();

    w.append("Required version change:       ");

    switch (tracker.semanticVersioning()) {
      case SEMANTIC_MAJOR: {
        w.append("Major increment");
        break;
      }
      case SEMANTIC_MINOR: {
        w.append("Minor increment");
        break;
      }
      case SEMANTIC_NONE: {
        w.append("None");
        break;
      }
    }

    w.newLine();
    w.append("Suggested new version:         ");
    w.append(CVersions.showVersion(module_old_version));
    w.append(" → ");
    w.append(CVersions.showVersion(
      tracker.suggestVersionNumber(module_old_version)));
    w.newLine();
    w.flush();
  }


  @Override
  public CommandStatus run()
  {
    final Path mo_path = Paths.get(this.module_old);
    final CVersion mo_version = CVersions.parse(this.module_old_version);
    final Path mn_path = Paths.get(this.module_new);
    final CVersion mn_version = CVersions.parse(this.module_new_version);

    final Optional<CComparisonDriverProviderType> driver_opt =
      ServiceLoader.load(CComparisonDriverProviderType.class).findFirst();
    if (!driver_opt.isPresent()) {
      LOG.error("No comparison driver providers available.");
      return COMMAND_FAILURE;
    }

    final CComparisonDriverProviderType driver_provider = driver_opt.get();
    final CComparisonDriverType driver = driver_provider.create();

    try (CModuleType module_old = CModules.open(mo_path, mo_version)) {
      try (CModuleType module_new = CModules.open(mn_path, mn_version)) {
        try {
          if (this.loadModules() == COMMAND_FAILURE) {
            return COMMAND_FAILURE;
          }

          this.opened_modules.add(module_old);
          this.opened_modules.add(module_new);

          final CClassRegistryType registry =
            CClassRegistry.create(io.vavr.collection.List.ofAll(this.opened_modules));

          final ArrayList<Tuple2<CChangeCheckType, CChangeType>> changes =
            new ArrayList<>(32);

          if (compareModules(registry, driver, module_old, module_new, changes)
            == COMMAND_FAILURE) {
            return COMMAND_FAILURE;
          }

          return writeReport(mo_version, mn_version, changes);
        } finally {
          this.unloadModules();
        }
      } catch (final IOException e) {
        LOG.error(
          "Could not load new module: {}: {}: ",
          this.module_old,
          e.getClass().getCanonicalName(),
          e);
        return COMMAND_FAILURE;
      }
    } catch (final IOException e) {
      LOG.error(
        "Could not load old module: {}: {}: ",
        this.module_old,
        e.getClass().getCanonicalName(),
        e);
      return COMMAND_FAILURE;
    }
  }

  private void unloadModules()
  {
    this.opened_modules.forEach(module -> {
      try {
        module.close();
      } catch (final Exception e) {
        LOG.error(
          "Could not close archive {}: {}: ",
          module.archive().descriptor().path(),
          e.getMessage(),
          e);
      }
    });
  }

  private CommandStatus loadModules()
  {
    if (this.loadPlatformModules(this.opened_modules) == COMMAND_FAILURE) {
      return COMMAND_FAILURE;
    }
    if (this.loadExtraModules(this.opened_modules) == COMMAND_FAILURE) {
      return COMMAND_FAILURE;
    }

    return COMMAND_SUCCESS;
  }

  private CommandStatus loadPlatformModules(
    final Collection<CModuleType> modules)
  {
    CommandStatus status = COMMAND_SUCCESS;

    for (final String name : this.platform_modules) {
      try {
        modules.add(CModuleWeaklyCaching.wrap(CModules.openPlatformModule(name)));
      } catch (final IOException e) {
        LOG.error(
          "Failed to load module {}: {}: ",
          name,
          e.getClass().getCanonicalName(),
          e);
        status = COMMAND_FAILURE;
      }
    }
    return status;
  }

  private CommandStatus loadExtraModules(
    final Collection<CModuleType> modules)
  {
    CommandStatus status = COMMAND_SUCCESS;

    for (final String path_name : this.extra_module_directories) {
      final Path dir_path = Paths.get(path_name);
      final List<Path> archives;

      try {
        archives = Files.list(dir_path).collect(Collectors.toList());
      } catch (final IOException e) {
        LOG.error(
          "Unable to list directory {}: {}: ",
          dir_path,
          e.getClass().getCanonicalName(),
          e);
        status = COMMAND_FAILURE;
        continue;
      }

      for (final Path archive : archives) {
        try {
          modules.add(CModuleWeaklyCaching.wrap(CModules.openFromZip(
            archive,
            CVersion.of(0, 0, 0, ""),
            new ZipFile(archive.toFile()))));
        } catch (final IOException e) {
          LOG.error(
            "Unable to load archive {}: {}: ",
            archive,
            e.getClass().getCanonicalName(),
            e);
          status = COMMAND_FAILURE;
          continue;
        }
      }
    }
    return status;
  }
}
