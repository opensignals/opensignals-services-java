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

import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Service;
import io.opensignals.services.Services.Status;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.opensignals.services.Services.Status.*;
import static io.opensignals.services.Services.context;
import static io.opensignals.services.Services.name;
import static io.opensignals.services.samples.Strings.SERVICE_LOCAL;
import static io.opensignals.services.samples.Strings.SERVICE_REMOTE;

@SuppressWarnings ( "MethodCallInLoopCondition" )
final class Statuses {

  private static void ok () {

    final Service service =
      context ()
        .service (
          name (
            SERVICE_LOCAL
          )
        );

    assert
      service.getStatus ()
        == NONE;

    while (
      service.getStatus ()
        != OK
    ) {

      service
        .start ()
        .succeed ()
        .stop ();

    }

  }

  private static void degraded () {

    final Service service =
      context ()
        .service (
          name (
            SERVICE_REMOTE
          )
        );

    assert
      service.getStatus ()
        != DEGRADED;

    while (
      service.getStatus ()
        != DEGRADED
    ) {

      service
        .call ()
        .elapsed ()
        .retry ()
        .succeeded ();

    }

  }

  private static void defective () {

    final Service service =
      context ()
        .service (
          name (
            SERVICE_REMOTE
          )
        );

    assert
      service.getStatus ()
        != DEFECTIVE;

    while (
      service.getStatus ()
        != DEFECTIVE
    ) {

      service
        .call ()
        .failed ();

    }

  }

  private static void down () {

    final Name name =
      name (
        SERVICE_REMOTE
      );

    final Context context =
      context ();

    final AtomicBoolean guard =
      new AtomicBoolean (
        false
      );

    //noinspection OverlyLongLambda
    context
      .subscribe (
        ( n, registrar ) -> {
          if ( n == name ) {
            registrar
              .accept (
                ( orientation, status ) -> {
                  if ( status == DOWN )
                    guard.set (
                      true
                    );
                }
              );
          }
        },
        Status.class
      );

    final Service service =
      context.service (
        name
      );

    assert
      DOWN !=
        service.getStatus ();

    while (
      !guard.get ()
    ) {

      service
        .call ()
        .disconnected ()
        .failed ();

    }

  }

  private Statuses () {}

  public static void main (
    final String[] args
  ) {

    ok ();
    degraded ();
    defective ();
    down ();

  }

}