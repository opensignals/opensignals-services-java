/*
 * Copyright © 2020 OpenSignals Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package io.opensignals.services.ext.spi.alpha;

import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Environment;
import io.opensignals.services.plugin.ServicesPlugin;
import io.opensignals.services.plugin.ServicesPluginFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static io.opensignals.services.ext.spi.alpha.Environments.remap;
import static java.lang.Class.forName;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.regex.Pattern.compile;

/**
 * A utility class for loading and configuring plugin factories and
 * augmenting a {@link Contexts.Context} with a {@link ServicesPlugin}.
 *
 * @author wlouth
 * @since 1.0
 */

final class Plugins {

  private static final String FACTORY_CLASS_DEFAULT_PATTERN =
    ServicesPlugin.class.getPackage ().getName () +
      ".%s." + ServicesPluginFactory.class.getSimpleName ();

  private static final Map< String, Entry > ENTRIES =
    new ConcurrentHashMap<> ();

  private static final Names.Name PATH_PREFIX =
    Names.root ( Strings.OPENSIGNALS )
      .node ( Strings.SERVICES );

  private static final Names.Name PLUGINS_PATH =
    PATH_PREFIX.node (
      Strings.PLUGINS
    );

  private static final Names.Name PLUGIN_PATH =
    PATH_PREFIX.node (
      Strings.PLUGIN
    );

  private static final Variables.Variable< String > PLUGINS =
    Variables.of (
      PLUGINS_PATH,
      (String) null
    );

  private static final Pattern PATTERN = compile ( "," );

  private Plugins () {}

  static Context apply (
    final Context context
  ) {

    final String plugins =
      PLUGINS.of (
        context.getEnvironment ()
      );

    if ( plugins != null ) {

      // scope the environment to
      // the plugin configuration

      final Environment environment =
        context.getEnvironment ();

      PATTERN.splitAsStream (
        plugins
      ).forEach (
        name ->
          install (
            context,
            environment,
            name
          )
      );

    }

    return
      context;

  }

  private static void install (
    final Context context,
    final Environment environment,
    final String name
  ) {

    pluginOf (
      name,
      environment
    ).ifPresent (
      plugin ->
        plugin.apply (
          context
        )
    );

  }


  private static Optional< ServicesPlugin > pluginOf (
    final String name,
    final Environment environment
  ) {

    final Entry entry =
      entryOf (
        environment.getString (
          PLUGIN_PATH
            .name ( name ) // we use name in case the plugin uses a separator
            .node ( Strings.FACTORY )
            .node ( Strings.CLASS ),
          format (
            FACTORY_CLASS_DEFAULT_PATTERN,
            name
          )
        )
      );

    return
      entry != Entry.FAILED
      ? newPlugin ( name, environment, entry.factory )
      : empty ();

  }


  private static Entry entryOf (
    final String factoryName
  ) {

    final Entry entry =
      ENTRIES.get (
        factoryName
      );

    return
      nonNull ( entry )
      ? entry
      : ENTRIES.computeIfAbsent ( factoryName, Plugins::newEntry );

  }


  private static Optional< ServicesPlugin > newPlugin (
    final String name,
    final Environment environment,
    final ServicesPluginFactory factory
  ) {

    try {

      return
        factory.create (
          remap (
            environment,
            path ->
              PLUGIN_PATH.node (
                name
              ).node (
                Strings.ENVIRONMENT
              ).name (
                path.toString ()
              )
          )
        );

    } catch (
      final Exception error
    ) {

      return
        empty ();

    }

  }


  private static Entry newEntry (
    final String factoryName
  ) {

    try {

      return
        new Entry (
          (ServicesPluginFactory)
            forName ( factoryName )
              .getConstructor ()
              .newInstance ()
        );

    } catch (
      final Exception error
    ) {

      return Entry.FAILED;

    }

  }

  private static final class Entry {

    static final Entry FAILED = new Entry ();

    final ServicesPluginFactory factory;

    private Entry () {

      factory =
        null;

    }

    Entry (
      final ServicesPluginFactory factory
    ) {

      this.factory =
        factory;

    }

  }

}
