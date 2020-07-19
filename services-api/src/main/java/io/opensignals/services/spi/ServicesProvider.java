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

package io.opensignals.services.spi;

import io.opensignals.services.Services;
import io.opensignals.services.Services.*;

import java.lang.reflect.Member;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

/**
 * The service provider interface for the services signaling runtime.
 * <p>
 * Note: An SPI implementation of this interface is free to override
 * the default methods implementation included here.
 *
 * @author wlouth
 * @since 1.0
 */

public interface ServicesProvider {

  /**
   * A method called once on initialization of the services
   * runtime and before any other method in this interface.
   */

  @SuppressWarnings ( "EmptyMethod" )
  default void init () {}


  /**
   * @see Services#context()
   */

  Context context ();


  /**
   * @see Services#context(Environment)
   */

  Context context ( Environment environment );


  /**
   * @see Services#name(String)
   */

  Name name ( String path );


  /**
   * @see Services#name(String, String)
   */

  default Name name (
    final String first,
    final String second
  ) {

    return
      name ( first )
        .name ( second );

  }


  /**
   * @see Services#name(String, String, String)
   */

  default Name name (
    final String first,
    final String second,
    final String third
  ) {

    return
      name ( first )
        .name ( second )
        .name ( third );

  }

  /**
   * @see Services#name(String, String)
   */

  default Name name (
    final Class< ? > cls,
    final String path
  ) {

    return
      name ( cls )
        .name ( path );

  }


  /**
   * @see Services#name(Class)
   */

  default Name name (
    final Class< ? > cls
  ) {

    return
      name (
        cls.getName ()
      );

  }


  /**
   * @see Services#name(Member)
   */

  default Name name (
    final Member member
  ) {

    return
      name (
        member.getDeclaringClass ().getName ()
      ).name (
        member.getName ()
      );

  }


  /**
   * @see Services#name(Iterable)
   */

  default Name name (
    final Iterable< String > it
  ) {

    return
      name (
        it,
        identity ()
      );

  }


  /**
   * @see Services#name(Iterable, Function)
   */

  default < T > Name name (
    final Iterable< ? extends T > it,
    final Function< T, String > fn
  ) {

    Name name = null;

    for ( final T value : it ) {

      final String path =
        fn.apply (
          value
        );

      name =
        ( name != null )
        ? name.name ( path )
        : name ( path );

    }

    if ( name != null )
      return name;
    else
      throw new IllegalArgumentException ();

  }


  /**
   * @see Services#execute(Service, Fn)
   */

  default < R, T extends Throwable > R execute (
    final Service service,
    final Fn< R, T > fn
  ) throws T {

    service.start ();

    boolean success =
      true;

    try {

      return
        fn.apply ();

    } catch (
      final Throwable error
    ) {

      success =
        false;

      service.fail ();

      throw error;

    } finally {

      if ( success ) {

        service.succeed ();

      }

      service.stop ();

    }

  }


  /**
   * @see Services#execute(Service, Op)
   */

  default < T extends Throwable > void execute (
    final Service service,
    final Op< T > op
  ) throws T {

    service.start ();

    boolean success =
      true;

    try {

      op.apply ();

    } catch (
      final Throwable error
    ) {

      success =
        false;

      service.fail ();

      throw error;

    } finally {

      if ( success ) {
        service.succeed ();
      }

      service.stop ();

    }

  }


  /**
   * @see Services#call(Service, Fn)
   */

  default < R, T extends Throwable > R call (
    final Service service,
    final Fn< R, T > fn
  ) throws T {

    service.call ();

    boolean success =
      true;

    try {

      return
        fn.apply ();

    } catch (
      final Throwable error
    ) {

      success =
        false;

      service.failed ();

      throw error;

    } finally {

      if ( success ) {
        service.succeeded ();
      }

    }

  }


  /**
   * @see Services#call(Service, Op)
   */

  default < T extends Throwable > void call (
    final Service service,
    final Op< T > op
  ) throws T {

    service.call ();

    boolean success =
      true;

    try {

      op.apply ();

    } catch (
      final Throwable error
    ) {

      success =
        false;

      service.failed ();

      throw error;

    } finally {

      if ( success ) {
        service.succeeded ();
      }

    }

  }


  /**
   * @see Services#service(Service, Callback)
   */

  Service service (
    Service service,
    Callback< ? super Signal > callback
  );

  /**
   * @see Services#environment()
   */

  default Environment environment () {

    return
      environment (
        n ->
          empty ()
      );

  }


