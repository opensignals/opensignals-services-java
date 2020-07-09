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

import io.opensignals.services.Services.Environment;
import io.opensignals.services.Services.Orientation;
import io.opensignals.services.Services.Signal;
import io.opensignals.services.Services.Status;
import io.opensignals.services.ext.spi.alpha.Sinks.Sink;

import java.util.concurrent.atomic.AtomicInteger;

import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static io.opensignals.services.Services.Status.*;

/**
 * @author wlouth
 * @since 1.0
 */

final class ScoreCards {

  private static final Signal[] SIGNALS = Signal.values ();

  private static final int[] SCORE_DEFAULTS = {0, 2, 4, 8, 16, 64};
  private static final int[] DECAY_DEFAULTS = {0, 75, 75, 75, 75, 75};

  static final Status[] STATES  = Status.values ();
  static final int[]    MAPPING = new int[SIGNALS.length];

  static {

    MAPPING[SUCCEED.ordinal ()] =
      OK.ordinal ();

    MAPPING[FAIL.ordinal ()] =
      DEFECTIVE.ordinal ();

    MAPPING[RECOURSE.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[ELAPSE.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[ELAPSE.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[RETRY.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[REJECT.ordinal ()] =
      DEFECTIVE.ordinal ();

    MAPPING[DROP.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[DELAY.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[SUSPEND.ordinal ()] =
      DEGRADED.ordinal ();

    MAPPING[DISCONNECT.ordinal ()] =
      DOWN.ordinal ();

  }

  private static int[] decays (
    final Environment environment,
    final Names.Name prefix
  ) {

    final int[] decays =
      new int[STATES.length];

    for (
      int i = STATES.length - 1;
      i >= 0;
      i--
    ) {

      decays[i] =
        Math.min (
          Math.max (
            environment.getInteger (
              prefix.node (
                STATES[i]
              ).node (
                Strings.DECAY
              ),
              DECAY_DEFAULTS[i]
            ),
            0
          ),
          100
        );

    }
    return decays;
  }

  private static int[] scores (
    final Environment environment,
    final Names.Name prefix
  ) {
    final int[] scores =
      new int[SIGNALS.length];

    for (
      int i = SIGNALS.length - 1;
      i >= 0;
      i--
    ) {

      scores[i] =
        Math.max (
          environment.getInteger (
            prefix.node (
              SIGNALS[i]
            ).node (
              Strings.SCORE
            ),
            SCORE_DEFAULTS[MAPPING[i]]
          ),
          0
        );

    }
    return scores;
  }

  private ScoreCards () {}

  static Scoring scoring (
    final Environment environment
  ) {

    final Names.Name prefix =
      Names.root ( Strings.OPENSIGNALS )
        .node ( Strings.SERVICES )
        .node ( Strings.SERVICE );

    return
      new Scoring (
        scores (
          environment,
          prefix.node (
            Strings.SIGNAL
          )
        ),
        decays (
          environment,
          prefix.node (
            Strings.STATUS
          )
        )
      );

  }

  @SuppressWarnings ( "unused" )
  static Sink< Signal > sink (
    final Scoring scoring,
    final Sink< ? super Status > sink
  ) {

    return
      new Atomic (
        scoring,
        sink
      );

  }

  static final class Scoring {

    final int[] scores;
    final int[] decay;

    @SuppressWarnings ( "AssignmentOrReturnOfFieldWithMutableType" )
    Scoring (
      final int[] scores,
      final int[] decay
    ) {

      this.scores =
        scores;

      this.decay =
        decay;

    }

  }

  /**
   * A non-thread safe class used for scoring the signals fired by a service.
   *
   * @author wlouth
   * @since 1.0
   */

  private static final class Atomic
    implements Sink< Signal > {

    private static final int LOCKED   = 1;
    private static final int UNLOCKED = 0;

    // this guards access to the score card
    private final AtomicInteger lock =
      new AtomicInteger ();

    private final long[]                 scores = new long[STATES.length];
    private final Scoring                scoring;
    private final Sink< ? super Status > sink;
    private       int                    current;

    Atomic (
      final Scoring scoring,
      final Sink< ? super Status > sink
    ) {

      this.scoring =
        scoring;

      this.sink =
        sink;

    }

    private int score (
      final int status,
      final int unit
    ) {

      final int[] decay =
        scoring.decay;

      long max = 0L;
      int result = 0;

      for (
        int i = STATES.length - 1;
        i >= 0;
        i--
      ) {

        long total =
          ( scores[i] * decay[i] ) / 100L;

        if ( i == status )
          total += unit;

        scores[i] =
          total;

        if ( total > max ) {

          result = i;
          max = total;

        }


      }

      return
        result;

    }

    private void update (
      final Names.Name name,
      final int status,
      final int value
    ) {

      final int update;

      final AtomicInteger atomic =
        lock;

      try {

        // spin-and-yield strategy with the
        // assumption that the scoring of a
        // status (or signal) is very fast
        // along with a change event dispatch
        // if indeed the status has changed

        while ( !atomic.compareAndSet ( UNLOCKED, LOCKED ) )
          //noinspection CallToThreadYield
          Thread.yield ();

        final int score =
          score (
            status,
            value
          );

        update =
          current == score
          ? -1
          : ( current = score );

      } finally {

        atomic.set (
          UNLOCKED
        );

      }

      if ( update >= 0 ) {

        sink.accept (
          name,
          EMIT,
          STATES[update]
        );

      }

    }

    @Override
    public void accept (
      final Names.Name name,
      final Orientation orientation,
      final Signal value
    ) {

      final int index =
        value.ordinal ();

      final int score =
        scoring.scores[index];

      if ( score > 0 ) {

        update (
          name,
          MAPPING[index],
          score
        );

      }

    }

  }

}
