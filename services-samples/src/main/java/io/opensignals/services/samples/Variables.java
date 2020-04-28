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
import io.opensignals.services.Services.Environment;
import io.opensignals.services.Services.Variable;

final class Variables {

  private Variables () {}

  public static void main (
    final String[] args
  ) {

    of ();

  }

  private static void of () {

    final Variable< String > string =
      Services
        .variable (
          Services
            .name (
              "opensignals.services.context.id"
            ),
          (String) null
        );

    final Variable< Integer > chromosomes =
      Services
        .variable (
          Services
            .name (
              "chromosomes"
            ),
          42
        );


    final Environment environment =
      Services
        .context ()
        .getEnvironment ();

    assert
      null !=
        string.of (
          environment
        );

    assert
      42 ==
        chromosomes.of (
          environment
        );

  }

}
