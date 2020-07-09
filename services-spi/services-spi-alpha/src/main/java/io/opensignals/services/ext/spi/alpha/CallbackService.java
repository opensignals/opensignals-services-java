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
      delegate
        .getName ();

  }


  @Override
  public Status getStatus () {

    return
      delegate
        .getStatus ();

  }


  @Override
  public void signal (
    final Orientation orientation,
    final Signal signal
  ) {

    delegate
      .signal (
        orientation,
        signal
      );

    callback
      .accept (
        orientation,
        signal
      );

  }

}