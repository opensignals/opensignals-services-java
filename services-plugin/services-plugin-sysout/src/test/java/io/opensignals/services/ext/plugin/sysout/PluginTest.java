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

package io.opensignals.services.ext.plugin.sysout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import static io.opensignals.services.Services.*;
import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.SUCCEED;
import static io.opensignals.services.Services.Status.OK;
import static java.lang.System.setOut;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

final class PluginTest {

  private static Context context;

  private static final PrintStream OUT =
    mock ( PrintStream.class );

  static {

    setOut (
      OUT
    );

  }

  @BeforeAll
  static void setup ()
  throws IOException {

    final Properties properties =
      new Properties ();

    try (
      final InputStream in =
        PluginTest.class.getResourceAsStream (
          "/opensignals-services.properties"
        )

    ) {

      properties.load (
        in
      );

    }

    final Environment environment =
      environment (
        name ->
          properties.getProperty (
            name.toString ()
          )
      );

    context =
      context (
        environment
      );

  }

  @Test
  void install () {

    final Name name =
      name ( "test" );

    context.service (
      name
    ).succeed ();

    verify ( OUT )
      .printf (
        "%s [%s] %s%n",
        name,
        EMIT,
        SUCCEED
      );

    verify ( OUT )
      .printf (
        "%s [%s] %s%n",
        name,
        EMIT,
        OK
      );

  }

}
