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

import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Subscription;
import io.opensignals.services.ext.spi.alpha.Channels.Channel;
import org.junit.jupiter.api.Test;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.START;
import static io.opensignals.services.Services.Signal.STOP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class MemoryChannelTest {

  private static final class Closure {

    Name        name;
    Orientation orientation;
    Signal      signal;

    Closure () {}
  }


  @Test
  void subscribe_dispatch_cancel () {

    final Channel< Signal > channel =
      Channels.memory ();

    final Closure closure =
      new Closure ();

    final Subscription subscription =
      channel.subscribe (
        ( name, registrar ) ->
          registrar.accept (
            ( orientation, signal ) -> {

              closure.name =
                name;
              closure.orientation =
                orientation;
              closure.signal =
                signal;

            }
          )
      );

    final Name name =
      Provider.INSTANCE.name (
        getClass ()
      );

    channel.dispatch (
      name,
      EMIT,
      START
    );

    assertEquals (
      closure.name,
      name
    );

    assertEquals (
      EMIT,
      closure.orientation
    );

    assertEquals (
      START,
      closure.signal
    );

    channel.dispatch (
      name,
      RECEIPT,
      STOP
    );

    assertEquals (
      closure.name,
      name
    );

    assertEquals (
      RECEIPT,
      closure.orientation
    );

    assertEquals (
      STOP,
      closure.signal
    );

    subscription.cancel ();

    closure.name =
      null;

    closure.orientation =
      null;

    closure.signal =
      null;

    channel.dispatch (
      name,
      RECEIPT,
      STOP
    );

    assertNull (
      closure.name
    );

    assertNull (
      closure.signal
    );

    assertNull (
      closure.orientation
    );

  }


}