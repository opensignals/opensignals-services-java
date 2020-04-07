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

package io.opensignals.services.ext.spi.alpha;

import io.opensignals.services.Services.Environment;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Status;
import io.opensignals.services.Services.Variable;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.opensignals.services.Services.Status.DOWN;
import static io.opensignals.services.Services.Status.OK;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VariableTest {

  private static final Provider PROVIDER =
    Provider.INSTANCE;

  private static final Names.Name PATH =
    Names.of ( Variable.class );

  private static final Environment EMPTY =
    path -> empty ();

  @Test
  void of_type () {

    final Map< ?, ? > defValue =
      new HashMap<> ();

    final Map< ?, ? > actual =
      new HashMap<> ();

    //noinspection rawtypes
    final Variable< Map > option =
      Variables.of (
        PATH,
        Map.class,
        defValue
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defValue,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defValue,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }


  @Test
  void of_object () {

    final Object defValue =
      new Object ();

    final Object actual =
      new Object ();

    final Variable< Object > option =
      Variables.of (
        PATH,
        defValue
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defValue,
      option.of (
        EMPTY
      )
    );

  }


  @Test
  void of_int () {

    final Integer defVal = 1;
    final Integer actual = 2;

    final Variable< Integer > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_long () {

    final Long defVal = 1L;
    final Long actual = 2L;

    final Variable< Long > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_double () {

    final Double defVal = 1.0D;
    final Double actual = 2.0D;

    final Variable< Double > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_float () {

    final Float defVal = 1.0F;
    final Float actual = 2.0F;

    final Variable< Float > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_string () {

    final String defVal = "1";
    final String actual = "2";

    final Variable< String > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @SuppressWarnings ( "ConstantConditions" )
  @Test
  void of_boolean () {

    final Boolean defVal = true;
    final Boolean actual = false;

    final Variable< Boolean > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

  @Test
  void of_name () {

    final Name defVal =
      PROVIDER.name ( "1" );

    final Name actual =
      PROVIDER.name ( "2" );

    final Variable< Name > option =
      Variables.of (
        PATH,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );
  }

  @Test
  void of_enum () {

    final Status defVal = OK;
    final Status actual = DOWN;

    final Variable< Status > option =
      Variables.of (
        PATH,
        Status.class,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.toString ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );
  }

  @Test
  void of_alt_type () {

    final Long defVal = 1L;
    final Long actual = 2L;

    final Variable< Long > option =
      Variables.of (
        PATH,
        Long.class,
        Number.class,
        Number::longValue,
        defVal
      );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual
        )
      )
    );

    assertEquals (
      actual,
      option.of (
        PROVIDER.environment (
          PATH,
          actual.intValue ()
        )
      )
    );

    assertEquals (
      defVal,
      option.of (
        EMPTY
      )
    );

    assertEquals (
      defVal,
      option.of (
        PROVIDER.environment (
          PATH,
          new Object ()
        )
      )
    );

  }

}