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

import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

final class Subscriptions {

  private Subscriptions () {}

  static < T extends Phenomenon > Subscription< T > scan (
    final Subscription< T > initial
  ) {

    Subscription< T > next =
      initial;

    while (
      next != null &&
        next.dispatcher == null
    ) {

      next =
        next.next;

    }

    return next;

  }

  static final class Subscription< T extends Phenomenon >
    implements Services.Subscription {

    volatile Dispatcher< T > dispatcher;

    Subscription< T > next;

    Subscription (
      final Subscriber< ? super T > subscriber,
      final Subscription< T > next
    ) {

      dispatcher =
        new Dispatcher<> (
          subscriber
        );

      this.next =
        next;

    }


    void dispatch (
      final Name name,
      final Orientation orientation,
      final T value
    ) {

      final Dispatcher< T > target =
        dispatcher;

      if ( target != null ) {

        try {

          target.dispatch (
            name,
            orientation,
            value
          );

        } catch (
          final Throwable error
        ) {

          dispatcher =
            null;

        }

      }

    }


    @Override
    public void cancel () {

      final Dispatcher< T > target =
        dispatcher;

      if ( target != null ) {

        dispatcher =
          null;

        next =
          scan (
            next
          );

      } else {

        throw new IllegalStateException ();

      }

    }


    private static final class Dispatcher< T extends Phenomenon > {

      private static final Callback< ? > IGNORE =
        ( orientation, value ) -> { /* do nothing with the update */ };

      private final ConcurrentHashMap< Name, Callback< ? super T > > callbacks =
        new ConcurrentHashMap<> ();

      private final Subscriber< ? super T > subscriber;

      Dispatcher (
        final Subscriber< ? super T > subscriber
      ) {

        this.subscriber =
          subscriber;

      }

      void dispatch (
        final Name name,
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
