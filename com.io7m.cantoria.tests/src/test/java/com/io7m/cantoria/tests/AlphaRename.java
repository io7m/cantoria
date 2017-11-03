package com.io7m.cantoria.tests;

import com.io7m.cantoria.api.CGClassSignature;
import com.io7m.cantoria.api.CGenericsUniqueNames;
import com.io7m.cantoria.api.CGenericsParsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AlphaRename
{
  private AlphaRename()
  {

  }

  public static void main(
    final String[] args)
    throws IOException
  {
    try (BufferedReader reader =
           new BufferedReader(new InputStreamReader(System.in, UTF_8))) {

      while (true) {
        final String line = reader.readLine();
        if (line == null) {
          return;
        }

        try {
          final CGClassSignature g = CGenericsParsing.parseClassSignature(line);
          System.out.println(g.toJava());

          final CGenericsUniqueNames.UniqueNamesType names =
            CGenericsUniqueNames.emptyUniqueNames();
          final CGClassSignature h =
            CGenericsUniqueNames.uniqueClassSignature(names, g);

          System.out.println(h.toJava());
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
