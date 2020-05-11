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
import static io.opensignals.services.samples.Strings.ONE;
import static io.opensignals.services.samples.Strings.SERVICE;

final class SingleService {

  private static final Name S1 =
    name ( SERVICE )
      .name ( ONE );

  private SingleService () {}

  public static void main (
    final String[] args
  ) {

    final Context context =
      context ();

    final Service s1 =
      context
        .service (
          S1
        );

    s1.emit ( START );
    s1.emit (
      SUCCEED,
      STOP
    );

    s1.start ( EMIT );
    s1.succeed ( EMIT )
      .stop ( EMIT );

    s1.start ();
    s1.succeed ()
      .stop ();

    execute (
      s1,
      () -> {
      }
    );

  }

}
