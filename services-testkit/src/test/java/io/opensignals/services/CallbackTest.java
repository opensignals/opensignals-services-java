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

import io.opensignals.services.Services.Service;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.testkit.TestKit;
import io.opensignals.services.testkit.TestKit.Capture;
import io.opensignals.services.testkit.TestKit.Closure;
import io.opensignals.services.testkit.TestKit.Recorder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.service;
import static io.opensignals.services.testkit.TestKit.capture;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test class for the {@link Services.Callback} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class CallbackTest
  extends AbstractTest {

  @Test
  void service_callback () {

    final Closure< Signal > closure =
      new Closure<> ();

    final Service service =
      service (
        s1,
        ( orientation, value ) ->
          closure.capture =
            capture (
              s1.getName (),
              orientation,
              value,
              closure.capture
            )
      );

    final Recorder< Signal > recorder =
      TestKit.recorder (
        context,
        Signal.class
      );

    recorder.start ();

    service.start ();
    service.succeed ();
    service.stop ();

    final Capture< Signal > expected =
      capture (
        service.getName (),
        EMIT,
        START
      ).to (
        SUCCEED
      ).to (
        STOP
      );

    assertEquals (
      Optional.of (
        expected
      ),
      recorder.stop ()
    );

    assertEquals (
      expected,
      closure.capture
    );


  }

}
