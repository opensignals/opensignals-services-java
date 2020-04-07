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
import io.opensignals.services.Services.Variable;

import java.util.function.Function;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

final class Variables {

  private Variables () {}

  /**
   * Creates a {@link Variable} of type {@code Object}.
   *
   * @param name   the name of the configuration item
   * @param defVal the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Object}
   */

  static Variable< Object > of (
    final Name name,
    final Object defVal
  ) {

    return
      environment ->
        environment.getObject (
          name,
          defVal
        );

  }


  /**
   * Creates a {@link Variable} of type {@code T}.
   *
   * @see Environment#getType(Name, Class, Object)
   */

  static < T > Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final T defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if (
          obj != defVal &&
            type.isInstance ( obj )
        ) {

          return
            type.cast ( obj );

        }

        return defVal;

      };

  }


  /**
   * Creates a {@link Variable} of type {@code T}.
   *
   * @see Environment#getType(Name, Class, Class, Function)
   */

  static < T, A > Variable< T > of (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( type.isInstance ( obj ) ) {

            return
              type.cast ( obj );

          } else if ( alt.isInstance ( obj ) ) {

            return
              mapper.apply (
                alt.cast (
                  obj
                )
              );

          }

        }

        return defVal;

      };


  }


  /**
   * Creates a {@link Variable} of type {@code Enum}.
   *
   * @see Environment#getEnum(Name, Class, Enum)
   */

  static < T extends Enum< T > > Variable< T > of (
    final Name name,
    final Class< T > type,
    final T defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( type.isInstance ( obj ) ) {

            return
              type.cast ( obj );

          } else if ( obj instanceof String ) {

            return
              Enum.valueOf (
                type,
                (String) obj
              );

          }

        }

        return defVal;

      };


  }


  /**
   * Creates a {@link Variable} of type {@code Boolean}.
   *
   * @see Environment#getBoolean(Name, boolean)
   */

  @SuppressWarnings ( "ChainOfInstanceofChecks" )
  static Variable< Boolean > of (
    final Name name,
    final Boolean defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( obj instanceof Boolean ) {

            return
              (Boolean) obj;

          } else if ( obj instanceof String ) {

            return
              parseBoolean (
                (String) obj
              );

          }

        }

        return defVal;

      };

  }

  /**
   * Creates a {@link Variable} of type {@code Integer}.
   *
   * @see Environment#getInteger(Name, int)
   */

  @SuppressWarnings ( "ChainOfInstanceofChecks" )
  static Variable< Integer > of (
    final Name name,
    final Integer defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( obj instanceof Integer ) {

            return
              (Integer) obj;

          } else if ( obj instanceof Number ) {

            return
              ( (Number) obj )
                .intValue ();

          } else if ( obj instanceof String ) {

            return
              parseInt (
                (String) obj
              );

          }

        }

        return defVal;

      };

  }


  /**
   * Creates a {@link Variable} of type {@code Long}.
   *
   * @see Environment#getLong(Name, long)
   */

  @SuppressWarnings ( "ChainOfInstanceofChecks" )
  static Variable< Long > of (
    final Name name,
    final Long defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( obj instanceof Long ) {

            return
              (Long) obj;

          } else if ( obj instanceof Number ) {

            return
              ( (Number) obj )
                .longValue ();

          } else if ( obj instanceof String ) {

            return
              parseLong (
                (String) obj
              );

          }

        }

        return defVal;

      };

  }


  /**
   * Creates a {@link Variable} of type {@code Double}.
   *
   * @see Environment#getDouble(Name, double)
   */

  @SuppressWarnings ( "ChainOfInstanceofChecks" )
  static Variable< Double > of (
    final Name name,
    final Double defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( obj instanceof Double ) {

            return
              (Double) obj;

          } else if ( obj instanceof Number ) {

            return
              ( (Number) obj )
                .doubleValue ();

          } else if ( obj instanceof String ) {

            return
              parseDouble (
                (String) obj
              );

          }

        }

        return defVal;

      };

  }


  /**
   * Creates a {@link Variable} of type {@code String}.
   *
   * @see Environment#getString(Name, String)
   */

  static Variable< String > of (
    final Name name,
    final String defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        return
          obj != defVal &&
            obj instanceof String
          ? (String) obj
          : defVal;

      };


  }


  /**
   * Creates a {@link Variable} of type {@code Float}.
   *
   * @see Environment#getFloat(Name, float)
   */

  @SuppressWarnings ( "ChainOfInstanceofChecks" )
  static Variable< Float > of (
    final Name name,
    final Float defVal
  ) {

    return
      environment -> {

        final Object obj =
          environment.getObject (
            name,
            defVal
          );

        if ( obj != defVal ) {

          if ( obj instanceof Float ) {

            return
              (Float) obj;

          } else if ( obj instanceof Number ) {

            return
              ( (Number) obj )
                .floatValue ();

          } else if ( obj instanceof String ) {

            return
              parseFloat (
                (String) obj
              );

          }

        }

        return defVal;

      };

  }


  /**
   * Creates a {@link Variable} of type {@code Name}.
   *
   * @see Environment#getName(Name, Name)
   */

  static Variable< Name > of (
    final Name name,
    final Name defVal
  ) {

    return
      environment ->
        environment.getName (
          name,
          defVal
        );

  }

  /**
   * Creates a {@link Variable} of type {@code CharSequence}.
   *
   * @see Environment#getCharSequence(Name, CharSequence)
   */

  static Variable< CharSequence > of (
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

}
