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
import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Subscription;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static java.lang.String.format;

final class Subscribers {

  private static final int CLEAR = 0;

  private Subscribers () {}

  public static void main (
    final String[] args
  ) {

    subscribing ();

  }

  private static void subscribing () {

    final StringBuilder out =
      new StringBuilder ();

    final Context context =
      Services.context ();

    final Services.Name n1 =
      Services.name (
        "service.one"
      );

    final Services.Name n2 =
      Services.name (
        "service.two"
      );

    final Subscription subscription =
      context
        .subscribe (
          ( name, registrar ) -> {
            if ( name == n1 )
              registrar.accept (
                ( orientation, signal ) ->
                  out.append (
                    format (
                      "%s %s %s%n",
                      name,
                      orientation,
                      signal
                    )
                  )
              );
          },
          Signal.class
        );

    context
      .service ( n1 )
      .start ()
      .succeed ()
      .stop ();

    assert
      format (
        "%s %s %s%n" +
          "%s %s %s%n" +
          "%s %s %s%n",
        n1, EMIT, START,
        n1, EMIT, SUCCEED,
        n1, EMIT, STOP
      ).equals (
        out.toString ()
      );

    out.setLength (
      CLEAR
    );

    context
      .service ( n2 )
      .start ()
      .succeed ()
      .stop ();

    assert
      out.length ()
        == CLEAR;

    subscription
      .cancel ();

    context
      .service ( n1 )
      .start ()
      .succeed ()
      .stop ();

    assert
      out.length ()
        == CLEAR;

  }

}
