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
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Phenomenon;
import io.opensignals.services.ext.spi.alpha.Sinks.Sink;

/**
 * @author wlouth
 * @since 1.0
 */

final class Subscriptions {

  private Subscriptions () {}

  static < T extends Phenomenon > Subscription< T > create (
    final Sink< ? super T > sink,
    final Subscription< T > next
  ) {

    return
      new Subscription<> (
        sink,
        next
      );

  }

  static < T extends Phenomenon > Subscription< T > scan (
    final Subscription< T > initial
  ) {

    Subscription< T > next =
      initial;

    while (
      next != null &&
        next.sink == null
    ) {

      next =
        next.next;

    }

    return
      next;

  }

  static final class Subscription< T extends Phenomenon >
    implements Sink< T >, Services.Subscription {

    volatile Sink< ? super T > sink;

    Subscription< T > next;

    Subscription (
      final Sink< ? super T > sink,
      final Subscription< T > next
    ) {

      this.sink =
        sink;

      this.next =
        next;

    }


    public void accept (
      final Names.Name name,
      final Orientation orientation,
      final T value
    ) {

      final Sink< ? super T > sink =
        this.sink;

      if ( sink != null ) {

        try {

          sink.accept (
            name,
            orientation,
            value
          );

        } catch (
          final Throwable error
        ) {

          this.sink =
            null;

        }

      }

    }


    @Override
    public void cancel () {

      if ( sink != null ) {

        sink =
          null;

      } else {

        throw
          new IllegalStateException ();

      }

    }

  }

}
