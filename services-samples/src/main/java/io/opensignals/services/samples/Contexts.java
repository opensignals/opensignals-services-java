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
import static io.opensignals.services.samples.Strings.*;

final class Contexts {

  private static void global () {

    final Name name =
      name (
        SERVICE_ONE
      );

    final Context context =
      context ();

    assert
      context ==
        context ();

    final Service service =
      context.service (
        name
      );

    assert
      service ==
        context.service (
          name
        );

  }

  private static void anonymous () {

    final Name name =
      name (
        SERVICE_ONE
      );

    final Context context =
      context (
        environment ()
      );

    assert
      context !=
        context (
          environment ()
        );

    final Service service =
      context.service (
        name
      );

    assert
      service ==
        context.service (
          name
        );

    assert
      service !=
        context (
          environment ()
        ).service (
          name
        );

  }

  private static void identified () {

    final Environment env =
      environment (
        name (
          OPENSIGNALS_SERVICES_CONTEXT_ID
        ),
        SERVICE_1
      );

    final Context context =
      context (
        env
      );

    assert
      context ==
        context (
          env
        );

  }

  private Contexts () {}

  public static void main (
    final String[] args
  ) {

    global ();
    anonymous ();
    identified ();

  }

}