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
import io.opensignals.services.ext.spi.alpha.Variables.Variable;

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

  private static final Integer[] SCORE_DEFAULTS = {0, 2, 4, 8, 16, 64};
  private static final Integer[] DECAY_DEFAULTS = {0, 75, 75, 75, 75, 75};

  @SuppressWarnings ( "WeakerAccess" )
  static final Status[] STATES = Status.values ();

  @SuppressWarnings ( "WeakerAccess" )
  static final int[] MAPPINGS = new int[SIGNALS.length];

  @SuppressWarnings ( "unchecked" )
  private static final Variable< Integer >[] SCORE_VARS =
    (Variable< Integer >[]) ( new Variable[SIGNALS.length] );

  @SuppressWarnings ( "unchecked" )
  private static final Variable< Integer >[] DECAY_VARS =
    (Variable< Integer >[]) ( new Variable[STATES.length] );

  static {

    map (
      SUCCEED,
      OK
    );

    map (
      FAIL,
      DEFECTIVE
    );

    map (
      RECOURSE,
      DEGRADED
    );

    map (
      ELAPSE,
      DEGRADED
    );

    map (
      ELAPSE,
      DEGRADED
    );

    map (
      RETRY,
      DEGRADED
    );

    map (
      REJECT,
      DEFECTIVE
    );

    map (
      DROP,
      DEGRADED
    );

    map (
      DELAY,
      DEGRADED
    );

    map (
      SUSPEND,
      DEGRADED
    );

    map (
      DISCONNECT,
      DOWN
    );

    final Names.Name root =
      Names.root ( Strings.OPENSIGNALS )
        .node ( Strings.SERVICES )
        .node ( Strings.SERVICE );

    final Names.Name signals =
      root.node (
        Strings.SIGNAL
      );

    for ( final Signal signal : SIGNALS ) {

      final int index =
        signal.ordinal ();

      //noinspection ObjectAllocationInLoop
      SCORE_VARS[index] =
        Variables.of (
          signals.node (
            signal
          ).node (
            Strings.SCORE
          ),
          SCORE_DEFAULTS[MAPPINGS[index]]
        );

    }

    final Names.Name states =
      root.node (
        Strings.STATUS
      );

    for ( final Status status : STATES ) {

      final int index =
        status.ordinal ();

      //noinspection ObjectAllocationInLoop
      DECAY_VARS[index] =
        Variables.of (
          states.node (
            status
          ).node (
            Strings.DECAY
          ),
          DECAY_DEFAULTS[MAPPINGS[index]]
        );

    }

  }

  private static void map (
    final Signal succeed,
    final Status ok
  ) {

    MAPPINGS[succeed.ordinal ()] =
      ok.ordinal ();

  }

  private static int[] decays (
    final Environment environment
  ) {

    final int count =
      DECAY_VARS.length;

    final int[] decays =
      new int[count];

    for (
      int i = count - 1;
      i >= 0;
      i--
    ) {

      decays[i] =
        Math.min (
          Math.max (
            DECAY_VARS[i].of (
              environment
            ),
            0
          ),
          100
        );

    }

    return
      decays;

  }

  private static int[] scores (
    final Environment environment
  ) {

    final int count =
      SCORE_VARS.length;

    final int[] scores =
      new int[count];

    for (
      int i = count - 1;
      i >= 0;
      i--
    ) {

      scores[i] =
        Math.max (
          SCORE_VARS[i].of (
            environment
          ),
          0
        );

    }

    return
      scores;

  }

  private ScoreCards () {}

  static Scoring scoring (
    final Environment environment
  ) {


    return
      new Scoring (
        scores (
          environment
        ),
        decays (
          environment
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

    private final long[]                 totals = new long[STATES.length];
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

      final long[] totals =
        this.totals;

      long max = 0L;
      int result = 0;

      for (
        int i = totals.length - 1;
        i >= 0;
        i--
      ) {

        final long total =
          ( totals[i] * decay[i] ) / 100L;

        totals[i] =
          total;

        if ( total > max ) {

          result = i;
          max = total;

        }

      }

      final long total =
        totals[status] += unit;

      return
        total > max
        ? status
        : result;

    }

    private int update (
      final int status,
      final int value
    ) {

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

        return
          current == score
          ? -1
          : ( current = score );

      } finally {

        atomic.set (
          UNLOCKED
        );

      }

    }

    @Override
    public void accept (
      final Names.Name name,
      final Orientation orientation,
      final Signal value
    ) {

      final int signal =
        value.ordinal ();

      final int score =
        scoring.scores[signal];

      if ( score > 0 ) {

        final int status =
          update (
            MAPPINGS[signal],
            score
          );

        if ( status >= 0 ) {

          sink.accept (
            name,
            EMIT,
            STATES[status]
          );

        }

      }

    }

  }

}
