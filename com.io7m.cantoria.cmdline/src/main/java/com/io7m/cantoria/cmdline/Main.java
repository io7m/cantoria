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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import io.vavr.collection.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.io7m.cantoria.cmdline.CommandStatus.COMMAND_FAILURE;

/**
 * The main entry point for the command line.
 */

public final class Main
{
  private static final Logger LOG =
    LoggerFactory.getLogger(Main.class);

  private final JCommander jcommander;
  private final String[] args;
  private final HashMap<String, CommandCompare> commands;

  private Main(
    final String[] in_args)
  {
    this.args = Objects.requireNonNull(in_args, "Arguments");

    final CommandCompare cc = new CommandCompare();
    this.commands = HashMap.of("compare", cc);
    this.jcommander =
      JCommander.newBuilder()
        .programName("cantoria")
        .addCommand(cc)
        .build();
  }

  /**
   * Main entry point.
   *
   * @param args Command line arguments
   */

  public static void main(
    final String[] args)
  {
    final Main main = new Main(args);
    System.exit(main.run().exitCode());
  }

  /**
   * Execute the parsed command.
   *
   * @return The resulting command status
   */

  public CommandStatus run()
  {
    try {
      if (this.args.length == 0) {
        this.usage();
        return COMMAND_FAILURE;
      }

      this.jcommander.parse(this.args);

      final String cmd = this.jcommander.getParsedCommand();
      if (cmd == null) {
        this.usage();
        return COMMAND_FAILURE;
      }

      final CCommandType command = this.commands.get(cmd).get();
      return command.run();
    } catch (final ParameterException e) {
      LOG.error("Usage error: {}", e.getMessage());
      this.usage();
      return COMMAND_FAILURE;
    }
  }

  private void usage()
  {
    final StringBuilder sb = new StringBuilder(128);
    this.jcommander.usage(sb);
    LOG.info("usage: {}", sb.toString());
  }
}
