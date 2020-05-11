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
import static io.opensignals.services.samples.Strings.SERVICE_ONE;

final class Ops {

  @SuppressWarnings (
    "SameReturnValue"
  )
  private static int doWork () {

    return 1;

  }

  private Ops () {}

  @SuppressWarnings ( "unused" )
  static int doWork (
    final int units
  ) {

    return
      units;

  }

  public static void main (
    final String[] args
  ) throws Throwable {

    final Service service =
      context ()
        .service (
          name (
            SERVICE_ONE
          )
        );

    //noinspection Convert2MethodRef
    assert
      1 ==
        execute (
          service,
          () ->
            doWork ()
        );

    assert
      1 ==
        execute (
          service,
          (Fn< Integer, ? >)
            Ops::doWork
        );

    //noinspection ResultOfMethodCallIgnored
    execute (
      service,
      Op.of (
        Ops::doWork
      )
    );

    execute (
      service,
      Op.from (
        Ops::doWork
      )
    );

  }

}
