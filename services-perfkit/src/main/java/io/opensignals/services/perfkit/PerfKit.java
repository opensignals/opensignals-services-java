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

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Consumer;

import static io.opensignals.services.Services.*;
import static io.opensignals.services.Services.Orientation.EMIT;
import static io.opensignals.services.Services.Signal.*;
import static java.lang.System.getProperty;

/**
 * The PerfKit utility class for micro-benchmarking purposes.
 *
 * @author wlouth
 * @since 1.0
 */

@SuppressWarnings (
  {
    "unused",
    "MethodMayBeStatic",
    "PublicMethodNotExposedInInterface",
    "squid:S00100",
    "UnusedReturnValue"
  }
)
@State ( Scope.Benchmark )
public class PerfKit {

  @SuppressWarnings ( {"EmptyMethod", "WeakerAccess"} )
  static void callback (
    final Orientation orientation,
    final Signal signal
  ) {
    // no overhead method
  }

  private static final Subscriber< Signal > SUBSCRIBER = new SignalsSubscriber ();

  private static final String FIRST              = "first";
  private static final String SECOND             = "second";
  private static final String THIRD              = "third";
  private static final String FIRST_SECOND_THIRD = "first.second.third";
  private static final String SERVICE            = "service";
  private static final String SEPARATOR          = "/";
  private static final String PROFILE            = "profile";
  private static final String EXTENSION          = ".properties";
  private static final String ALPHA              = "alpha";

  private Method method;

  private Name    name;
  private Context context;
  private Service service;
  private Service serviceWithCallback;

  private static final Environment ENVIRONMENT =
    environment (
      name ( "opensignals.services.context.service.name" ),
      "default"
    );

  private static final Name FIRST_NAME =
    name (
      FIRST
    );


  @Setup ( Level.Trial )
  public final void setup ()
  throws NoSuchMethodException, IOException {

    final Properties properties =
      new Properties ();

    properties.load (
      PerfKit.class.getResourceAsStream (
        SEPARATOR +
          getProperty (
            PROFILE,
            ALPHA
          ) +
          EXTENSION
      )
    );

    context =
      context (
        environment (
          path ->
            properties.getProperty (
              path.toString ()
            )
        )
      );

    context.subscribe (
      SUBSCRIBER,
      Signal.class
    );

    service =
      context.service (
        SERVICE
      );

    serviceWithCallback =
      service (
        service,
        PerfKit::callback
      );

    method =
      PerfKit.class.getDeclaredMethod (
        "setup"
      );

    name =
      name (
        method
      );

  }


  /**
   * Parse a composite path.
   */

  @Benchmark
  public Name name_parse () {

    return
      name (
        FIRST_SECOND_THIRD
      );

  }

  /**
   * Create of a root name.
   */

  @Benchmark
  public Name name_root () {

    return
      name (
        FIRST
      );

  }


  /**
   * Extend an existing name.
   */

  @Benchmark
  public Name name_name () {

    return
      name.name (
        FIRST
      );

  }


  /**
   * Call {@code Name.toString}
   */

  @Benchmark
  public String name_to_string () {

    return
      name.toString ();

  }


  /**
   * Create a composite name using fluid calls.
   */

  @Benchmark
  public Name name_chain () {

    return
      name (
        FIRST
      ).name (
        SECOND
      ).name (
        THIRD
      );

  }

  /**
   * Create a composite name using varargs.
   */

  @Benchmark
  public Name name_string_string_string () {

    return
      name (
        FIRST,
        SECOND,
        THIRD
      );

  }

  /**
   * Create a composite name using varargs.
   */

  @Benchmark
  public Name name_name_string_string () {

    return
      name (
        FIRST
      ).name (
        SECOND,
        THIRD
      );

  }

  /**
   * Create a composite name from a class.
   */

  @Benchmark
  public Name name_class () {

    return
      name (
        getClass ()
      );

  }

  /**
   * Create a composite name from a class and simple string.
   */

  @Benchmark
  public Name name_class_string () {

    return
      name (
        getClass (),
        "member"
      );

  }


  /**
   * Create a composite name from a method.
   */

  @Benchmark
  public Name name_method () {

    return
      name (
        method
      );

  }


  /**
   * Lookup of a service by name.
   */

  @Benchmark
  public Service service_lookup () {

    return
      context.service (
        name
      );

  }


  /**
   * Emit a {@code SUCCEED} signal.
   */

  @Benchmark
  public void service_emit_succeed () {

    service.emit (
      SUCCEED
    );

  }


  /**
   * Call {@code Service.getStatus}
   */

  @Benchmark
  public Status service_get_status () {

    return
      service.getStatus ();

  }


  /**
   * Call {@code Service.signal()} on a service.
   */

  @Benchmark
  public void service_signal_emit_succeed () {

    service.signal (
      EMIT,
      SUCCEED
    );

  }


  /**
   * Call {@code Service.succeed()} on a service.
   */

  @Benchmark
  public Service service_succeed () {

    return
      service.succeed ();

  }

  /**
   * Call {@code Service.succeed(EMIT)} on a service.
   */

  @Benchmark
  public Service service_succeed_emit () {

    return
      service.succeed (
        EMIT
      );

  }


  /**
   * Emit a {@code START} signal.
   */

