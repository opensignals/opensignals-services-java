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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.Status.UNKNOWN;
import static io.opensignals.services.ext.spi.alpha.Names.root;

/**
 * The SPI implementation of {@link Services.Service}.
 * <p>
 * Implementation Note:
 * Currently the scoring of a signal involves first mapping it to
 * a status and then scoring the status. We could in the future
 * allow for the mapping of signal to status to be configurable as
 * well as the scoring to be done on the signal itself.
 *
 * @author wlouth
 * @since 1.0
 */

final class Service
  implements Services.Service {

  private static final int LOCKED   = 1;
  private static final int UNLOCKED = 0;
  private static final int ATTEMPTS = 5;

  private static final Variables.Variable< Integer > OK_SCORE =
    Variables.of (
      root ( Strings.OPENSIGNALS )
        .name ( Strings.SERVICES )
        .name ( Strings.SERVICE )
        .name ( Strings.STATUS )
        .name ( Strings.OK )
        .name ( Strings.SCORE ),
      1
    );

  private static final Variables.Variable< Integer > DEGRADED_SCORE =
    Variables.of (
      root ( Strings.OPENSIGNALS )
        .name ( Strings.SERVICES )
        .name ( Strings.SERVICE )
        .name ( Strings.STATUS )
        .name ( Strings.DEGRADED )
        .name ( Strings.SCORE ),
      4
    );

  private static final Variables.Variable< Integer > DEFECTIVE_SCORE =
    Variables.of (
      root ( Strings.OPENSIGNALS )
        .name ( Strings.SERVICES )
        .name ( Strings.SERVICE )
        .name ( Strings.STATUS )
        .name ( Strings.DEFECTIVE )
        .name ( Strings.SCORE ),
      16
    );

  private static final Variables.Variable< Integer > DOWN_SCORE =
    Variables.of (
      root ( Strings.OPENSIGNALS )
        .name ( Strings.SERVICES )
        .name ( Strings.SERVICE )
        .name ( Strings.STATUS )
        .name ( Strings.DOWN )
        .name ( Strings.SCORE ),
      64
    );

  // for performance reasons each
  // mapping has a corresponding field

  private final int down;
  private final int defective;
  private final int degraded;
  private final int ok;

  private final Contexts.Context context;
  private final Name             name;

  private final ScoreCard scoreCard =
    new ScoreCard ();

  // this guards access to the score card
  // which is not (currently) thread safe

  private final AtomicInteger lock =
    new AtomicInteger ();

  private Status status = UNKNOWN;

  Service (
    final Contexts.Context context,
    final Name name
  ) {

    this.context =
      context;

    this.name =
      name;

    // we need to determine the scoring for
    // each of the mapped status enum values
    // using the environment of the context

    final Environment environment =
      context.getEnvironment ();

    ok =
      OK_SCORE.of (
        environment
      );

    degraded =
      DEGRADED_SCORE.of (
        environment
      );

    defective =
      DEFECTIVE_SCORE.of (
        environment
      );

    down =
      DOWN_SCORE.of (
        environment
      );

  }


  @Override
  public Name getName () {

    return name;

  }


  @Override
  public Status getStatus () {

    // if there is an update in-progress
    // then wait for it to complete
    // no guarantee that an update will
    // not happen after this and before read

    for (
      int i = ATTEMPTS;
      i > 0 &&
        lock.get () != UNLOCKED;
      i--
    ) {

      //noinspection CallToThreadYield
      Thread.yield ();

    }

    return status;

  }


  @Override
  public void signal (
    final Orientation orientation,
    final Signal signal
  ) {

    onSignal (
      orientation,
      signal
    );

  }

  @Override
  public void signal (
    final Orientation orientation,
    final Signal first,
    final Signal second
  ) {

    onSignal (
      orientation,
      first
    );

    onSignal (
      orientation,
      second
    );

  }

  @Override
  public void emit (
    final Signal signal
  ) {

    onSignal (
      EMIT,
      signal
    );

  }

  @Override
  public void receipt (
    final Signal signal
  ) {

    onSignal (
      RECEIPT,
      signal
    );

  }

  @Override
  public void receipt (
    final Signal first,
    final Signal second
  ) {

    onSignal (
      RECEIPT,
      first
    );

    onSignal (
      RECEIPT,
      second
    );

  }

  @Override
  public void emit (
    final Signal first,
    final Signal second
  ) {

    onSignal (
      EMIT,
      first
    );

    onSignal (
      EMIT,
      second
    );

  }

  @Override
  public Services.Service succeed (
    final Orientation orientation
  ) {

    onSucceed (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service succeed () {

    onSucceed (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service succeeded () {

    onSucceed (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service fail (
    final Orientation orientation
  ) {

    onFail (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service fail () {

    onFail (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service failed () {

    onFail (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service elapse (
    final Orientation orientation
  ) {

    onElapse (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service elapse () {

    onElapse (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service elapsed () {

    onElapse (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service drop (
    final Orientation orientation
  ) {

    onDrop (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service drop () {

    onDrop (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service dropped () {

    onDrop (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service reject (
    final Orientation orientation
  ) {

    onReject (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service reject () {

    onReject (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service rejected () {

    onReject (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service retry (
    final Orientation orientation
  ) {

    onRetry (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service retry () {

    onRetry (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service retried () {

    onRetry (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service disconnect (
    final Orientation orientation
  ) {

    onDisconnect (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service disconnect () {

    onDisconnect (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service disconnected () {

    onDisconnect (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service call (
    final Orientation orientation
  ) {

    onCall (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service call () {

    onCall (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service called () {

    onCall (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service start (
    final Orientation orientation
  ) {

    onStart (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service start () {

    onStart (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service started () {

    onStart (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service stop (
    final Orientation orientation
  ) {

    onStop (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service stop () {

    onStop (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service stopped () {

    onStop (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service recourse (
    final Orientation orientation
  ) {

    onRecourse (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service recourse () {

    onRecourse (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service recoursed () {

    onRecourse (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service delay (
    final Orientation orientation
  ) {

    onDelay (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service delay () {

    onDelay (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service delayed () {

    onDelay (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service schedule (
    final Orientation orientation
  ) {

    onSchedule (
      orientation
    );

    return this;

  }


  @Override
  public Services.Service schedule () {

    onSchedule (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service scheduled () {

    onSchedule (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service suspend (
    final Orientation orientation
  ) {

    onSuspend (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service suspend () {

    onSuspend (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service suspended () {

    onSuspend (
      RECEIPT
    );

    return this;

  }

  @Override
  public Services.Service resume (
    final Orientation orientation
  ) {

    onResume (
      orientation
    );

    return this;

  }

  @Override
  public Services.Service resume () {

    onResume (
      EMIT
    );

    return this;

  }

  @Override
  public Services.Service resumed () {

    onResume (
      RECEIPT
    );

    return this;

  }

  private void onSucceed (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      SUCCEED
    );

    ok ();

  }

  private void onFail (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      FAIL
    );

    defective ();

  }

  private void onElapse (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      ELAPSE
    );

    degraded ();

  }

  private void onDrop (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      DROP
    );

    degraded ();

  }

  private void onReject (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      REJECT
    );

  }

  private void onRetry (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      RETRY
    );

    degraded ();

  }

  private void onDisconnect (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      DISCONNECT
    );

    down ();

  }

  private void onCall (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      CALL
    );

  }

  private void onStart (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      START
    );

  }

  private void onStop (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      STOP
    );

  }

  private void onRecourse (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      RECOURSE
    );

    degraded ();

  }

  private void onDelay (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      DELAY
    );

    degraded ();

  }

  private void onSchedule (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      SCHEDULE
    );

  }

  private void onSuspend (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      SUSPEND
    );

    degraded ();

  }

  private void onResume (
    final Orientation orientation
  ) {

    context.dispatch (
      name,
      orientation,
      RESUME
    );

  }

  private void onSignal (
    final Orientation orientation,
    final Signal signal
  ) {

    //noinspection SwitchStatement,SwitchStatementWithTooManyBranches
    switch ( signal ) {

      case START:
        onStart (
          orientation
        );
        break;

      case STOP:
        onStop (
          orientation
        );
        break;

      case CALL:
        onCall (
          orientation
        );
        break;

      case SUCCEED:
        onSucceed (
          orientation
        );
        break;

      case FAIL:
        onFail (
          orientation
        );
        break;

      case RECOURSE:
        onRecourse (
          orientation
        );
        break;

      case ELAPSE:
        onElapse (
          orientation
        );
        break;

      case RETRY:
        onRetry (
          orientation
        );
        break;

      case REJECT:
        onReject (
          orientation
        );
        break;

      case DROP:
        onDrop (
          orientation
        );
        break;

      case DELAY:
        onDelay (
          orientation
        );
        break;

      case SCHEDULE:
        onSchedule (
          orientation
        );
        break;

      case SUSPEND:
        onSuspend (
          orientation
        );
        break;

      case RESUME:
        onResume (
          orientation
        );
        break;

      case DISCONNECT:
        onDisconnect (
          orientation
        );
        break;

    }

  }

  private void score (
    final IntFunction< Status > fn,
    final int value
  ) {

    final AtomicInteger atomic =
      lock;

    final Status update;

    try {

      // spin-and-yield strategy with the
      // assumption that the scoring of a
      // status (or signal) is very fast
      // along with a change event dispatch
      // if indeed the status has changed

      while ( !atomic.compareAndSet ( UNLOCKED, LOCKED ) )
        //noinspection CallToThreadYield
        Thread.yield ();

      final Status result =
        fn.apply (
          value
        );

      update =
        status == result
        ? null
        : ( status = result );

    } finally {

      atomic.set (
        UNLOCKED
      );

    }

    // no event ordering guarantee
    // @todo introduce ticketing

    if ( update != null ) {

      context.dispatch (
        name,
        EMIT,
        update
      );

    }

  }


  private void down () {

    score (
      scoreCard::down,
      down
    );

  }


  private void degraded () {

    score (
      scoreCard::degraded,
      degraded
    );

  }

  private void defective () {

    score (
      scoreCard::defective,
      defective
    );

  }

  private void ok () {

    score (
      scoreCard::ok,
      ok
    );

  }


}
