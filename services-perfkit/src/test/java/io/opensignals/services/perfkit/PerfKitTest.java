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

package io.opensignals.services.perfkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.opensignals.services.perfkit.PerfKitLauncher.execute;

@TestMethodOrder (
  OrderAnnotation.class
)
final class PerfKitTest {

  private static final String FACTORY = "io.opensignals.services.ext.spi.alpha.ProviderFactory";
  private static final String ALPHA   = "alpha";

  @Test
  @Order ( 1 )
  void alpha_one () {

    execute (
      FACTORY,
      ALPHA,
      250.0,
      1,
      Assertions::fail
    );

  }

  @Test
  @Order ( 2 )
  void alpha_two () {

    execute (
      FACTORY,
      ALPHA,
      500.0,
      2,
      Assertions::fail
    );

  }

  @Test
  @Order ( 4 )
  void alpha_four () {

    execute (
      FACTORY,
      ALPHA,
      1000.0,
      4,
      Assertions::fail
    );

  }

}
