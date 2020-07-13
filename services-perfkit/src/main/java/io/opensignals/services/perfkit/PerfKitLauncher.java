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

package io.opensignals.services.perfkit;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.AverageTimeResult;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toList;
import static org.openjdk.jmh.runner.options.TimeValue.seconds;

/**
 * The PerfKit class used to launch separate micro-benchmarking processes.
 *
 * @author wlouth
 * @since 1.0
 */

final class PerfKitLauncher {

  private static final String    NEWLINE         = System.getProperty ( "line.separator" );
  private static final char      SEMICOLON       = ':';
  private static final char      SPACE           = ' ';
  private static final int       ITERATIONS      = 2;
  private static final TimeValue TIME            = seconds ( 2L );
  private static final TimeValue WARMUP_TIME     = seconds ( 1L );
  private static final int       WARM_ITERATIONS = 2;
  private static final boolean   GC_ENABLED      = true;
  private static final int       FORK_COUNT      = 2;
  private static final boolean   FAIL_ON_ERROR   = true;

  private static Collection< RunResult > run (
    final Options opt
  ) throws RunnerException {

    return
      new Runner (
        opt
      ).run ();

  }

  private static Options options (
    final String spi,
    final String profile,
    final String pattern,
    final int threads
  ) {

    return
      new OptionsBuilder ()
        .include ( PerfKit.class.getSimpleName () + "." + pattern )
        .mode ( Mode.AverageTime )
        .timeUnit ( NANOSECONDS )
        .warmupTime ( WARMUP_TIME )
        .warmupIterations ( WARM_ITERATIONS )
        .measurementTime ( TIME )
        .measurementIterations ( ITERATIONS )
        .threads ( threads )
        .shouldFailOnError ( FAIL_ON_ERROR )
        .shouldDoGC ( GC_ENABLED )
        .forks ( FORK_COUNT )
        .jvmArgsAppend (
          "-server",
          "-Dio.opensignals.services.spi.factory=" + spi,
          "-Dprofile=" + profile
        ).build ();

  }

  private static void inspect (
    final String spi,
    final Collection< ? extends RunResult > results,
    final double threshold,
    final Consumer< ? super String > consumer
  ) {

    final List< AverageTimeResult > list =
      results
        .stream ()
        .flatMap (
          run ->
            run.getBenchmarkResults ().stream () )
        .map ( BenchmarkResult::getPrimaryResult )
        .map ( AverageTimeResult.class::cast )
        .filter (
          result ->
            result.getScore () > threshold )
        .collect (
          toList ()
        );

    if ( !list.isEmpty () ) {

      final StringBuilder message =
        new StringBuilder ( list.size () * 100 );

      message.append (
        "provider"
      ).append (
        SEMICOLON
      ).append (
        SPACE
      ).append (
        spi
      ).append (
        NEWLINE
      );

      list.forEach (
        result ->
          message.append (
            result.getLabel ()
          ).append (
            SEMICOLON
          ).append (
            SPACE
          ).append (
            result.getScore ()
          ).append (
            NEWLINE
          )
      );

      consumer.accept (
        message.toString ()
      );

    }

  }


  private PerfKitLauncher () {
  }

  static void execute (
    final String spi,
    final String profile,
    final String pattern,
    final int threads,
    final double threshold,
    final Consumer< ? super String > consumer
  ) {

    try {

      inspect (
        spi,
        run (
          options (
            spi,
            profile,
            pattern,
            threads
          )
        ),
        threshold,
        consumer
      );

    } catch (
      final Throwable error
    ) {

      consumer.accept (
        error.toString ()
      );

    }

  }

}