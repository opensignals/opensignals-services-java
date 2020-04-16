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
import io.opensignals.services.spi.ServicesProvider;

import java.lang.reflect.Member;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.opensignals.services.Services.*;
import static io.opensignals.services.ext.spi.alpha.Names.root;
import static java.util.Objects.requireNonNull;

/**
 * The SPI implementation of {@link ServicesProvider}.
 *
 * @author wlouth
 * @since 1.0
 */

final class Provider
  implements ServicesProvider {

  static final Provider INSTANCE = new Provider ();

  private static final Names.Name CONTEXT_ID =
    root ( Strings.OPENSIGNALS )
      .node ( Strings.SERVICES )
      .node ( Strings.CONTEXT )
      .node ( Strings.ID );

  private static final Environment DEFAULTS =
    Environments.cache (
      Environments.chain (
        Environments.single (
          CONTEXT_ID,
          UUID.randomUUID ().toString ()
        ),
        Environments.map (
          path ->
            System.getProperty (
              path.toString ()
            )
        )
      )
    );

  private static final Variable< String > ID =
    Variables.of (
      CONTEXT_ID,
      (String) null
    );

  // this can be expanded later to include
  // multiple property options not just id

  private final ConcurrentHashMap< String, Context > contexts =
    new ConcurrentHashMap<> ();

  private Provider () { }

  @SuppressWarnings ( "RedundantMethodOverride" )
  @Override
  public void init () {

    // @todo add process inspection code
    // to capture values for configuration

  }


  @Override
  public Context context () {

    return
      context (
        DEFAULTS
      );

  }


  @Override
  public Context context (
    final Environment environment
  ) {

    final String id =
      ID.of (
        environment
      );

    if ( id == null ) {

      return
        newContext (
          environment
        );

    } else {

      final Context context =
        contexts.get (
          id
        );

      return
        context == null ?
        contexts.computeIfAbsent (
          id,
          key ->
            newContext (
              DEFAULTS.environment (
                environment
              )
            )
        ) :
        context;

    }

  }

  private static Context newContext (
    final Environment environment
  ) {

    return
      Plugins.apply (
        new Contexts.Context (
          environment
        )
      );

  }


  @Override
  public Name name (
    final String path
  ) {

    return
      Names.of (
        requireNonNull (
          path
        )
      );

  }


  @Override
  public Name name (
    final Class< ? > cls
  ) {

    return
      Names.of (
        requireNonNull (
          cls
        )
      );

  }


  @Override
  public Name name (
    final Member member
  ) {

    return
      Names.of (
        requireNonNull (
          member
        )
      );

  }


  @Override
  public Services.Service service (
    final Services.Service service,
    final Callback< ? super Signal > callback
  ) {

    return
      new CallbackService (
        service,
        callback
      );

  }


  @Override
  public < T > Environment environment (
    final Function< ? super Name, T > mapper
  ) {

    return
      Environments.cache (
        Environments.map (
          mapper
        )
      );

  }


  @Override
  public < T > Environment environment (
    final Name path,
    final T value
  ) {

    return
      Environments.single (
        path,
        value
      );

  }

  @Override
  public < T > Environment environment (
    final Predicate< ? super Name > predicate,
    final Function< ? super Name, T > mapper
  ) {

    return
      Environments.guard (
        predicate,
        mapper
      );

  }

  @Override
  public Variable< Object > variable (
    final Name name,
    final Object defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public < T > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        defValue
      );

  }

  @Override
  public < T, A > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        alt,
        mapper,
        defValue
      );

  }

  @SuppressWarnings ( "BoundedWildcard" )
  @Override
  public < T extends Enum< T > > Variable< T > variable (
    final Name name,
    final Class< T > type,
    final T defValue
  ) {

    return
      Variables.of (
        name,
        type,
        defValue
      );

  }

  @Override
  public Variable< Boolean > variable (
    final Name name,
    final Boolean defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Integer > variable (
    final Name name,
    final Integer defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Long > variable (
    final Name name,
    final Long defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Double > variable (
    final Name name,
    final Double defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Float > variable (
    final Name name,
    final Float defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< String > variable (
    final Name name,
    final String defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< Name > variable (
    final Name name,
    final Name defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

  @Override
  public Variable< CharSequence > variable (
    final Name name,
    final CharSequence defValue
  ) {

    return
      Variables.of (
        name,
        defValue
      );

  }

}
