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

import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Phenomenon;

/**
 * @author wlouth
 * @since 1.0
 */

final class Sinks {

  private Sinks () {}

  @FunctionalInterface
  interface Sink< T extends Phenomenon > {

    /**
     * Accepts a change, signal or status, related to a service.
     *
     * @param name        the name of the service.
     * @param orientation the orientation of the change
     * @param value       the phenomenon value
     */

    void accept (
      Names.Name name,
      Orientation orientation,
      T value
    );

    /**
     * Creates a new sink that first calls this sink and then another.
     *
     * @param after the other sink to be called after this sink
     * @return A new sink that first calls this sink and then another
     */

    default Sink< T > andThen (
      final Sink< ? super T > after
    ) {

      return
        ( name, orientation, value ) -> {

          accept (
            name,
            orientation,
            value
          );

          after.accept (
            name,
            orientation,
            value
          );

        };

    }

  }

}
