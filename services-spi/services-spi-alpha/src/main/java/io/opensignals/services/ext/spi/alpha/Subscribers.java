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
import io.opensignals.services.Services.Callback;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Phenomenon;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * @author wlouth
 * @since 1.0
 */

final class Subscribers {

  private Subscribers () {}

  static < T extends Phenomenon > Sinks.Sink< T > sink (
    final Services.Subscriber< ? super T > subscriber
  ) {

    return
      new Subscriber<> (
        subscriber
      );

  }

  private static final class Subscriber< T extends Phenomenon >
    implements Sinks.Sink< T > {

    private static final Callback< ? > IGNORE =
      ( orientation, value ) -> { /* do nothing with the update */ };

    private final ConcurrentHashMap< Name, Callback< ? super T > > callbacks =
      new ConcurrentHashMap<> ();

    private final Services.Subscriber< ? super T > subscriber;

    Subscriber (
      final Services.Subscriber< ? super T > subscriber
    ) {

      this.subscriber =
        subscriber;

    }

    public void accept (
      final Names.Name name,
      final Orientation orientation,
      final T value
    ) {

      final Callback< ? super T > callback =
        callbackOf (
          name
        );

      if ( callback != IGNORE ) {

        callback.accept (
          orientation,
          value
        );

      }

    }


    @SuppressWarnings ( "unchecked" )
    private Callback< ? super T > newCallback (
      final Name name
    ) {

      final Closure< T > closure =
        new Closure<> (
          (Callback< T >) IGNORE
        );

      subscriber.accept (
        name,
        callback ->
          closure.callback =
            requireNonNull (
              callback
            )
      );

      return
        closure.callback;

    }


    private Callback< ? super T > callbackOf (
      final Name name
    ) {

      final Callback< ? super T > callback =
        callbacks.get (
          name
        );

      return
        callback != null ?
        callback :
        createIfAbsent ( name );

    }

    private Callback< ? super T > createIfAbsent (
      final Name name
    ) {

      return
        callbacks.computeIfAbsent (
          name,
          this::newCallback
        );

    }

    private static final class Closure< T extends Phenomenon > {

      Callback< ? super T > callback;

      Closure (
        final Callback< T > initial
      ) {

        callback =
          initial;

      }

    }

  }

}
