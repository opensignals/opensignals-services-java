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

package io.opensignals.services;

import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Subscriber;
import io.opensignals.services.Services.Subscription;
import io.opensignals.services.testkit.TestKit.Closure;
import org.junit.jupiter.api.Test;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.testkit.TestKit.capture;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test class for the {@link Subscriber} and {@link Subscription} interfaces.
 *
 * @author wlouth
 * @since 1.0
 */

final class SubscriberTest
  extends AbstractTest {

  @Test
  void subscribe_signals () {

    final Closure< Signal > closure =
      new Closure<> ();

    final Subscription subscription =
      context.subscribe (
        ( name, registrar ) ->
          registrar.accept (
            ( orientation, value ) -> {
              closure.capture =
                capture (
                  name,
                  orientation,
                  value,
                  closure.capture
                );
            }
          ),
        Signal.class
      );

    s1.start ();
    s2.call ();
    s2.succeeded ();

    subscription.cancel ();

    s1.succeed ();
    s1.stop ();

    assertEquals (
      capture (
        s1.getName (),
        EMIT,
        START
      ).to (
        s2.getName (),
        EMIT,
        CALL
      ).to (
        RECEIPT,
        SUCCEED
      ),
      closure.capture
    );

  }

}
