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
import io.opensignals.services.Services.*;
import io.opensignals.services.ext.spi.alpha.Channels.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

final class Contexts {

  private Contexts () {}

  /**
   * The SPI implementation of {@link Services.Context}.
   *
   * @author wlouth
   * @since 1.0
   */

  static final class Context
    implements Services.Context {

    private final Environment environment;

    private final ConcurrentHashMap< Name, Service > services =
      new ConcurrentHashMap<> ();

    private final Channel< Phenomenon > allChannel =
      Channels.memory ();

    private final Channel< Signal > signalsChannel =
      Channels.memory ();

    private final Channel< Status > statusChannel =
      Channels.memory ();


    Context (
      final Environment environment
    ) {

      this.environment =
        environment;

    }


    /*
     * Creates and adds a Service to
     * the name-to-service map data structure.
     */

    private Service serviceOf (
      final Name name
    ) {

      return
        services.computeIfAbsent (
          name,
          this::newService
        );

    }

    private Service newService (
      final Name name
    ) {

      return
        new Service (
          this,
          name
        );

    }


    @Override
    public Environment getEnvironment () {

      return environment;

    }


    @Override
    public Services.Service service (
      final Name name
    ) {

      final Service service =
        services.get (
          name
        );

      return
        service != null
        ? service
        : serviceOf ( name );

    }

    @Override
    public Stream< Services.Service > services () {

      return
        services
          .values ()
          .stream ()
          .map ( Services.Service.class::cast );

    }

    @Override
    public Subscription subscribe (
      final Subscriber< ? super Phenomenon > subscriber
    ) {

      return
        allChannel.subscribe (
          subscriber
        );

    }

    @SuppressWarnings ( {"unchecked", "ChainOfInstanceofChecks"} )
    @Override
    public < T extends Phenomenon > Subscription subscribe (
      final Subscriber< T > subscriber,
      final Class< T > type
    ) {

      if ( type == Signal.class ) {

        return
          signalsChannel.subscribe (
            (Subscriber< Signal >) subscriber
          );

      } else if ( type == Status.class ) {

        return
          statusChannel.subscribe (
            (Subscriber< Status >) subscriber
          );

      } else {

        throw
          new IllegalArgumentException ();

      }

    }


    void dispatch (
      final Name name,
      final Orientation orientation,
      final Signal signal
    ) {

      allChannel.dispatch (
        name,
        orientation,
        signal
      );

      signalsChannel.dispatch (
        name,
        orientation,
        signal
      );

    }


    @SuppressWarnings ( "SameParameterValue" )
    void dispatch (
      final Name name,
      final Orientation orientation,
      final Status status
    ) {

      allChannel.dispatch (
        name,
        orientation,
        status
      );

      statusChannel.dispatch (
        name,
        orientation,
        status
      );

    }


  }
}
