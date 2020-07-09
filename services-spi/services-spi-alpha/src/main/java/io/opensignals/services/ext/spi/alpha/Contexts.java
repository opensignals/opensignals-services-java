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
import io.opensignals.services.ext.spi.alpha.ScoreCards.Scoring;
import io.opensignals.services.ext.spi.alpha.Sinks.Sink;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author wlouth
 * @since 1.0
 */

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

    private final ConcurrentHashMap< Names.Name, Service > services =
      new ConcurrentHashMap<> ( 1009 );

    private final Channel< Phenomenon > allChannel =
      Channels.memory ();

    private final Channel< Signal > signalsChannel =
      Channels.memory ();

    private final Channel< Status > statusChannel =
      Channels.memory ();

    private final Sink< Signal > signalsSink =
      signalsChannel.andThen (
        allChannel
      );

    private final Sink< Status > statusSink =
      statusChannel.andThen (
        allChannel
      );

    private final Scoring scoring;

    Context (
      final Environment environment
    ) {

      this.environment =
        environment;

      scoring =
        ScoreCards.scoring (
          environment
        );

    }


    /*
     * Creates and adds a Service to
     * the name-to-service map data structure.
     */

    private Service serviceOf (
      final Names.Name name
    ) {

      return
        services
          .computeIfAbsent (
            name,
            this::newService
          );

    }

    private Service newService (
      final Names.Name name
    ) {

      return
        new Service (
          name,
          signalsSink.andThen (
            ScoreCards.sink (
              scoring,
              new Synchronizer (
                services::get
              ).andThen (
                statusSink
              )
            )
          )
        );

    }

    @Override
    public Environment getEnvironment () {

      return environment;

    }

    @Override
    public Service service (
      final Name name
    ) {

      //noinspection SuspiciousMethodCalls
      final Service service =
        services.get (
          name
        );

      return
        service != null
        ? service
        : serviceOf ( (Names.Name) name );

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
        allChannel
          .subscribe (
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
          signalsChannel
            .subscribe (
              (Subscriber< Signal >) subscriber
            );

      } else if ( type == Status.class ) {

        return
          statusChannel
            .subscribe (
              (Subscriber< Status >) subscriber
            );

      } else {

        throw
          new IllegalArgumentException ();

      }

    }

    private static final class Synchronizer
      implements Sink< Status > {

      private final Function< ? super Names.Name, Service > lookup;
      private       Service                                 service;

      Synchronizer (
        final Function< ? super Names.Name, Service > lookup
      ) {

        this.lookup =
          lookup;

      }

      @Override
      public void accept (
        final Names.Name name,
        final Orientation orientation,
        final Status value
      ) {

        Service service =
          this.service;

        if ( service == null ) {
          this.service
            = service
            = lookup.apply ( name );
        }

        service.status =
          value;

      }

    }

  }

}
