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

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static io.opensignals.services.Services.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * The test class for the {@link Environment} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class EnvironmentTest {

  private static final Name  FLOAT_PATH     = name ( "float.1" );
  private static final Name  FLOAT_ALT_PATH = name ( "float.2" );
  private static final float FLOAT_VALUE    = 1.0F;
  private static final float FLOAT_DEFAULT  = 2.0F;

  private static void get_float (
    final Environment environment
  ) {

    assertEquals (
      FLOAT_VALUE,
      environment.getFloat (
        FLOAT_PATH,
        FLOAT_DEFAULT
      )
    );

    assertEquals (
      FLOAT_DEFAULT,
      environment.getFloat (
        FLOAT_ALT_PATH,
        FLOAT_DEFAULT
      )
    );

  }


  @Test
  @SuppressWarnings ( "squid:S2699" )
  void environment_function () {

    final HashMap< Name, Float > map =
      new HashMap<> ();

    map.put (
      FLOAT_PATH,
      FLOAT_VALUE
    );

    get_float (
      environment (
        map::get
      )
    );

  }

  @Test
  @SuppressWarnings ( "squid:S2699" )
  void environment_string_object () {

    get_float (
      environment (
        FLOAT_PATH,
        FLOAT_VALUE
      )
    );

  }

  @Test
  @SuppressWarnings ( "squid:S2699" )
  void environment_predicate_function () {

    get_float (
      environment (
        FLOAT_PATH::equals,
        s -> FLOAT_VALUE
      )
    );

  }


  @Test
  void services_context_anonymous () {

    assertNotSame (
      context (),
      context ()
    );

  }

}
