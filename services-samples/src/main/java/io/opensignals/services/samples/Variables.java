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
import static io.opensignals.services.samples.Strings.CHROMOSOMES;
import static io.opensignals.services.samples.Strings.OPENSIGNALS_SERVICES_CONTEXT_ID;

final class Variables {

  private static void of () {

    final Variable< String > string =
      variable (
        name (
          OPENSIGNALS_SERVICES_CONTEXT_ID
        ),
        (String) null
      );

    final Variable< Integer > chromosomes =
      variable (
        name (
          CHROMOSOMES
        ),
        42
      );


    final Environment environment =
      context ()
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

  private Variables () {}

  public static void main (
    final String[] args
  ) {

    of ();

  }

}
