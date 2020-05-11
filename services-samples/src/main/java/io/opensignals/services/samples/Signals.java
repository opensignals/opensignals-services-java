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

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.context;
import static io.opensignals.services.Services.name;

final class Signals {

  private static void execute () {

    final Context context =
      context ();

    final Service local =
      context
        .service (
          name (
            "service"
          ).name (
            "one"
          )
        );

    Services
      .execute (
        local,
        () -> {
        }
      );

    local
      .start ()
      .succeed ()
      .stop ();

    local
      .start ( EMIT )
      .succeed ( EMIT )
      .stop ( EMIT );

    local
      .emit (
        START
      );
    local
      .emit (
        SUCCEED,
        STOP
      );

    local
      .signal (
        EMIT,
        START
      );
    local
      .signal (
        EMIT,
        SUCCEED,
        STOP
      );

  }

  private static void call () {

    final Context context =
      context ();

    final Service remote =
      context
        .service (
          name (
            "service"
          ).name (
            "one"
          )
        );

    Services
      .call (
        remote,
        () -> {
        }
      );

    remote
      .call ()
      .succeeded ();

    remote
      .call ( EMIT )
      .succeed ( RECEIPT );

    remote
      .emit (
        CALL
      );
    remote
      .receipt (
        SUCCEED
      );

    remote
      .signal (
        EMIT,
        CALL
      );
    remote
      .signal (
        RECEIPT,
        SUCCEED
      );

  }

  private Signals () {}

  public static void main (
    final String[] args
  ) {

    execute ();
    call ();

  }

}

