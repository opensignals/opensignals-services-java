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
import io.opensignals.services.Services.Environment;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Service;

import java.util.Optional;

final class Contexts {

  public static void main (
    final String[] args
  ) {

    global ();
    anonymous ();
    identified ();

  }

  private static void global () {

    final Name name =
      Services.name (
        "service.one"
      );

    final Context context =
      Services.context ();

    assert
      context ==
        Services.context ();

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
      Services.name (
        "service.one"
      );

    final Environment env =
      Services.environment (
        n ->
          Optional.empty ()
      );

    final Context context =
      Services.context (
        env
      );

    assert
      context !=
        Services.context (
          env
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

  }

  private static void identified () {

    final Environment env =
      Services.environment (
        Services.name (
          "opensignals.services.context.id"
        ),
        "service.1"
      );

    final Context context =
      Services.context (
        env
      );

    assert
      context ==
        Services.context (
          env
        );

  }

}