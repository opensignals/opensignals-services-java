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
import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Status;
import io.opensignals.services.ext.spi.alpha.Sinks.Sink;

import static io.opensignals.services.Services.Status.NONE;
import static io.opensignals.services.ext.spi.alpha.Names.Name;

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

  private final Sink< ? super Signal > sink;

  private final Name   name;
  volatile      Status status = NONE;

  Service (
    final Name name,
    final Sink< ? super Signal > sink
  ) {

    this.name =
      name;

    this.sink =
      sink;

  }

  @Override
  public Name getName () {

    return
      name;

  }

  @Override
  public Status getStatus () {

    return
      status;

  }

  @Override
  public void signal (
    final Orientation orientation,
    final Signal signal
  ) {

    sink.accept (
      name,
      orientation,
      signal
    );

  }

}