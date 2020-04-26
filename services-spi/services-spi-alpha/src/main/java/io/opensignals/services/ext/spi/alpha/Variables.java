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

import io.opensignals.services.Services;
import io.opensignals.services.Services.Environment;
import io.opensignals.services.Services.Name;

import java.util.function.BiFunction;
import java.util.function.Function;

final class Variables {

  @FunctionalInterface
  private interface Decoder< T > {

    T decode ( String string );

  }

  private static < T > T decode (
    final String string,
    final Decoder< T > decoder,
    final T defValue
  ) {

    try {

      return
        decoder.decode (
          string
        );

    } catch (
      final Exception e
    ) {

      return defValue;

    }


  }


  private static < T, A > T toAltType (
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final Object value,
    final T defVal
  ) {

    if ( type.isInstance ( value ) ) {

      return
        type.cast ( value );

    } else if ( alt.isInstance ( value ) ) {

      return
        mapper.apply (
          alt.cast (
            value
          )
        );

    } else {

      return
        defVal;

    }

  }

  private static < T extends Enum< T > > T toEnum (
    final Class< T > type,
    final Object value,
    final T defVal
  ) {

    if ( type.isInstance ( value ) ) {

      return
        type.cast (
          value
        );

    } else if ( value instanceof String ) {

      return
        Enum.valueOf (
          type,
          (String) value
        );

    } else {

      return
        defVal;

    }

  }

  private static Boolean toBoolean (
    final Object value,
    final Boolean defVal
  ) {

    if ( value instanceof Boolean ) {

      return
        (Boolean) value;

    } else if ( value instanceof String ) {

      return
        decode (
          (String) value,
          Boolean::parseBoolean,
          defVal
        );

    } else {

      return
        defVal;

    }

  }

  private static Integer toInteger (
    final Object value,
    final Integer defVal
  ) {

    if ( value instanceof Integer ) {

      return
        (Integer) value;

    } else if ( value instanceof Number ) {

      return
        ( (Number) value )
          .intValue ();

    } else if ( value instanceof String ) {

      return
        decode (
          (String) value,
          Integer::parseInt,
          defVal
        );

    } else {

      return
        defVal;

    }

  }

  private static Long toLong (
    final Object value,
    final Long defVal
  ) {

    if ( value instanceof Long ) {

      return
        (Long) value;

    } else if ( value instanceof Number ) {

      return
        ( (Number) value )
          .longValue ();

    } else if ( value instanceof String ) {

      return
        decode (
          (String) value,
          Long::parseLong,
          defVal
        );

    } else {

      return
        defVal;

    }

  }

  private static Double toDouble (
    final Object value,
    final Double defVal
  ) {

    if ( value instanceof Double ) {

      return
        (Double) value;

    } else if ( value instanceof Number ) {

      return
        ( (Number) value )
          .doubleValue ();

    } else if ( value instanceof String ) {

      return
        decode (
          (String) value,
          Double::parseDouble,
          defVal
        );

    } else {

      return
        defVal;

    }

  }

  private static Float toFloat (
    final Object value,
    final Float defVal
  ) {

    if ( value instanceof Float ) {

      return
        (Float) value;

    } else if ( value instanceof Number ) {

      return
        ( (Number) value )
          .floatValue ();

    } else if ( value instanceof String ) {

      return
        decode (
          (String) value,
          Float::parseFloat,
          defVal
        );

    } else {

      return
        defVal;

    }

  }

  private static Object toObject (
    final Object value,
    final Object defVal
  ) {

    return
      value != null
      ? value
      : defVal;

  }

  private static String toString (
    final Object value,
    final String defVal
  ) {

    return
      value instanceof String
      ? (String) value
      : defVal;

  }

  private static Name toName (
    final Object value,
    final Name defVal
  ) {

    if ( value instanceof Name ) {

      return
        (Name) value;

    } else if ( value instanceof String ) {

      return
        Names.of (
          (String) value
        );

    } else {

      return
        defVal;

    }

  }

  private Variables () {}

  /**
   * Creates a {@link Services.Variable} of type {@code Object}.
   *
   * @param name   the name of the configuration item
   * @param defVal the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Object}
   */

  static Services.Variable< Object > of (
    final Name name,
    final Object defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toObject
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code T}.
   *
   * @see Environment#getType(Name, Class, Object)
   */

  static < T > Services.Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          type.isInstance ( val )
          ? type.cast ( val )
          : def
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code T}.
   *
   * @see Environment#getType(Name, Class, Class, Function)
   */

  static < T, A > Services.Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          toAltType (
            type,
            alt,
            mapper,
            val,
            def
          )
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Enum}.
   *
   * @see Environment#getEnum(Name, Class, Enum)
   */

  static < T extends Enum< T > > Services.Variable< T > of (
    final Name name,
    final Class< T > type,
    final T defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        ( val, def ) ->
          toEnum (
            type,
            val,
            def
          )
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Boolean}.
   *
   * @see Environment#getBoolean(Name, boolean)
   */

  static Services.Variable< Boolean > of (
    final Name name,
    final Boolean defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toBoolean
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Integer}.
   *
   * @see Environment#getInteger(Name, int)
   */

  static Services.Variable< Integer > of (
    final Name name,
    final Integer defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toInteger
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Long}.
   *
   * @see Environment#getLong(Name, long)
   */

  static Services.Variable< Long > of (
    final Name name,
    final Long defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toLong
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Double}.
   *
   * @see Environment#getDouble(Name, double)
   */

  static Services.Variable< Double > of (
    final Name name,
    final Double defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toDouble
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code String}.
   *
   * @see Environment#getString(Name, String)
   */

  static Services.Variable< String > of (
    final Name name,
    final String defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toString
      );


  }

  /**
   * Creates a {@link Services.Variable} of type {@code Float}.
   *
   * @see Environment#getFloat(Name, float)
   */

  static Services.Variable< Float > of (
    final Name name,
    final Float defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toFloat
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code Name}.
   *
   * @see Environment#getName(Name, Name)
   */

  static Services.Variable< Name > of (
    final Name name,
    final Name defVal
  ) {

    return
      new Variable<> (
        name,
        defVal,
        Variables::toName
      );

  }

  /**
   * Creates a {@link Services.Variable} of type {@code CharSequence}.
   *
   * @see Environment#getCharSequence(Name, CharSequence)
   */

  static Services.Variable< CharSequence > of (
    final Name name,
    final CharSequence defVal
  ) {

    return
      environment ->
        environment.getCharSequence (
          name,
          defVal
        );

  }

  private static final class Variable< T >
    implements Services.Variable< T > {

    private final Name                                         name;
    private final T                                            defVal;
    private final BiFunction< Object, ? super T, ? extends T > mapper;

    protected Variable (
      final Name name,
      final T defVal,
      final BiFunction< Object, ? super T, ? extends T > mapper
    ) {

      this.name =
        name;

      this.defVal =
        defVal;

      this.mapper =
        mapper;

    }

    public T of (
      final Environment environment
    ) {

      final T fallback =
        defVal;

      final Object value =
        environment.getObject (
          name,
          fallback
        );


      return
        value == fallback
        ? fallback
        : mapper.apply ( value, fallback );

    }

  }

}
