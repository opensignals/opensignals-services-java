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

package io.opensignals.services.testkit;

import io.opensignals.services.Services;
import io.opensignals.services.Services.Context;
import io.opensignals.services.Services.Name;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.testkit.TestKit.Capture;
import io.opensignals.services.testkit.TestKit.Recorder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.START;
import static io.opensignals.services.Services.Signal.STOP;
import static io.opensignals.services.Services.name;
import static io.opensignals.services.testkit.TestKit.capture;
import static io.opensignals.services.testkit.TestKit.recorder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@link TestKit} class.
 */

final class TestKitTest {

  private static final Name FIRST = name ( "first" );
  private static final Name LAST  = name ( "last" );

  @Test
  void capture_to () {

    final Capture< Signal > first =
      capture (
        FIRST,
        EMIT,
        START
      );

    assertEquals (
      FIRST,
      first.getName ()
    );

    assertEquals (
      EMIT,
      first.getOrientation ()
    );

    assertEquals (
      START,
      first.getValue ()
    );

    assertFalse (
      first
        .getPrevious ()
        .isPresent ()
    );

    final Capture< Signal > second =
      first.to (
        STOP
      );

    assertEquals (
      STOP,
      second.getValue ()
    );

    second
      .getPrevious ()
      .filter ( first::equals )
      .orElseGet ( Assertions::fail );

    final Capture< Signal > third =
      second.to (
        LAST,
        EMIT,
        START
      );

    third
      .getPrevious ()
      .flatMap ( Capture::getPrevious )
      .filter ( first::equals )
      .orElseGet ( Assertions::fail );

  }

  @Test
  void recorder_signals () {

    final Context context =
      Services.context ();

    final Recorder< Signal > recorder =
      recorder (
        context,
        Signal.class
      );

    recorder.start ();

    context
      .service ( FIRST )
      .emit ( START );

    context
      .service ( FIRST )
      .emit ( STOP );

    assertEquals (
      Optional.of (
        capture (
          FIRST,
          EMIT,
          START
        ).to (
          STOP
        )
      ),
      recorder.stop ()
    );

    context
      .service ( FIRST )
      .emit ( START );

    assertFalse (
      recorder
        .stop ()
        .isPresent ()
    );

    recorder.start ();

    context
      .service ( FIRST )
      .emit ( STOP );

    assertEquals (
      Optional.of (
        capture (
          FIRST,
          EMIT,
          STOP
        )
      ),
      recorder.stop ()
    );

  }

}