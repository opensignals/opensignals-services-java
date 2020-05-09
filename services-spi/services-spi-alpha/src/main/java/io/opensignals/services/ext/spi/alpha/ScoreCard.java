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

import io.opensignals.services.Services.Status;

import java.util.function.IntToLongFunction;

import static io.opensignals.services.Services.Status.*;

/**
 * A non-thread safe class used for scoring the signals fired by a service.
 *
 * @author wlouth
 * @since 1.0
 */

final class ScoreCard {

  private static final long DECAY   = 75L;
  private static final long PERCENT = 100L;

  private long ok;
  private long degraded;
  private long defective;
  private long down;

  private void decay () {

    ok =
      decay (
        ok
      );

    defective =
      decay (
        defective
      );

    degraded =
      decay (
        degraded
      );

    down =
      decay (
        down
      );

  }

  private static long decay (
    final long current
  ) {

    return
      current != 0L
      ? current * DECAY / PERCENT
      : 0L;

  }

  private Status score (
    final Status status,
    final long value
  ) {

    long max =
      value;

    Status result =
      status;

    long temp;

    if ( ( temp = ok ) > max ) {

      max =
        temp;

      result =
        OK;

    }

    if ( ( temp = degraded ) > max ) {

      max =
        temp;

      result =
        DEGRADED;

    }

    if ( ( temp = defective ) > max ) {

      max =
        temp;

      result =
        DEFECTIVE;

    }

    return
      down > max
      ? DOWN
      : result;

  }

  private Status update (
    final IntToLongFunction fn,
    final int value,
    final Status status
  ) {

    decay ();

    return
      score (
        status,
        fn.applyAsLong (
          value
        )
      );

  }


  Status ok (
    final int value
  ) {

    //noinspection ImplicitNumericConversion
    return
      update (
        inc ->
          ok += inc,
        value,
        OK
      );

  }


  Status degraded (
    final int value
  ) {

    //noinspection ImplicitNumericConversion
    return
      update (
        inc ->
          degraded += inc,
        value,
        DEGRADED
      );

  }


  Status defective (
    final int value
  ) {

    //noinspection ImplicitNumericConversion
    return
      update (
        inc ->
          defective += inc,
        value,
        DEFECTIVE
      );

  }

  Status down (
    final int value
  ) {

    //noinspection ImplicitNumericConversion
    return
      update (
        inc ->
          down += inc,
        value,
        DOWN
      );

  }

}