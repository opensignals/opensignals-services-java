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
import io.opensignals.services.Services.Service;
import io.opensignals.services.Services.Status;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.opensignals.services.Services.Status.*;

final class Statuses {

  public static void main (
    final String[] args
  ) {

    ok ();
    degraded ();
    defective ();
    down ();

  }

  private static void ok () {

    final Service service =
      Services
        .context ()
        .service (
          Services.name (
            "service.local"
          )
        );

    assert
      service.getStatus ()
        == UNKNOWN;

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
      Services
        .context ()
        .service (
          Services.name (
            "service.remote"
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
      Services
        .context ()
        .service (
          Services.name (
            "service.remote"
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

    final Services.Name name =
      Services.name (
        "service.remote"
      );

    final Context context =
      Services.context ();

    final AtomicBoolean guard =
      new AtomicBoolean (
        false
      );

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

}