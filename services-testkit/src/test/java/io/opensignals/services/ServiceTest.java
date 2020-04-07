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

import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Service;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.testkit.TestKit.Recorder;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.Status.*;
import static io.opensignals.services.testkit.TestKit.capture;
import static io.opensignals.services.testkit.TestKit.recorder;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test class for the {@link Services.Service} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class ServiceTest
  extends AbstractTest {

  private static void signal (
    final Recorder< Signal > recorder,
    final Service service,
    final Orientation orientation,
    final Signal signal
  ) {

    recorder.start ();

    service.signal (
      orientation,
      signal
    );

    assertEquals (
      Optional.of (
        capture (
          service.getName (),
          orientation,
          signal
        )
      ),
      recorder.stop ()
    );

  }

  private static void signal (
    final Recorder< Signal > recorder,
    final Service service,
    final Orientation orientation,
    final Signal signal,
    final BiFunction< ? super Service, ? super Orientation, Service > f1,
    final Function< ? super Service, Service > f2
  ) {

    signal (
      recorder,
      service,
      orientation,
      signal
    );

    recorder.start ();

    assertSame (
      service,
      f1.apply (
        service,
        orientation
      )
    );

    assertEquals (
      Optional.of (
        capture (
          service.getName (),
          orientation,
          signal
        )
      ),
      recorder.stop ()
    );

    recorder.start ();

    assertSame (
      service,
      f2.apply (
        service
      )
    );

    assertEquals (
      Optional.of (
        capture (
          service.getName (),
          orientation,
          signal
        )
      ),
      recorder.stop ()
    );

  }


  @Test
  void lookup () {

    assertSame (
      S1_NAME,
      s1.getName ()
    );

    assertEquals (
      UNKNOWN,
      s1.getStatus ()
    );

    assertSame (
      s1,
      context.service (
        S1_NAME
      )
    );

    assertSame (
      S2_NAME,
      s2.getName ()
    );

    assertEquals (
      UNKNOWN,
      s2.getStatus ()
    );

    assertSame (
      s2,
      context.service (
        S2_NAME
      )
    );

    assertNotEquals (
      s1,
      s2
    );

  }


  @Test
  void signals () {

    final Recorder< Signal > recorder =
      recorder (
        context,
        Signal.class
      );

    signal (
      recorder,
      s1,
      EMIT,
      START,
      Service::start,
      Service::start
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      START,
      Service::start,
      Service::started
    );

    signal (
      recorder,
      s1,
      EMIT,
      STOP,
      Service::stop,
      Service::stop
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      STOP,
      Service::stop,
      Service::stopped
    );

    signal (
      recorder,
      s1,
      EMIT,
      CALL,
      Service::call,
      Service::call
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      CALL,
      Service::call,
      Service::called
    );

    signal (
      recorder,
      s1,
      EMIT,
      SUCCEED,
      Service::succeed,
      Service::succeed
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      SUCCEED,
      Service::succeed,
      Service::succeeded
    );

    signal (
      recorder,
      s1,
      EMIT,
      FAIL,
      Service::fail,
      Service::fail
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      FAIL,
      Service::fail,
      Service::failed
    );

    signal (
      recorder,
      s1,
      EMIT,
      ELAPSE,
      Service::elapse,
      Service::elapse
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      ELAPSE,
      Service::elapse,
      Service::elapsed
    );

    signal (
      recorder,
      s1,
      EMIT,
      DELAY,
      Service::delay,
      Service::delay
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      DELAY,
      Service::delay,
      Service::delayed
    );

    signal (
      recorder,
      s1,
      EMIT,
      DROP,
      Service::drop,
      Service::drop
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      DROP,
      Service::drop,
      Service::dropped
    );

    signal (
      recorder,
      s1,
      EMIT,
      RECOURSE,
      Service::recourse,
      Service::recourse
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      RECOURSE,
      Service::recourse,
      Service::recoursed
    );

    signal (
      recorder,
      s1,
      EMIT,
      REJECT,
      Service::reject,
      Service::reject
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      REJECT,
      Service::reject,
      Service::rejected
    );

    signal (
      recorder,
      s1,
      EMIT,
      RETRY,
      Service::retry,
      Service::retry
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      RETRY,
      Service::retry,
      Service::retried
    );

    signal (
      recorder,
      s1,
      EMIT,
      DISCONNECT,
      Service::disconnect,
      Service::disconnect
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      DISCONNECT,
      Service::disconnect,
      Service::disconnected
    );

    signal (
      recorder,
      s1,
      EMIT,
      SCHEDULE,
      Service::schedule,
      Service::schedule
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      SCHEDULE,
      Service::schedule,
      Service::scheduled
    );

    signal (
      recorder,
      s1,
      EMIT,
      SUSPEND,
      Service::suspend,
      Service::suspend
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      SUSPEND,
      Service::suspend,
      Service::suspended
    );

    signal (
      recorder,
      s1,
      EMIT,
      RESUME,
      Service::resume,
      Service::resume
    );

    signal (
      recorder,
      s1,
      RECEIPT,
      RESUME,
      Service::resume,
      Service::resumed
    );

  }


  @Test
  void status () {

    // some basic test cases
    // need to extend to more
    // complex interactions
    // switching between states

    s1.start ();

    assertEquals (
      UNKNOWN,
      s1.getStatus ()
    );

    s1.fail ();

    assertEquals (
      DEFECTIVE,
      s1.getStatus ()
    );

    s1.disconnect ();

    assertEquals (
      DOWN,
      s1.getStatus ()
    );

    s1.succeed ();
    s1.recourse ();
    s1.succeed ();
    s1.recourse ();
    s1.succeed ();
    s1.recourse ();
    s1.succeed ();
    s1.recourse ();

    assertEquals (
      DEGRADED,
      s1.getStatus ()
    );

  }


  @Test
  void services () {

    final Set< Service > services =
      context
        .services ()
        .collect (
          toSet ()
        );

    assertEquals (
      2,
      services.size ()
    );

    assertTrue (
      services.contains (
        s1
      )
    );

    assertTrue (
      services.contains (
        s2
      )
    );


  }


}
