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

import static io.opensignals.services.Services.*;
import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.samples.Strings.SERVICE_ONE;
import static io.opensignals.services.samples.Strings.times;
import static java.lang.String.format;

final class Callbacks {

  private static final int    CLEAR   = 0;
  private static final String PATTERN = "%s %s%n";

  private static void wrapping () {

    final StringBuilder out =
      new StringBuilder ();

    final Callback< Signal > callback =
      ( orientation, signal ) ->
        out.append (
          format (
            PATTERN,
            orientation,
            signal
          )
        );

    final Service service =
      service (
        context ()
          .service (
            name (
              SERVICE_ONE
            )
          ),
        callback
      );

    service.call ();

    assert
      format (
        PATTERN,
        EMIT, CALL
      ).equals (
        out.toString ()
      );

    out.setLength (
      CLEAR
    );

    execute (
      service,
      () -> {
      }
    );

    assert
      format (
        times (
          PATTERN,
          3
        ),
        EMIT, START,
        EMIT, SUCCEED,
        EMIT, STOP
      ).equals (
        out.toString ()
      );

  }

  private Callbacks () {}

  public static void main (
    final String[] args
  ) {

    wrapping ();

  }

}
