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

import io.opensignals.services.Services.*;

import java.util.concurrent.atomic.AtomicReference;

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

  interface Channel< T extends Phenomenon > {

    /**
     * Dispatches a change related to a service.
     *
     * @param name        the name of the service.
     * @param orientation the orientation of the change
     * @param value       the phenomenon value
     */

    void dispatch (
      Name name,
      Orientation orientation,
      T value
    );

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

    private final AtomicReference< Subscriptions.Subscription< T > > store =
      new AtomicReference<> ();

    @Override
    public void dispatch (
      final Name name,
      final Orientation orientation,
      final T value
    ) {

      //noinspection LocalVariableOfConcreteClass
      final Subscriptions.Subscription< T > head =
        store.get ();

      if ( head != null ) {

        dispatch (
          name,
          orientation,
          value,
          head
        );

      }

    }


    private void dispatch (
      final Name name,
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

          if ( prev != null ) {

            prev.next =
              next;

          } else {

            store.compareAndSet (
              head,
              next
            );

          }

          if ( next == null ) {
            //noinspection BreakStatement
            break;
          }

          current =
            next;

        }

        current.dispatch (
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


    @Override
    public Subscription subscribe (
      final Subscriber< ? super T > subscriber
    ) {

      return
        store.updateAndGet (
          next ->
            new Subscriptions.Subscription<> (
              subscriber,
              Subscriptions.scan (
                next
              )
            )
        );

    }

  }

}
