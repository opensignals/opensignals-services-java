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

import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Phenomenon;
import io.opensignals.services.plugin.ServicesPlugin;

import static java.lang.System.out;

/**
 * A simple implementation of a plugin that writes a message to a file channel
 *
 * @author wlouth
 * @since 1.0
 */

final class Plugin
  implements ServicesPlugin {

  private final String pattern;

  Plugin (
    final String pattern
  ) {

    this.pattern =
      pattern;

  }


  @Override
  public void apply (
    final Context context
  ) {

    context.subscribe (
      ( name, registrar ) ->
        registrar.accept (
          ( orientation, phenomenon ) ->
            log (
              name,
              orientation,
              phenomenon
            )
        )
    );

  }

  private void log (
    final Name name,
    final Orientation orientation,
    final Phenomenon phenomenon
  ) {

    //noinspection UseOfSystemOutOrSystemErr
    out.printf (
      pattern + "%n",
      name,
      orientation,
      phenomenon
    );

  }

}
