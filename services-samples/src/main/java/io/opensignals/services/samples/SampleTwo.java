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
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Service;

import static io.opensignals.services.Services.context;
import static io.opensignals.services.Services.name;

public final class SampleTwo {

  private static final Name S1 =
    name ( "com.acme.service.one" );

  private static final Name S2 =
    name ( "com.acme.service.two" );

  public static void main (
    final String[] args
  ) {

    final Context context =
      context ();

    context.subscribe (
      ( name, registrar ) ->
        registrar.accept (
          ( orientation, value ) ->
            System.out.println ( name + "=" + value )
        ),
      Services.Signal.class
    );

    final Service s1 =
      context.service (
        S1.name (
          "post"
        )
      );

    final Service s2 =
      context.service (
        S2.name (
          "get"
        )
      );

    s1.start ();              // EMIT -> START
    {
      s2.call ();             // EMIT -> CALL
      s2.succeeded ();        // RECEIPT -> SUCCEED
    }
    s1.succeed ()             // EMIT -> SUCCEED
      .stop ();               // EMIT -> STOP

  }

}
