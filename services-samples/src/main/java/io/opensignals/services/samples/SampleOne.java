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

import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.context;

public final class SampleOne {

  // names exist outside of a context and can be
  // created and treated as constant string paths

  private static final Name S1 =
    Services
      .name ( "com" )
      .name ( "acme" )
      .name ( "service" )
      .name ( "one" );

  public static void main (
    final String[] args
  ) {

    // use the default context for this process
    // assuming a defined the root service name

    final Context context =
      context ();

    final Service s1 =
      context.service (
        S1
      );

    // signal s1 is starting the execution of work
    // then indicate it was successful and has stopped
    // the ordering is not so important as in tracing
    // because we use a buffer for signals to determine
    // the actual state of the service in operation

    s1.start ();
    // do some work
    s1.succeed ()
      .stop ();

    // alternatively

    s1.emit (
      START
    );
    // do some work
    s1.emit (
      SUCCEED,
      STOP
    );

  }

}
