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

final class SingleService {

  private static final Name S1 =
    Services
      .name ( "com" )
      .name ( "acme" )
      .name ( "service" )
      .name ( "one" );

  public static void main (
    final String[] args
  ) {

    final Context context =
      Services.context ();

    final Service s1 =
      context.service (
        S1
      );

    // version #1

    s1.start ();
    // do some work
    s1.succeed ()
      .stop ();

    // version #2

    s1.emit (
      START
    );
    // do some work
    s1.emit (
      SUCCEED,
      STOP
    );

    // version #3

    Services.execute (
      s1,
      () -> {
        // do some work
      }
    );

  }

}
