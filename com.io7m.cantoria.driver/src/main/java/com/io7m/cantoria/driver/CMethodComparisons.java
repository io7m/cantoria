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

package com.io7m.cantoria.driver;

import com.io7m.cantoria.api.CClass;
import com.io7m.cantoria.api.CClassNames;
import com.io7m.cantoria.api.CClassRegistryType;
import com.io7m.cantoria.api.CMethod;
import com.io7m.cantoria.api.CMethods;
import com.io7m.cantoria.changes.spi.CChangeReceiverType;
import com.io7m.cantoria.changes.spi.CMethodCheckAdditionType;
import com.io7m.cantoria.changes.spi.CMethodCheckRemovalType;
import com.io7m.cantoria.changes.spi.CMethodOverloadComparatorType;
import com.io7m.jaffirm.core.Invariants;
import com.io7m.jnull.NullCheck;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeMap;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

/**
 * Functions to compare methods.
 */

public final class CMethodComparisons
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CMethodComparisons.class);

  private ServiceLoader<CMethodOverloadComparatorType> overload_comparators;
  private ServiceLoader<CMethodCheckRemovalType> removal_checks;
  private ServiceLoader<CMethodCheckAdditionType> addition_checks;

  private CMethodComparisons()
  {
    this.overload_comparators =
      ServiceLoader.load(CMethodOverloadComparatorType.class);
    this.removal_checks =
      ServiceLoader.load(CMethodCheckRemovalType.class);
    this.addition_checks =
      ServiceLoader.load(CMethodCheckAdditionType.class);
  }

  /**
   * @return A method comparison driver
   */

  public static CMethodComparisons create()
  {
    return new CMethodComparisons();
  }

  private static TreeMap<String, Map<String, CMethod>> collectMethods(
    final CClass c)
  {
    TreeMap<String, Map<String, CMethod>> mm = TreeMap.empty();
    for (int index = 0; index < c.node().methods.size(); ++index) {
      final MethodNode c_method = c.node().methods.get(index);
      Map<String, CMethod> overloads =
        mm.getOrElse(c_method.name, HashMap.empty());
      Invariants.checkInvariant(
        !overloads.containsKey(c_method.desc),
        "Methods with the same name cannot have the same type descriptor");
      overloads = overloads.put(
        c_method.desc, CMethods.method(c.name(), c_method));
      mm = mm.put(c_method.name, overloads);
    }

    return mm;
  }

  /**
   * Compare all methods in the given classes.
   *
   * @param receiver  The change receiver
   * @param registry  A class registry
   * @param class_old The old class
   * @param class_new The new class
   */

  public void compareAllMethods(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new)
  {
    NullCheck.notNull(receiver, "Receiver");
    NullCheck.notNull(class_old, "Class (old)");
    NullCheck.notNull(class_new, "Class (new)");

    final TreeMap<String, Map<String, CMethod>> methods_old =
      collectMethods(class_old);
    final TreeMap<String, Map<String, CMethod>> methods_new =
      collectMethods(class_new);

    final SortedSet<String> all_names =
      methods_new.keySet().union(methods_old.keySet());

    for (final String name : all_names) {
      final Map<String, CMethod> method_types_old =
        methods_old.getOrElse(name, HashMap.empty());
      final Map<String, CMethod> method_types_new =
        methods_new.getOrElse(name, HashMap.empty());

      final Set<String> types_all =
        method_types_new.keySet().union(method_types_old.keySet());

      for (final String type : types_all) {
        LOG.debug("method {} {}", name, type);

        if (!method_types_old.containsKey(type)
          && method_types_new.containsKey(type)) {

          final CMethod method =
            method_types_new.get(type).get();
          final Map<String, CMethod> other_overloads =
            method_types_new.remove(type);

          this.onMethodAdded(
            receiver, registry, class_old, class_new, method, other_overloads);
          continue;
        }

        if (method_types_old.containsKey(type)
          && !method_types_new.containsKey(type)) {

          final CMethod method =
            method_types_old.get(type).get();
          final Map<String, CMethod> other_overloads =
            method_types_old.remove(type);

          this.onMethodRemoved(
            receiver, registry, class_old, class_new, method, other_overloads);
          continue;
        }

        final CMethod method_old = method_types_old.get(type).get();
        final CMethod method_new = method_types_new.get(type).get();
        this.compareMethodExactOverload(
          receiver, registry, class_old, method_old, class_new, method_new);
      }
    }
  }

  /**
   * A method was removed.
   *
   * @param receiver  A change receiver
   * @param method    The removed method
   * @param overloads The existing method overloads
   */

  private void onMethodRemoved(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    this.removal_checks.forEach(
      check -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            check.name(),
            CClassNames.show(class_new.name()),
            method.name());
        }

        check.checkMethodRemovalChecked(
          receiver, registry, class_old, class_new, method, overloads);
      });
  }

  /**
   * A method was added.
   *
   * @param receiver  A change receiver
   * @param method    The added method
   * @param overloads The existing method overloads
   */

  private void onMethodAdded(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass class_old,
    final CClass class_new,
    final CMethod method,
    final Map<String, CMethod> overloads)
  {
    this.addition_checks.forEach(
      check -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            check.name(),
            CClassNames.show(class_new.name()),
            method.name());
        }

        check.checkMethodAdditionChecked(
          receiver, registry, class_old, class_new, method, overloads);
      });
  }

  private void compareMethodExactOverload(
    final CChangeReceiverType receiver,
    final CClassRegistryType registry,
    final CClass c_old,
    final CMethod m_old,
    final CClass c_new,
    final CMethod m_new)
  {
    this.overload_comparators.forEach(
      compare -> {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
            "running {} for {}:{}",
            compare.name(),
            CClassNames.show(c_new.name()),
            m_new.name());
        }

        compare.compareMethodOverloadChecked(
          receiver, registry, c_old, m_old, c_new, m_new);
      });
  }
}
