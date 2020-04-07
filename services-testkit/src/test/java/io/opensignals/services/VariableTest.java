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

import java.util.function.BiFunction;

import static io.opensignals.services.Services.*;
import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.RUNNABLE;
import static org.junit.jupiter.api.Assertions.assertSame;

final class VariableTest {

  private static final Name NAME =
    name (
      Variable.class
    );

  private static final Name ALT_NAME =
    name (
      VariableTest.class
    );

  private static final Environment EMPTY =
    environment (
      name -> null
    );

  private static < T > void test (
    final T value,
    final T defVal,
    final BiFunction< ? super Name, T, ? extends Variable< T > > factory
  ) {

    final Variable< T > variable =
      factory.apply (
        NAME,
        defVal
      );

    assertSame (
      value,
      variable.of (
        environment (
          NAME,
          value
        )
      )
    );

    assertSame (
      defVal,
      variable.of (
        EMPTY
      )
    );

  }

  @Test
  void object_of () {

    test (
      new Object (),
      new Object (),
      Services::variable
    );

  }

  @Test
  void string_of () {

    test (
      "value",
      "defValue",
      Services::variable
    );

  }

  @Test
  void float_of () {

    test (
      0.0f,
      0.1f,
      Services::variable
    );

  }

  @Test
  void double_of () {

    test (
      0.0d,
      0.1d,
      Services::variable
    );

  }

  @Test
  void integer_of () {

    test (
      0,
      1000,
      Services::variable
    );

  }

  @Test
  void long_of () {

    test (
      0L,
      1000L,
      Services::variable
    );

  }

  @Test
  void boolean_of () {

    test (
      true,
      false,
      Services::variable
    );

  }

  @Test
  void name_of () {

    test (
      NAME,
      ALT_NAME,
      Services::variable
    );

  }

  @Test
  void enum_of () {

    test (
      BLOCKED,
      RUNNABLE,
      ( name, defVal ) ->
        variable (
          name,
          Thread.State.class,
          defVal
        )
    );

  }

}