  @Benchmark
  public void service_signal_emit_start () {

    service.signal (
      EMIT,
      START
    );

  }


  /**
   * Emit a {@code START} signal.
   */

  @Benchmark
  public void service_emit_start () {

    service.emit (
      START
    );

  }


  /**
   * Call {@code Service.start()} on a service.
   */

  @Benchmark
  public Service service_start () {

    return
      service.start ();

  }

  /**
   * Call {@code Service.start(EMIT)} on a service.
   */

  @Benchmark
  public Service service_start_emit () {

    return
      service.start (
        EMIT
      );

  }


  /**
   * Receipt of a {@code SUCCEED} signal.
   */

  @Benchmark
  public void service_receipt_succeed () {

    service.receipt (
      SUCCEED
    );

  }


  /**
   * Call {@code Service.succeeded()} on a service.
   */

  @Benchmark
  public Service service_succeeded () {

    return
      service.succeeded ();

  }


  /**
   * Emit a {@code SUCCEED} signal with a callback
   */

  @Benchmark
  public void service_emit_succeed_with_callback () {

    serviceWithCallback.emit (
      SUCCEED
    );

  }


  /**
   * Call {@code Service.succeed()} with a callback
   */

  @Benchmark
  public Service service_succeed_with_callback () {

    return
      serviceWithCallback.succeed ();

  }

  /**
   * Call {@code Service.succeed(EMIT)} with a callback
   */

  @Benchmark
  public Service service_succeed_emit_with_callback () {

    return
      serviceWithCallback.succeed (
        EMIT
      );

  }


  /**
   * Emit a {@code SUCCEED} signal with a callback
   */

  @Benchmark
  public void service_receipt_succeed_with_callback () {

    serviceWithCallback.receipt (
      SUCCEED
    );

  }


  /**
   * Call {@code Service.succeeded()} with a callback
   */

  @Benchmark
  public Service service_succeeded_with_callback () {

    return
      serviceWithCallback.succeeded ();

  }


  /**
   * Calls {@code Services.environment(string,value)}
   */

  @Benchmark
  public Environment environment_string_value_create () {

    return
      environment (
        name ( FIRST ),
        FIRST
      );

  }

  private static final Environment environment_string_value =
    environment (
      name ( FIRST ),
      FIRST
    );

  /**
   * Calls {@code Environment.getString(string,value)}.
   */

  @Benchmark
  public boolean environment_string_value_get_string () {

    return
      environment_string_value.getString (
        FIRST_NAME
      ).isPresent ();

  }


  /**
   * Call {@code Context.subscribe()}.
   */

  @Benchmark
  public void add_remove_subscriber () {

    context.subscribe (
      SUBSCRIBER,
      Signal.class
    ).cancel ();

  }

  /**
   * Call {@code Services.context()}.
   */

  @Benchmark
  public Context services_context_default () {

    return
      context ();

  }

  /**
   * Call {@code Services.context()}.
   */

  @Benchmark
  public Context services_context_environment () {

    return
      context (
        ENVIRONMENT
      );

  }

  /**
   * Call {@code Services.variable(name, long)}.
   */

  @Benchmark
  public Variable< Long > services_variable_long () {

    return
      variable (
        FIRST_NAME,
        1L
      );

  }

  /**
   * Call {@code Services.variable(name, long)}.
   */

  @Benchmark
  public Variable< Integer > services_variable_integer () {

    return
      variable (
        FIRST_NAME,
        1
      );

  }

  /**
   * Call {@code Services.variable(name, string)}.
   */

  @Benchmark
  public Variable< String > services_variable_string () {

    return
      variable (
        FIRST_NAME,
        FIRST
      );

  }

  /**
   * Call {@code Services.variable(name, double)}.
   */

  @Benchmark
  public Variable< Double > services_variable_double () {

    return
      variable (
        FIRST_NAME,
        1d
      );

  }

  /**
   * Call {@code Services.variable(name, double)}.
   */

  @Benchmark
  public Variable< Float > services_variable_float () {

    return
      variable (
        FIRST_NAME,
        1f
      );

  }

  /**
   * Call {@code Services.variable(name, boolean)}.
   */

  @Benchmark
  public Variable< Boolean > services_variable_boolean () {

    return
      variable (
        FIRST_NAME,
        true
      );

  }

  /**
   * Call {@code Services.variable(name, enum)}.
   */

  @Benchmark
  public Variable< Signal > services_variable_enum () {

    return
      variable (
        FIRST_NAME,
        Signal.class,
        CALL
      );

  }

  /**
   * Call {@code Services.variable(name, char_sequence)}.
   */

  @Benchmark
  public Variable< CharSequence > services_variable_char_sequence () {

    return
      variable (
        FIRST_NAME,
        (CharSequence) FIRST
      );

  }

  /**
   * Call {@code Services.variable(name, object)}.
   */

  @Benchmark
  public Variable< Object > services_variable_object () {

    return
      variable (
        FIRST_NAME,
        (Object) FIRST
      );

  }


  private static final class SignalsSubscriber
    implements Subscriber< Signal > {

    SignalsSubscriber () {}

    @Override
    public void accept (
      final Name name,
      final Consumer< Callback< ? super Signal > > registrar
    ) {

      registrar.accept (
        PerfKit::callback
      );

    }
  }

}