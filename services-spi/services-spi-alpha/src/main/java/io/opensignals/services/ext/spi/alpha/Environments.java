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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * A utility class for constructing various different
 * implementations of the {@link Environment} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class Environments {

  private Environments () {}

  @SuppressWarnings ( "SameReturnValue" )
  static Environment empty () {

    return
      Empty.INSTANCE;

  }

  @SuppressWarnings ( "WeakerAccess" )
  static Object callWithDefault (
    final Function< ? super Name, ? > function,
    final Name name,
    final Object defValue
  ) {

    final Object result =
      function.apply ( name );

    return
      result != null ?
      result :
      defValue;

  }


  static Environment cache (
    final Environment environment
  ) {

    return
      new Cached (
        environment
      );

  }


  static Environment map (
    final Function< ? super Name, ? > function
  ) {

    return
      new Adaptor (
        function
      );

  }


  static Environment single (
    final Name name,
    final Object value
  ) {

    return
      new Single (
        name,
        value
      );

  }


  static Environment remap (
    final Environment environment,
    final Function< ? super Name, ? extends Name > mapper
  ) {

    return
      new Remap (
        environment,
        mapper
      );

  }

  static < T > Environment guard (
    final Predicate< ? super Name > predicate,
    final Function< ? super Name, T > mapper
  ) {

    return
      new Guarded (
        predicate,
        mapper
      );

  }


  @SuppressWarnings ( "WeakerAccess" )
  static Environment chain (
    final Environment fallback,
    final Environment primary
  ) {

    return
      new Chained (
        fallback,
        primary
      );

  }


  private abstract static class Abstract
    implements Environment {

    @Override
    public < T > Environment environment (
      final Name name,
      final T value
    ) {

      return
        environment (
          environment (
            name,
            () -> value
          )
        );

    }


    @Override
    public < T > Environment environment (
      final Name name,
      final Supplier< T > supplier
    ) {

      return
        environment (
          map (
            path ->
              path == name
              ? supplier.get ()
              : null
          )
        );

    }

    @Override
    public Environment environment (
      final Environment environment
    ) {

      return
        chain (
          this,
          environment
        );

    }

  }


  private static final class Cached
    extends Abstract {

    private static final Object NONE =
      new Object ();

    private final Environment delegate;

    private final Map< Name, Object > cache =
      new ConcurrentHashMap<> ();

    Cached (
      final Environment delegate
    ) {

      this.delegate =
        delegate;

    }


    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      final Object result =
        lookup (
          name
        );

      return
        result != NONE ?
        Optional.of ( result ) :
        Optional.empty ();

    }


    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      final Object result =
        lookup (
          name
        );

      return
        result != NONE ?
        result :
        defValue;

    }

    private Object lookup (
      final Name name
    ) {

      final Object result =
        cache.get (
          name
        );

      return
        result != null ?
        result :
        source ( name );

    }


    private Object source (
      final Name name
    ) {

      return
        cache
          .computeIfAbsent (
            name,
            key ->
              delegate
                .getObject ( key )
                .orElse ( NONE )
          );

    }

  }


  private static final class Single
    extends Abstract {

    private final Name   name;
    private final Object value;

    Single (
      final Name name,
      final Object value
    ) {

      this.name =
        name;

      this.value =
        value;

    }


    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      return
        this.name == name
        ? Optional.of ( value )
        : Optional.empty ();

    }


    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      return
        this.name == name
        ? value
        : defValue;

    }

  }


  private static final class Adaptor
    extends Abstract {

    final Function< ? super Name, ? > function;

    Adaptor (
      final Function< ? super Name, ? > function
    ) {

      this.function =
        function;

    }

    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      return
        ofNullable (
          function
            .apply (
              name
            )
        );

    }

    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      return
        callWithDefault (
          function,
          name,
          defValue
        );

    }

  }


  private static final class Remap
    extends Abstract {

    private final Environment delegate;

    private final Function< ? super Name, ? extends Name > function;

    Remap (
      final Environment delegate,
      final Function< ? super Name, ? extends Name > mapper
    ) {

      this.delegate =
        delegate;

      function =
        mapper;

    }


    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      return
        delegate
          .getObject (
            function.apply (
              name
            )
          );

    }

  }

  private static final class Guarded
    extends Abstract {

    final Predicate< ? super Name >   predicate;
    final Function< ? super Name, ? > function;

    Guarded (
      final Predicate< ? super Name > predicate,
      final Function< ? super Name, ? > function
    ) {

      this.predicate =
        predicate;

      this.function =
        function;

    }

    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      return
        predicate.test ( name )
        ? ofNullable ( function.apply ( name ) )
        : Optional.empty ();

    }

    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      return
        predicate.test ( name )
        ? callWithDefault ( function, name, defValue )
        : defValue;

    }

  }


  private static final class Chained
    extends Abstract {

    final Environment fallback;
    final Environment primary;

    Chained (
      final Environment fallback,
      final Environment primary
    ) {

      this.fallback =
        fallback;

      this.primary =
        primary;

    }


    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      final Optional< Object > result =
        primary
          .getObject (
            name
          );

      return
        result.isPresent ()
        ? result
        : fallback.getObject ( name );

    }


    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      final Object result =
        primary.getObject (
          name,
          null
        );

      return
        result != null
        ? result
        : fallback.getObject ( name, defValue );

    }

  }

  private static final class Empty
    extends Abstract {

    static final Environment INSTANCE = new Empty ();

    @Override
    public Optional< Object > getObject (
      final Name name
    ) {

      return
        Optional.empty ();

    }

    @Override
    public Object getObject (
      final Name name,
      final Object defValue
    ) {

      return
        defValue;

    }

  }

}
