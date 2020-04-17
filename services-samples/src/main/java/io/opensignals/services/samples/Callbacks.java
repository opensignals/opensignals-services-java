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

package io.opensignals.services.samples;

import io.opensignals.services.Services;
import io.opensignals.services.Services.Callback;
import io.opensignals.services.Services.Service;
import io.opensignals.services.Services.Signal;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static java.lang.String.format;

final class Callbacks {

  protected static final int CLEAR = 0;

  public static void main (
    final String[] args
  ) {

    wrapping ();

  }

  static void wrapping () {

    final StringBuilder out =
      new StringBuilder ();

    final Callback< Signal > callback =
      ( orientation, signal ) ->
        out.append (
          format (
            "%s %s%n",
            orientation,
            signal
          )
        );

    final Service service =
      Services.service (
        Services
          .context ()
          .service (
            Services.name (
              "service.one"
            )
          ),
        callback
      );

    service.call ();

    assert
      format (
        "%s %s%n",
        EMIT, CALL
      ).equals (
        out.toString ()
      );

    out.setLength (
      CLEAR
    );

    Services.execute (
      service,
      () -> {
      }
    );

    assert
      format (
        "%s %s%n" +
          "%s %s%n" +
          "%s %s%n",
        EMIT, START,
        EMIT, SUCCEED,
        EMIT, STOP
      ).equals (
        out.toString ()
      );

  }

}