  /**
   * @see Services#environment(Function)
   */

  default < T > Environment environment (
    final Function< ? super Name, T > mapper
  ) {

    return
      name -> {
        try {

          return
            ofNullable (
              mapper.apply (
                name
              )
            );

        } catch (
          final Throwable error
        ) {

          return
            empty ();

        }
      };
  }


  /**
   * @see Services#environment(Name, Object)
   */

  default < T > Environment environment (
    final Name path,
    final T value
  ) {

    requireNonNull (
      path
    );

    return
      environment (
        name ->
          path == name
          ? value
          : null
      );

  }

  /**
   * @see Services#environment(Name, int)
   */

  default Environment environment (
    final Name path,
    final int value
  ) {

    return
      environment (
        path,
        Integer.valueOf (
          value
        )
      );

  }

  /**
   * @see Services#environment(Name, long)
   */

  default Environment environment (
    final Name path,
    final long value
  ) {

    return
      environment (
        path,
        Long.valueOf (
          value
        )
      );

  }

  /**
   * @see Services#environment(Name, float)
   */

  default Environment environment (
    final Name path,
    final float value
  ) {

    return
      environment (
        path,
        Float.valueOf (
          value
        )
      );

  }


  /**
   * @see Services#environment(Name, double)
   */

  default Environment environment (
    final Name path,
    final double value
  ) {

    return
      environment (
        path,
        Double.valueOf (
          value
        )
      );

  }

  /**
   * @see Services#environment(Name, boolean)
   */

  default Environment environment (
    final Name path,
    final boolean value
  ) {

    return
      environment (
        path,
        Boolean.valueOf (
          value
        )
      );

  }

  /**
   * @see Services#environment(Name, Supplier)
   */

  default < T > Environment environment (
    final Name path,
    final Supplier< T > supplier
  ) {

    requireNonNull (
      path
    );

    return
      environment (
        name ->
          path == name
          ? supplier.get ()
          : null
      );

  }


  /**
   * @see Services#environment(Predicate, Function)
   */

  default < T > Environment environment (
    final Predicate< ? super Name > predicate,
    final Function< ? super Name, T > mapper
  ) {

    return
      environment (
        name ->
          predicate.test ( name )
          ? mapper.apply ( name )
          : null
      );

  }


  /**
   * @see Services#variable(Name, Object)
   */

  default Variable< Object > variable (
    final Name name,
    final Object defValue
  ) {

    return
      environment ->
        environment
          .getObject (
            name,
            defValue
          );

  }


  /**
   * @see Services#variable(Name, Class, Object)
   */

  default < T > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final T defValue
  ) {

    return
      environment ->
        environment.< T > getType ( name, type )
          .orElse ( defValue );


  }


  /**
   * @see Services#variable(Name, Class, Class, Function, Object)
   */

  default < T, A > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defValue
  ) {

    return
      environment ->
        environment.getType (
          name,
          type,
          alt,
          mapper
        ).orElse (
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Class, Enum)
   */

  default < T extends Enum< T > > Variable< T > variable (
    final Name name,
    final Class< T > type,
    final T defValue
  ) {

    return
      environment ->
        environment.getEnum (
          name,
          type,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Boolean)
   */

  default Variable< Boolean > variable (
    final Name name,
    final Boolean defValue
  ) {

    return
      environment ->
        environment.getBoolean (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Integer)
   */

  default Variable< Integer > variable (
    final Name name,
    final Integer defValue
  ) {

    return
      environment ->
        environment.getInteger (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Long)
   */

  default Variable< Long > variable (
    final Name name,
    final Long defValue
  ) {

    return
      env ->
        env.getLong (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Double)
   */

  default Variable< Double > variable (
    final Name name,
    final Double defValue
  ) {

    return
      environment ->
        environment.getDouble (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Float)
   */

  default Variable< Float > variable (
    final Name name,
    final Float defValue
  ) {

    return
      environment ->
        environment.getFloat (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, String)
   */

  default Variable< String > variable (
    final Name name,
    final String defValue
  ) {

    return
      environment ->
        environment.getString (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, CharSequence)
   */

  default Variable< CharSequence > variable (
    final Name name,
    final CharSequence defValue
  ) {

    return
      environment ->
        environment.getCharSequence (
          name,
          defValue
        );

  }


  /**
   * @see Services#variable(Name, Name)
   */

  default Variable< Name > variable (
    final Name name,
    final Name defValue
  ) {

    return
      environment ->
        environment.getName (
          name,
          defValue
        );

  }

}