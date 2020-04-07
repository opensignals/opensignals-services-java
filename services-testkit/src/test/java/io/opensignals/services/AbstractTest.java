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

package io.opensignals.services;

import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Service;
import org.junit.jupiter.api.BeforeEach;

import static io.opensignals.services.Services.context;
import static io.opensignals.services.Services.name;

/**
 * An abstract class used for sharing test setup across individual interface test classes.
 *
 * @author wlouth
 * @since 1.0
 */

abstract class AbstractTest {

  static final Name S1_NAME = name ( "service.1" );
  static final Name S2_NAME = name ( "service.2" );

  Context context;
  Service s1;
  Service s2;

  @BeforeEach
  final void setup () {

    context =
      context ();

    s1 =
      context.service (
        S1_NAME
      );


    s2 =
      context.service (
        S2_NAME
      );

  }

}