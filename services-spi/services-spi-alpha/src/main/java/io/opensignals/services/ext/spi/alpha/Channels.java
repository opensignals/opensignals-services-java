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

import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Phenomenon;
import io.opensignals.services.Services.Subscriber;
import io.opensignals.services.Services.Subscription;
import io.opensignals.services.ext.spi.alpha.Sinks.Sink;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author wlouth
 * @since 1.0
 */

final class Channels {

  private Channels () {}

  static < T extends Phenomenon > Channel< T > memory () {

    return
      new Memory<> ();

  }

  /**
   * Represents a channel for dispatching and receiving (via subscription) service changes.
   *
   * @param <T> the type of phenomenon.
   */

  interface Channel< T extends Phenomenon > extends Sink< T > {

    /**
     * Adds a {@link Subscriber} to a channel.
     *
     * @param subscriber the subscriber to be registered with the channel
     * @return A subscription representation this particular subscription call.
     */

    Subscription subscribe ( Subscriber< ? super T > subscriber );

  }

  /**
   * An implementation of the {@link Channel} interface that performs all event dispatching
   * immediately and within the same thread generating the event.
   *
   * @param <T> The type of {@link Phenomenon}
   */

  static final class Memory< T extends Phenomenon >
    implements Channel< T > {

    @SuppressWarnings ( {"rawtypes", "java:S3740"} )
    private static final AtomicReferenceFieldUpdater< Memory, Subscriptions.Subscription > UPDATER =
      AtomicReferenceFieldUpdater.newUpdater (
        Memory.class,
        Subscriptions.Subscription.class,
        "store"
      );

    private volatile Subscriptions.Subscription< T > store;

    @Override
    public void accept (
      final Names.Name name,
      final Orientation orientation,
      final T value
    ) {

      //noinspection LocalVariableOfConcreteClass
      final Subscriptions.Subscription< T > head =
        store;

      if ( head != null ) {

        dispatch (
          name,
          orientation,
          value,
          head
        );

      }

    }

    @Override
    public Subscription subscribe (
      final Subscriber< ? super T > subscriber
    ) {

      final Sink< ? super T > sink =
        Subscribers.sink (
          subscriber
        );

      //noinspection unchecked
      return
        UPDATER.updateAndGet (
          this,
          next ->
            Subscriptions.create (
              sink,
              Subscriptions.scan (
                next
              )
            )
        );

    }

    private void dispatch (
      final Names.Name name,
      final Orientation orientation,
      final T value,
      final Subscriptions.Subscription< T > head
    ) {

      Subscriptions.Subscription< T > current =
        head;

      Subscriptions.Subscription< T > prev =
        null;

      do {

        final Subscriptions.Subscription< T > next =
          Subscriptions.scan (
            current
          );

        if ( next != current ) {

          remove (
            prev,
            next,
            head
          );

          if ( next == null )
            break;

          current =
            next;

        }

        current
          .accept (
            name,
            orientation,
            value
          );

        prev =
          current;

      } while (
        ( current = current.next ) != null
      );

    }

    private void remove (
      final Subscriptions.Subscription< T > prev,
      final Subscriptions.Subscription< T > next,
      final Subscriptions.Subscription< T > head
    ) {

      if ( prev != null ) {

        prev.next =
          next;

      } else {

        UPDATER
          .compareAndSet (
            this,
            head,
            next
          );

      }

    }

  }

}