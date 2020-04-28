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
import io.opensignals.services.Services.Name;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

final class Environments {

  private Environments () {}

  public static void main (
    final String[] args
  ) {

    mapping ();
    chaining ();
    constructing ();
    transforming ();

  }

  private static void mapping () {

    final HashMap< Name, Object > map =
      new HashMap<> ();

    final Name string =
      Services.name (
        "string"
      );

    final Name integer =
      Services.name (
        "integer"
      );

    map.put (
      string,
      "string"
    );

    map.put (
      integer,
      42
    );

    final Environment env =
      Services.environment (
        map::get
      );

    assert
      env.getObject (
        string
      ).filter (
        "string"::equals
      ).isPresent ();

    assert
      !env.getBoolean (
        string
      ).filter (
        Boolean.TRUE::equals
      ).isPresent ();

    assert
      42 ==
        env.getInteger (
          integer,
          0
        );

    assert
      !env.getString (
        integer
      ).filter (
        "42"::equals
      ).isPresent ();

  }

  private static void chaining () {

    final Name name =
      Services.name (
        "name"
      );

    final Environment env =
      Services.environment (
        name,
        "parent"
      );

    assert
      "parent".equals (
        env.getString (
          name,
          null
        )
      );

    assert
      "child".equals (
        env.environment (
          name,
          "child"
        ).getString (
          name, null
        )
      );

    assert
      "parent".equals (
        env.environment (
          n ->
            Optional.empty ()
        ).getString (
          name,
          null
        )
      );

  }

  private static void constructing () {

    final Environment env =
      Services.environment (
        Services.name (
          "one"
        ),
        1
      ).environment (
        Services.name (
          "two"
        ),
        2
      );

    assert
      1 ==
        env.getInteger (
          Services.name (
            "one"
          ),
          0
        );

    assert
      2 ==
        env.getInteger (
          Services.name (
            "two"
          ),
          0
        );

  }

  private static void transforming () {

    final Properties props =
      new Properties ();

    props.setProperty (
      "name.one",
      "1"
    );

    final Environment env =
      Services.environment (
        n ->
          props.getProperty (
            n.toString ()
          )
      );

    assert
      1 ==
        env.getInteger (
          Services.name (
            "name"
          ).name (
            "one"
          ),
          0
        );

  }

}
