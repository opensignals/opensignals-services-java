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
import io.opensignals.services.Services.Callback;
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Status;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Orientation.RECEIPT;
import static io.opensignals.services.Services.Signal.*;

/**
 * An implementation of {@link Service} that dispatches a signal firing to a {@link Callback}
 *
 * @author wlouth
 * @since 1.0
 */

final class CallbackService
  implements Services.Service {

  private final Services.Service           delegate;
  private final Callback< ? super Signal > callback;

  CallbackService (
    final Services.Service delegate,
    final Callback< ? super Signal > callback
  ) {

    this.delegate =
      delegate;

    this.callback =
      callback;

  }


  @Override
  public Services.Name getName () {

    return
      delegate.getName ();

  }


  @Override
  public Status getStatus () {

    return
      delegate.getStatus ();

  }


  @Override
  public void signal (
    final Orientation orientation,
    final Signal signal
  ) {

    delegate.signal (
      orientation,
      signal
    );

    callback.accept (
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

    delegate.signal (
      orientation,
      first,
      second
    );

    // this does mean that a subscriber
    // will see both signals before the
    // callback receives the first one

    callback.accept (
      orientation,
      first
    );

    callback.accept (
      orientation,
      second
    );

  }

  @Override
  public void emit (
    final Signal signal
  ) {

    delegate.emit (
      signal
    );

    callback.accept (
      EMIT,
      signal
    );

  }

  @Override
  public void receipt (
    final Signal signal
  ) {

    delegate.receipt (
      signal
    );

    callback.accept (
      RECEIPT,
      signal
    );

  }

  @Override
  public void receipt (
    final Signal first,
    final Signal second
  ) {

    delegate.receipt (
      first,
      second
    );

    // this does mean that a subscriber
    // will see both signals before the
    // callback receives the first one

    callback.accept (
      RECEIPT,
      first
    );

    callback.accept (
      RECEIPT,
      second
    );

  }

  @Override
  public void emit (
    final Signal first,
    final Signal second
  ) {

    delegate.receipt (
      first,
      second
    );

    // this does mean that a subscriber
    // will see both signals before the
    // callback receives the first one

    callback.accept (
      EMIT,
      first
    );

    callback.accept (
      EMIT,
      second
    );

  }

  @Override
  public Services.Service start () {

    delegate.start ();

    callback.accept (
      EMIT,
      START
    );

    return this;

  }

  @Override
  public Services.Service started () {

    delegate.started ();

    callback.accept (
      RECEIPT,
      START
    );

    return this;

  }

  @Override
  public Services.Service stop () {

    delegate.stop ();

    callback.accept (
      EMIT,
      STOP
    );

    return this;

  }

  @Override
  public Services.Service stopped () {

    delegate.stopped ();

    callback.accept (
      RECEIPT,
      STOP
    );

    return this;

  }

  @Override
  public Services.Service call () {

    delegate.call ();

    callback.accept (
      EMIT,
      CALL
    );

    return this;

  }

  @Override
  public Services.Service called () {

    delegate.called ();

    callback.accept (
      RECEIPT,
      CALL
    );

    return this;

  }

  @Override
  public Services.Service succeed () {

    delegate.succeed ();

    callback.accept (
      EMIT,
      SUCCEED
    );

    return this;

  }

  @Override
  public Services.Service succeeded () {

    delegate.succeeded ();

    callback.accept (
      RECEIPT,
      SUCCEED
    );

    return this;

  }

  @Override
  public Services.Service fail () {

    delegate.fail ();

    callback.accept (
      EMIT,
      FAIL
    );

    return this;

  }

  @Override
  public Services.Service failed () {

    delegate.failed ();

    callback.accept (
      RECEIPT,
      FAIL
    );

    return this;

  }

  @Override
  public Services.Service recourse () {

    delegate.recourse ();

    callback.accept (
      EMIT,
      RECOURSE
    );

    return this;

  }

  @Override
  public Services.Service recoursed () {

    delegate.recoursed ();

    callback.accept (
      RECEIPT,
      RECOURSE
    );

    return this;

  }

  @Override
  public Services.Service elapse () {

    delegate.elapse ();

    callback.accept (
      EMIT,
      ELAPSE
    );

    return this;

  }

  @Override
  public Services.Service elapsed () {

    delegate.elapsed ();

    callback.accept (
      RECEIPT,
      ELAPSE
    );

    return this;

  }

  @Override
  public Services.Service retry () {

    delegate.retry ();

    callback.accept (
      EMIT,
      RETRY
    );

    return this;

  }

  @Override
  public Services.Service retried () {

    delegate.retried ();

    callback.accept (
      RECEIPT,
      RETRY
    );

    return this;

  }

  @Override
  public Services.Service reject () {

    delegate.reject ();

    callback.accept (
      EMIT,
      REJECT
    );

    return this;

  }

  @Override
  public Services.Service rejected () {

    delegate.rejected ();

    callback.accept (
      RECEIPT,
      REJECT
    );

    return this;

  }

  @Override
  public Services.Service drop () {

    delegate.drop ();

    callback.accept (
      EMIT,
      DROP
    );

    return this;

  }

  @Override
  public Services.Service dropped () {

    delegate.dropped ();

    callback.accept (
      RECEIPT,
      DROP
    );

    return this;

  }

  @Override
  public Services.Service delay () {

    delegate.delay ();

    callback.accept (
      EMIT,
      DELAY
    );

    return this;

  }

  @Override
  public Services.Service delayed () {

    delegate.delayed ();

    callback.accept (
      RECEIPT,
      DELAY
    );

    return this;

  }

  @Override
  public Services.Service schedule () {

    delegate.schedule ();

    callback.accept (
      EMIT,
      SCHEDULE
    );

    return this;

  }

  @Override
  public Services.Service scheduled () {

    delegate.scheduled ();

    callback.accept (
      RECEIPT,
      SCHEDULE
    );

    return this;

  }

  @Override
  public Services.Service suspend () {

    delegate.suspend ();

    callback.accept (
      EMIT,
      SUSPEND
    );

    return this;

  }

  @Override
  public Services.Service suspended () {

    delegate.suspended ();

    callback.accept (
      RECEIPT,
      SUSPEND
    );

    return this;

  }

  @Override
  public Services.Service resume () {

    delegate.resume ();

    callback.accept (
      EMIT,
      RESUME
    );

    return this;

  }

  @Override
  public Services.Service resumed () {

    delegate.resumed ();

    callback.accept (
      RECEIPT,
      RESUME
    );

    return this;

  }

  @Override
  public Services.Service disconnect () {

    delegate.disconnect ();

    callback.accept (
      EMIT,
      DISCONNECT
    );

    return this;

  }

  @Override
  public Services.Service disconnected () {

    delegate.disconnected ();

    callback.accept (
      RECEIPT,
      DISCONNECT
    );

    return this;

  }

  @Override
  public Services.Service succeed (
    final Orientation orientation
  ) {

    delegate.succeed (
      orientation
    );

    callback.accept (
      orientation,
      SUCCEED
    );

    return this;

  }

  @Override
  public Services.Service fail (
    final Orientation orientation
  ) {

    delegate.fail (
      orientation
    );

    callback.accept (
      orientation,
      FAIL
    );

    return this;

  }

  @Override
  public Services.Service elapse (
    final Orientation orientation
  ) {

    delegate.elapse (
      orientation
    );

    callback.accept (
      orientation,
      ELAPSE
    );

    return this;

  }

  @Override
  public Services.Service drop (
    final Orientation orientation
  ) {

    delegate.drop (
      orientation
    );

    callback.accept (
      orientation,
      DROP
    );

    return this;

  }

  @Override
  public Services.Service reject (
    final Orientation orientation
  ) {

    delegate.reject (
      orientation
    );

    callback.accept (
      orientation,
      REJECT
    );

    return this;

  }

  @Override
  public Services.Service retry (
    final Orientation orientation
  ) {

    delegate.retry (
      orientation
    );

    callback.accept (
      orientation,
      RETRY
    );

    return this;

  }

  @Override
  public Services.Service disconnect (
    final Orientation orientation
  ) {

    delegate.disconnect (
      orientation
    );

    callback.accept (
      orientation,
      DISCONNECT
    );

    return this;

  }

  @Override
  public Services.Service call (
    final Orientation orientation
  ) {

    delegate.call (
      orientation
    );

    callback.accept (
      orientation,
      CALL
    );

    return this;

  }

  @Override
  public Services.Service start (
    final Orientation orientation
  ) {

    delegate.start (
      orientation
    );

    callback.accept (
      orientation,
      START
    );

    return this;

  }

  @Override
  public Services.Service stop (
    final Orientation orientation
  ) {

    delegate.stop (
      orientation
    );

    callback.accept (
      orientation,
      STOP
    );

    return this;

  }

  @Override
  public Services.Service recourse (
    final Orientation orientation
  ) {

    delegate.recourse (
      orientation
    );

    callback.accept (
      orientation,
      RECOURSE
    );

    return this;

  }

  @Override
  public Services.Service delay (
    final Orientation orientation
  ) {

    delegate.delay (
      orientation
    );

    callback.accept (
      orientation,
      DELAY
    );

    return this;

  }

  @Override
  public Services.Service schedule (
    final Orientation orientation
  ) {

    delegate.schedule (
      orientation
    );

    callback.accept (
      orientation,
      SCHEDULE
    );

    return this;

  }

  @Override
  public Services.Service suspend (
    final Orientation orientation
  ) {

    delegate.suspend (
      orientation
    );

    callback.accept (
      orientation,
      SUSPEND
    );

    return this;

  }

  @Override
  public Services.Service resume (
    final Orientation orientation
  ) {

    delegate.resume (
      orientation
    );

    callback.accept (
      orientation,
      RESUME
    );

    return this;

  }

}