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

package io.opensignals.services;

import io.opensignals.services.spi.ServicesProvider;
import io.opensignals.services.spi.ServicesProviderFactory;

import java.lang.reflect.Member;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.System.getProperty;
import static java.security.AccessController.doPrivileged;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.*;
import static java.util.Spliterator.*;
import static java.util.function.Function.identity;

/**
 * The {@link Services} class is the entry point into the signals for services API.
 * <p>
 * OpenSignals is a new novel approach to the monitoring (observability) of service-to-service interactions
 * based on signalling theory and social systems regulated in part by local and remote status assessment.
 * <p>
 * <b>Execution vs Calling</b>
 * In the Services API there is a clear distinction made between service execution and service calling.
 * An execution is the performance of the service code local to the context. A call is the invocation of
 * some code from a caller site that is potentially remote to the eventual execution though it is possible
 * to have multiple context partitions within the same JVM process if required. The Services class, the
 * only non-interface class in the API, save enums, offers static helper methods for facilitating the right
 * set of signals to generate in the case of execution or calling.
 * <p>
 * <b>Integration</b>
 * OpenSignals offers two interception/extension points for integrating with other systems such as a metrics storage
 * backend. Integration can be achieved by implementing the service provider interface (SPI) or by registering
 * a {@link Subscriber} with a {@link Context} and then a {@link Callback} on a per {@link Service} basis.
 *
 * @author wlouth
 * @since 1.0
 */

public final class Services {

  private static final ServicesProvider PROVIDER;

  static {

    final ServicesProviderFactory factory;

    try {

      factory =
        (ServicesProviderFactory)
          Class.forName (
            doPrivileged (
              (PrivilegedAction< String >) () ->
                getProperty (
                  "io.opensignals.services.spi.factory",
                  "io.opensignals.services.ext.spi.alpha.ProviderFactory"
                ) ) )
            .getConstructor ()
            .newInstance ();

    } catch (
      final Exception error
    ) {

      throw
        new RuntimeException (
          error
        );

    }

    (
      PROVIDER =
        factory.create ()
    ).init ();

  }


  private Services () {
  }


  /**
   * Creates a provider defined default {@link Environment}.
   *
   * @return An {@link Environment} that sources its property values from a function
   * @see ServicesProvider#environment()
   */

  public static Services.Environment environment () {

    return
      PROVIDER.environment ();

  }


  /**
   * Creates a {@link Environment} that sources values using a provided mapping function.
   * <p>
   * Implementation Notes:
   * — Whenever the provided function results in an exception an empty optional will be returned.
   * — Implementations can choose to cache the value for repeated lookups for the same name.
   *
   * @param <T>    The result type of the function
   * @param lookup The function used for sourcing property values
   * @return An {@link Environment} that sources its property values from a function
   * @see ServicesProvider#environment(Function)
   */

  public static < T > Services.Environment environment (
    final Function< ? super Name, T > lookup
  ) {

    return
      PROVIDER.environment (
        lookup
      );

  }


  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param <T>   The property type
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, Object)
   */

  public static < T > Environment environment (
    final Name path,
    final T value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }


  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, int)
   */

  public static Environment environment (
    final Name path,
    final int value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }


  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, long)
   */

  public static Environment environment (
    final Name path,
    final long value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }


  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, float)
   */

  public static Environment environment (
    final Name path,
    final float value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }


  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, double)
   */

  public static Environment environment (
    final Name path,
    final double value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }

  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param path  The property path
   * @param value The property value
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, double)
   */

  public static Environment environment (
    final Name path,
    final boolean value
  ) {

    return
      PROVIDER.environment (
        path,
        value
      );

  }

  /**
   * Creates an {@link Environment} with a single {@link Name} and value pairing.
   *
   * @param <T>      The property type
   * @param path     The property path
   * @param supplier The property value supplier
   * @return A {@link Environment} containing a single property name-value pair.
   * @see ServicesProvider#environment(Name, Object)
   */

  public static < T > Environment environment (
    final Name path,
    final Supplier< T > supplier
  ) {

    return
      PROVIDER.environment (
        path,
        supplier
      );

  }


  /**
   * Useful for some configuration libraries where a lookup of a property can result in an exception
   * thrown but where a check on the presence of the property does not.
   *
   * @param predicate the predicate to be called prior to the function call
   * @param fn        the function used to for mapping a {@link Name} to a value
   * @param <T>       the return type of the function
   * @return An {@link Environment} that sources property values from a function
   * @see ServicesProvider#environment(Predicate, Function)
   */

  public static < T > Environment environment (
    final Predicate< ? super Name > predicate,
    final Function< ? super Name, T > fn
  ) {

    return
      PROVIDER.environment (
        predicate,
        fn
      );

  }


  /**
   * Returns the default {@link Context}.
   *
   * @return The default {@link Context}
   * @see ServicesProvider#context()
   */

  public static Context context () {

    return
      PROVIDER.context ();

  }


  /**
   * Returns a {@link Context} mapped based on one or more properties within {@link Environment} provided.
   * <p>
   * Implementation Notes:
   * — The context returned should equal a context returned with the same environment properties.
   *
   * @param environment the configuration used in the mapping and construction of a {@link Context}
   * @return A {@link Context} constructed from and mapped to the {@link Environment}
   * @see ServicesProvider#context(Environment)
   */

  public static Context context (
    final Environment environment
  ) {

    return
      PROVIDER.context (
        environment
      );

  }


  /**
   * Returns a {@link Name} from a string path.
   *
   * @param path the string to be parsed and returned as a {@link Name}
   * @return A {@link Name} mapped to the path string
   * @throws NullPointerException     if the path parameter is null
   * @throws IllegalArgumentException if processing of path does not result in a name
   * @see Name#name(String)
   * @see ServicesProvider#name(String)
   */

  public static Name name (
    final String path
  ) {

    return
      PROVIDER.name (
        path
      );

  }


  /**
   * Returns a {@link Name} from a concatenation of two string paths.
   *
   * @param first  the first string path to be parsed into a {@link Name}
   * @param second the second string path to be parsed and appended to first
   * @return A {@link Name} mapped to a concatenation of both paths joined with a separator
   * @throws NullPointerException     if one of the arguments is null
   * @throws IllegalArgumentException if processing of the arguments does not result in a name
   * @see Name#name(String, String)
   * @see ServicesProvider#name(String, String)
   */

  public static Name name (
    final String first,
    final String second
  ) {

    return
      PROVIDER.name (
        first,
        second
      );

  }


  /**
   * Creates a {@link Name} from a concatenation of three string paths.
   *
   * @param first  the first string path to be parsed into a {@link Name}
   * @param second the second string path to be parsed and appended to first
   * @param third  the third string path to be parsed and appended to second
   * @return A {@link Name} mapped to a concatenation of all three paths joined with a separator
   * @throws NullPointerException     if one of the arguments is null
   * @throws IllegalArgumentException if processing of the arguments does not result in a name
   * @see Name#name(String, String)
   * @see ServicesProvider#name(String, String, String)
   */

  public static Name name (
    final String first,
    final String second,
    final String third
  ) {

    return
      PROVIDER.name (
        first,
        second,
        third
      );

  }


  /**
   * Create a {@link Name} from iterating over a specified {@link Iterable} of {@link String} values.
   *
   * @param it the {@link Iterable} to be iterated over
   * @return A {@link Name} as a result of the latest iteration of appendage
   * @throws NullPointerException     if the {@link Iterable} is null or one of the values return is null
   * @throws IllegalArgumentException if processing of one of the iterations does not result in a new name
   * @see Name#name(Iterable)
   */

  public static Name name (
    final Iterable< String > it
  ) {

    return
      PROVIDER.name (
        it
      );

  }


  /**
   * Create a {@link Name} from iterating over a specified {@link Iterable} and applying a transformation function.
   *
   * @param <T> the type of each value iterated over
   * @param it  the {@link Iterable} to be iterated over
   * @param fn  the function to be used to transform the type to a String type
   * @return A {@link Name} as a result of the latest iteration of appendage
   * @throws NullPointerException     if the {@link Iterable} is null or one of the values return is null
   * @throws IllegalArgumentException if processing of one of the iterations does not result in a new name
   * @see Name#name(Iterable, Function)
   */

  public static < T > Name name (
    final Iterable< ? extends T > it,
    final Function< T, String > fn
  ) {

    return
      PROVIDER.name (
        it,
        fn
      );

  }


  /**
   * Create a {@link Name} from iterating over a specified {@link Iterator} of {@link String} values.
   *
   * @param it the {@link Iterator} to be iterated over
   * @return A {@link Name} as a result of the latest iteration of appendage
   * @throws NullPointerException     if the {@link Iterator} is null or one of the values return is null
   * @throws IllegalArgumentException if processing of one of the iterations does not result in a new name
   * @see Name#name(Iterable)
   */

  public static Name name (
    final Iterator< String > it
  ) {

    return
      PROVIDER.name (
        it
      );

  }


  /**
   * Create a {@link Name} from iterating over a specified {@link Iterator} and applying a transformation function.
   *
   * @param <T> the type of each value iterated over
   * @param it  the {@link Iterator} to be iterated over
   * @param fn  the function to be used to transform the type to a String type
   * @return A {@link Name} as a result of the latest iteration of appendage
   * @throws NullPointerException     if the {@link Iterator} is null or one of the values return is null
   * @throws IllegalArgumentException if processing of one of the iterations does not result in a new name
   * @see Name#name(Iterable, Function)
   */

  public static < T > Name name (
    final Iterator< ? extends T > it,
    final Function< T, String > fn
  ) {

    return
      PROVIDER.name (
        it,
        fn
      );

  }

  /**
   * Creates a {@link Name} from a {@link Class}.
   *
   * @param cls the {@link Class} to be mapped to a {@link Name}
   * @return A {@link Name} where {@code name.toString().equals(cls.getName())}
   * @see ServicesProvider#name(Class)
   */

  public static Name name (
    final Class< ? > cls
  ) {

    return
      PROVIDER.name (
        cls
      );

  }


  /**
   * Creates a {@link Name} from a {@link Class} and additional path string.
   *
   * @param cls the {@link Class} to be mapped to a {@link Name}
   * @return A {@link Name} where {@code name.toString().equals(cls.getName() + "." + path)}
   * @see ServicesProvider#name(Class, String)
   */

  public static Name name (
    final Class< ? > cls,
    final String path
  ) {

    return
      PROVIDER.name (
        cls,
        path
      );

  }


  /**
   * Creates a {@link Name} from a {@link Member}.
   *
   * @param member the {@link Member} to be mapped to a {@link Name}
   * @return A {@link Name} mapped to the {@link Member}
   */

  public static Name name (
    final Member member
  ) {

    return
      PROVIDER.name (
        member
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Object}.
   *
   * @param name     the name of the configuration item
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Object}.
   */

  public static Variable< Object > variable (
    final Name name,
    final Object defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code T}.
   *
   * @param name     the name of the configuration item
   * @param type     the class type of the value returned
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @param <T>      the class type of the variable
   * @return A {@code Variable} of type {@code T}.
   */

  public static < T > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final T defValue
  ) {

    return
      PROVIDER.variable (
        name,
        type,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code T}.
   *
   * @param name     the name to be matched
   * @param type     the type of the value returned
   * @param alt      an alternative class type to be used as the base for a mapping
   * @param mapper   the function used to transform the alternative type to the return type or <tt>null</tt>
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @param <T>      the class of the return value type
   * @param <A>      the class of the alternative value type
   * @return A {@code Variable} of type {@code T}.
   * @see Environment#getType(Name, Class, Class, Function)
   */

  public static < T, A > Variable< T > variable (
    final Name name,
    final Class< ? extends T > type,
    final Class< ? extends A > alt,
    final Function< ? super A, ? extends T > mapper,
    final T defValue
  ) {

    return
      PROVIDER.variable (
        name,
        type,
        alt,
        mapper,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Enum}.
   *
   * @param name     the name to be matched
   * @param type     the class type of the returned
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @param <T>      the class of the return value type
   * @return A {@code Variable} of type {@code T}.
   * @see Environment#getEnum(Name, Class, Enum)
   */

  @SuppressWarnings ( "BoundedWildcard" )
  public static < T extends Enum< T > > Variable< T > variable (
    final Name name,
    final Class< T > type,
    final T defValue
  ) {

    return
      PROVIDER.variable (
        name,
        type,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Boolean}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Boolean}.
   * @see Environment#getBoolean(Name, boolean)
   */

  public static Variable< Boolean > variable (
    final Name name,
    final Boolean defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Integer}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Integer}.
   * @see Environment#getInteger(Name, int)
   */

  public static Variable< Integer > variable (
    final Name name,
    final Integer defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Long}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Long}.
   * @see Environment#getLong(Name, long)
   */

  public static Variable< Long > variable (
    final Name name,
    final Long defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Double}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Double}.
   * @see Environment#getDouble(Name, double)
   */

  public static Variable< Double > variable (
    final Name name,
    final Double defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Float}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code Float}.
   * @see Environment#getFloat(Name, float)
   */

  public static Variable< Float > variable (
    final Name name,
    final Float defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code String}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code String}.
   * @see Environment#getString(Name, String)
   */

  public static Variable< String > variable (
    final Name name,
    final String defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code String}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code CharSequence}.
   * @see Environment#getCharSequence(Name, CharSequence)
   */

  public static Variable< CharSequence > variable (
    final Name name,
    final CharSequence defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * Creates a {@link Variable} of type {@code Name}.
   *
   * @param name     the name to be matched
   * @param defValue the value to be used if the variable is not present in an environment passed
   * @return A {@code Variable} of type {@code String}.
   * @see Environment#getName(Name, Name)
   */

  public static Variable< Name > variable (
    final Name name,
    final Name defValue
  ) {

    return
      PROVIDER.variable (
        name,
        defValue
      );

  }


  /**
   * A utility method that decorates a {@link Service} with a {@link Callback}.
   *
   * @param service  the service to be decorated
   * @param callback the callback to be called on signalling
   * @return a new service instance with the appropriate callbacks hooks installed
   */

  public static Service service (
    final Service service,
    final Callback< ? super Signal > callback
  ) {

    return
      PROVIDER.service (
        service,
        callback
      );

  }


  /**
   * Invokes the {@link Fn} parameter, calling {@link Service#start} before
   * executing the {@link Fn}, followed by the {@link Service#succeed()} or
   * {@link Service#fail()} depending on whether the {@link Fn}
   * threw an exception during execution. Finally, calls {@link Service#stop}.
   *
   * @param fn  the {@link Fn} to be executed
   * @param <R> The result type of the {@link Fn}
   * @param <T> The exception type of the derived throwable
   * @return The result of the {@link Fn#apply()} call
   * @throws T the exception thrown
   */

  public static < R, T extends Throwable > R execute (
    final Service service,
    final Fn< R, T > fn
  ) throws T {

    return
      PROVIDER.execute (
        service,
        fn
      );

  }


  /**
   * Invokes the {@link Op} parameter, calling {@link Service#start} before
   * executing the {@link Op}, followed by the {@link Service#succeed()} or
   * {@link Service#fail()} depending on whether the {@link Op}
   * threw an exception during execution. Finally, calls {@link Service#stop}.
   *
   * @param op  the {@link Op} to be executed
   * @param <T> The exception type of the derived throwable
   * @throws T the exception thrown
   */

  public static < T extends Throwable > void execute (
    final Service service,
    final Op< T > op
  ) throws T {

    PROVIDER.execute (
      service,
      op
    );

  }


  /**
   * Invokes the {@link Fn} parameter, calling {@link Service#call} before
   * executing the {@link Fn}, followed by the {@link Service#succeed()} or
   * {@link Service#fail()} depending on whether the {@link Fn}
   * threw an exception during execution.
   *
   * @param fn  the {@link Fn} to be executed
   * @param <R> The return type of the {@link Fn}
   * @param <T> The exception type of the derived throwable
   * @return The result of the {@link Fn#apply()} call
   * @throws T the exception thrown
   */

  public static < R, T extends Throwable > R call (
    final Service service,
    final Fn< R, T > fn
  ) throws T {

    return
      PROVIDER.call (
        service,
        fn
      );

  }


  /**
   * Invokes the {@link Op} parameter, calling {@link Service#call} before
   * executing the {@link Op}, followed by the {@link Service#succeeded()} or
   * {@link Service#failed()} depending on whether the {@link Op}
   * threw an exception during execution.
   *
   * @param op  the {@link Op} to be executed
   * @param <T> The exception type of the derived throwable
   * @throws T the exception thrown
   */

  public static < T extends Throwable > void call (
    final Service service,
    final Op< T > op
  ) throws T {

    PROVIDER.call (
      service,
      op
    );

  }


  /**
   * A status is a classification of a service state derived from
   * observed signalling behavior of a local or remote service
   * execution or call, respectively.
   */

  public enum Status
    implements Phenomenon {

    /**
     * Indicate that the service's operational status is yet to be determined.
     */

    NONE,

    /**
     * Indicate that the service's operational status is satisfactory.
     * <p>
     * The primary signal for deriving such an assessment is {@link Signal#SUCCEED}.
     */

    OK,

    /**
     * Indicate that the service's operation is deviating from some normal.
     * <p>
     * The status indicates that a service is exhibiting a pattern that is deviating
     * from a some behavioral baseline or norm but not enough to be impacting.
     */

    DEVIATING,

    /**
     * Indicates a degraded operational status for a service.
     * <p>
     * The status indicates that a service is operating in a sub-par manner
     * though still being able to complete the required work to some degree.
     * The signal set used to derive such an assessment include {@link Signal#RETRY},
     * {@link Signal#DROP}, {@link Signal#DELAY}, {@link Signal#RECOURSE}, and
     * {@link Signal#ELAPSE}. Another possible signal is {@link Signal#SUSPEND}.
     */

    DEGRADED,

    /**
     * Indicates a defective operational status for a service.
     * <p>
     * The primary signal used to derive such an assessment is {@link Signal#FAIL}
     * but also {@link Signal#REJECT} but only in so far as to indicate a client
     * is defective in what it has requested.
     */

    DEFECTIVE,

    /**
     * Indicates a down (or unavailable) operational status for a service.
     * <p>
     * The primary signal for this determination is {@link Signal#DISCONNECT},
     * though the inability for a service call to not complete after repeated
     * {@link Signal#RETRY} and {@link Signal#ELAPSE} signalling could also
     * constitute such an assessment.
     */

    DOWN

  }


  /**
   * A signal is a classification of some phenomenon concerning the
   * operation or outcome of a particular execution or call.
   * <p>
   * The primary purpose of this class along with the capture of behavior
   * is to derived (infer) a {@link Status} for the {@link Service} in question.
   */

  public enum Signal
    implements Phenomenon {

    /**
     * Signal the start of a service execution.
     *
     * @see Service#start()
     */

    START,

    /**
     * Signal the end processing of a service execution.
     *
     * @see Service#stop()
     */

    STOP,

    /**
     * Signal the calling of service at a caller site, or the receipt of a call at a callee site.
     *
     * @see Service#call()
     * @see Service#called()
     */

    CALL,

    /**
     * Signal a successful outcome of a service execution or call.
     *
     * @see Service#succeed()
     * @see Service#succeeded()
     */

    SUCCEED,

    /**
     * Signal a failed outcome of a service execution or call.
     *
     * @see Service#fail()
     * @see Service#failed()
     */

    FAIL,

    /**
     * Signal the use of a fallback mechanism, such as a cache or alternative target, following a failed service call.
     *
     * @see Service#recourse()
     */

    RECOURSE,

    /**
     * Signal the timing out of a service execution or call.
     *
     * @see Service#elapse()
     */

    ELAPSE,

    /**
     * Signal retry of a service call (following a service failure).
     *
     * @see Service#retry()
     */

    RETRY,

    /**
     * Signal the rejection of a service execution or call.
     *
     * @see Service#reject()
     * @see Service#rejected()
     */

    REJECT,

    /**
     * Signal the dropping of a service execution or call.
     *
     * @see Service#drop()
     * @see Service#dropped()
     */

    DROP,

    /**
     * Signal the delaying of a service execution or call.
     *
     * @see Service#delay()
     * @see Service#delayed()
     */

    DELAY,

    /**
     * Signal the scheduling of a service execution or call.
     *
     * @see Service#schedule()
     * @see Service#scheduled()
     */

    SCHEDULE,

    /**
     * Signal the suspension of a service execution or call.
     *
     * @see Service#suspend()
     * @see Service#suspended()
     */

    SUSPEND,

    /**
     * Signal the resumption of a service execution or call.
     *
     * @see Service#suspend()
     * @see Service#suspended()
     */

    RESUME,

    /**
     * Signal a client was unable to reach a service endpoint.
     *
     * @see Service#disconnect()
     */

    DISCONNECT

  }


  /**
   * An enum class used to indicate the method of signal or status change.
   * <p>
   * There are two ways a {@link Signal} can be recorded against a {@link Service} -
   * {@link Orientation#EMIT} and {@link Orientation#RECEIPT}. Emitting a signal is from the
   * self perspective — the service is recording and informing other observers (services) of
   * an operation or outcome. When an observer observes a signal within some message response
   * or event notification it uses {@link Orientation#RECEIPT} to record it against the identified
   * service within its {@link Context}. Receipt of a signal should be taken as being generated
   * in the past whereas emit is the present moment assuming discrete time stepping.
   *
   * @see Service#signal(Orientation, Signal)
   * @see Callback#accept(Orientation, Phenomenon)
   */

  public enum Orientation {

    /**
     * Emit of a phenomenon.
     *
     * @see Service#emit(Signal)
     */

    EMIT,

    /**
     * Receipt of a phenomenon.
     *
     * @see Service#receipt(Signal)
     */

    RECEIPT

  }


  /**
   * A functional interface used for working around method overloading issues.
   *
   * @param <T> The return type of the {@link Fn#apply()}
   * @param <E> The exception type of the derived throwable
   */

  @FunctionalInterface
  public interface Fn< T, E extends Throwable > {

    /**
     * Use this function to force typing of an
     * overloaded method handle at compile time.
     *
     * @param fn  the function to be cast
     * @param <T> the return type of the function
     * @param <E> the exception type thrown by the function
     * @return A casting of the specified function
     */
    static < T, E extends Throwable > Fn< T, E > of (
      final Fn< T, E > fn
    ) {
      return fn;
    }


    /**
     * Invokes the underlying function.
     *
     * @return The result returned by the function
     * @throws E The derived throwable type thrown
     */

    T apply () throws E;

  }


  /**
   * An interface used for working around method overloading issues.
   *
   * @param <E> The exception type of the derived throwable
   */

  @FunctionalInterface
  public interface Op< E extends Throwable > {

    /**
     * Use this function to force typing of an
     * overloaded method handle at compile time.
     *
     * @param op  the operation to be cast
     * @param <E> the exception type thrown by the operation
     * @return A casting of the specified operation
     */

    static < E extends Throwable > Op< E > of (
      final Op< E > op
    ) {
      return op;
    }


    /**
     * Converts a {@link Fn} into an {@code Op}.
     *
     * @param fn  the {@link Fn} to be transformed
     * @param <T> the return type of the function
     * @param <E> the exception type thrown by the function
     * @return An operation that wraps the function
     */

    static < T, E extends Throwable > Op< E > from (
      final Fn< T, ? extends E > fn
    ) {
      return fn::apply;
    }


    /**
     * Invokes the underlying operation.
     *
     * @throws E The derived throwable type thrown
     */

    void apply () throws E;

  }


  /**
   * An interface representing a composite named service with the ability
   * to inspect the availability status and emit or receive signals.
   * <p>
   * Note: An SPI implementation of this interface is free to override
   * the default methods implementation included here.
   */

  public interface Service {

    /**
     * Returns the name for this service.
     *
     * @return the service name
     */

    Name getName ();


    /**
     * Returns the status for this service.
     *
     * @return the service status.
     */

    Status getStatus ();


    /**
     * Record a signal for this service.
     *
     * @param orientation the orientation of the signal
     * @param signal      the signal transmitted
     */

    void signal (
      Orientation orientation,
      Signal signal
    );


    /**
     * Record a signal for this service.
     *
     * @param orientation the orientation of the signal
     * @param first       the first signal to be transmitted
     * @param second      the second signal to be transmitted
     */

    default void signal (
      final Orientation orientation,
      final Signal first,
      final Signal second
    ) {

      signal (
        orientation,
        first
      );

      signal (
        orientation,
        second
      );

    }


    /**
     * Record emit of a signal for this service.
     *
     * @param signal the signal to be emitted
     * @see #signal(Orientation, Signal)
     * @see Orientation#EMIT
     */

    default void emit (
      final Signal signal
    ) {

      signal (
        Orientation.EMIT,
        signal
      );

    }


    /**
     * Record receipt of a signal from this service.
     *
     * @param signal the signal received
     * @see #signal(Orientation, Signal)
     * @see Orientation#RECEIPT
     */

    default void receipt (
      final Signal signal
    ) {

      signal (
        Orientation.RECEIPT,
        signal
      );

    }


    /**
     * Record the receipt of a signal for this (remote) service.
     *
     * @param first  the first signal received
     * @param second the first signal received
     * @see #signal(Orientation, Signal)
     * @see Orientation#RECEIPT
     */

    default void receipt (
      final Signal first,
      final Signal second
    ) {

      signal (
        Orientation.RECEIPT,
        first,
        second
      );

    }


    /**
     * Emit two signals for this service.
     *
     * @param first  the first signal to be emitted
     * @param second the second signal to be emitted
     */

    default void emit (
      final Signal first,
      final Signal second
    ) {

      signal (
        Orientation.EMIT,
        first,
        second
      );

    }


    /**
     * Record a {@link Signal#SUCCEED} signal with a specified {@link Orientation}.
     *
     * @see #call()
     * @see Signal#SUCCEED
     * @see Status#OK
     * @see #succeed()
     * @see #succeeded()
     * @see #signal(Orientation, Signal)
     */

    default Service succeed (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.SUCCEED
      );

      return this;

    }


    /**
     * Emit a {@link Signal#SUCCEED} signal.
     *
     * @see Signal#SUCCEED
     */

    default Service succeed () {

      emit (
        Signal.SUCCEED
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#SUCCEED} signal.
     *
     * @see Signal#SUCCEED
     */

    default Service succeeded () {

      receipt (
        Signal.SUCCEED
      );

      return this;

    }


    /**
     * Record a {@link Signal#FAIL} signal with a specified {@link Orientation}.
     *
     * @see #call()
     * @see Signal#FAIL
     * @see Status#DEFECTIVE
     * @see #fail()
     * @see #failed()
     * @see #signal(Orientation, Signal)
     */

    default Service fail (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.FAIL
      );

      return this;

    }


    /**
     * Emit a {@link Signal#FAIL} signal.
     *
     * @see #failed()
     * @see Signal#FAIL
     */

    default Service fail () {

      emit (
        Signal.FAIL
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#FAIL} signal.
     *
     * @see #fail()
     * @see Signal#FAIL
     */

    default Service failed () {

      receipt (
        Signal.FAIL
      );

      return this;

    }


    /**
     * Record a {@link Signal#ELAPSE} signal with a specified {@link Orientation}.
     *
     * @see #call()
     * @see Signal#ELAPSE
     * @see Status#DEGRADED
     */

    default Service elapse (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.ELAPSE
      );

      return this;

    }


    /**
     * Emit a {@link Signal#ELAPSE} signal.
     *
     * @see #call()
     * @see Signal#ELAPSE
     * @see Orientation#EMIT
     * @see Status#DEGRADED
     */

    default Service elapse () {

      emit (
        Signal.ELAPSE
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#ELAPSE} signal.
     *
     * @see #call()
     * @see Signal#ELAPSE
     * @see Orientation#EMIT
     * @see Status#DEGRADED
     */

    default Service elapsed () {

      receipt (
        Signal.ELAPSE
      );

      return this;

    }


    /**
     * Record a {@link Signal#DROP} signal with a specified {@link Orientation}.
     *
     * @see #call()
     * @see Signal#DROP
     * @see Status#DEGRADED
     * @see #drop()
     * @see #dropped()
     * @see #signal(Orientation, Signal)
     */

    default Service drop (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.DROP
      );

      return this;

    }


    /**
     * Emit a {@link Signal#DROP} signal.
     *
     * @see #start()
     * @see Orientation#EMIT
     * @see Signal#DROP
     * @see Status#DEGRADED
     */
    default Service drop () {

      emit (
        Signal.DROP
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#DROP} signal.
     *
     * @see #call()
     * @see Orientation#RECEIPT
     * @see Signal#DROP
     * @see Status#DEGRADED
     */
    default Service dropped () {

      receipt (
        Signal.DROP
      );

      return this;

    }


    /**
     * Record a {@link Signal#REJECT} signal with a specified {@link Orientation}.
     *
     * @see Signal#REJECT
     * @see Status#DEGRADED
     * @see #reject()
     * @see #rejected()
     * @see #signal(Orientation, Signal)
     */

    default Service reject (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.REJECT
      );

      return this;

    }


    /**
     * Emit a {@link Signal#REJECT} signal.
     *
     * @see #start()
     * @see Orientation#EMIT
     * @see Signal#REJECT
     */

    default Service reject () {

      emit (
        Signal.REJECT
      );

      return this;

    }

    /**
     * Receipt of a {@link Signal#REJECT} signal.
     *
     * @see #call()
     * @see Orientation#RECEIPT
     * @see Signal#REJECT
     */

    default Service rejected () {

      receipt (
        Signal.REJECT
      );

      return this;

    }


    /**
     * Record a {@link Signal#RETRY} signal with a specified {@link Orientation}.
     *
     * @see Signal#RETRY
     * @see Status#DEGRADED
     * @see #retry()
     * @see #retried()
     * @see #signal(Orientation, Signal)
     */

    default Service retry (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.RETRY
      );

      return this;

    }


    /**
     * Emit a {@link Signal#RETRY} signal.
     *
     * @see #call()
     * @see Orientation#EMIT
     * @see Signal#RETRY
     * @see Status#DEGRADED
     */

    default Service retry () {

      emit (
        Signal.RETRY
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#RETRY} signal.
     *
     * @see #call()
     * @see Orientation#RECEIPT
     * @see Signal#RETRY
     * @see Status#DEGRADED
     */

    default Service retried () {

      receipt (
        Signal.RETRY
      );

      return this;

    }


    /**
     * Record a {@link Signal#DISCONNECT} signal with a specified {@link Orientation}.
     *
     * @see Signal#DISCONNECT
     * @see Status#DOWN
     * @see #disconnect()
     * @see #disconnected()
     * @see #signal(Orientation, Signal)
     */

    default Service disconnect (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.DISCONNECT
      );

      return this;

    }


    /**
     * Emit a {@link Signal#DISCONNECT} signal.
     *
     * @see #call()
     * @see Orientation#EMIT
     * @see Signal#DISCONNECT
     * @see Status#DOWN
     */

    default Service disconnect () {

      emit (
        Signal.DISCONNECT
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#DISCONNECT} signal.
     *
     * @see #call()
     * @see Orientation#EMIT
     * @see Signal#DISCONNECT
     * @see Status#DOWN
     */

    default Service disconnected () {

      receipt (
        Signal.DISCONNECT
      );

      return this;

    }


    /**
     * Record a {@link Signal#CALL} signal with a specified {@link Orientation}.
     *
     * @see Signal#CALL
     * @see #call()
     * @see #called()
     * @see #signal(Orientation, Signal)
     */

    default Service call (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.CALL
      );

      return this;

    }


    /**
     * Emit a {@link Signal#CALL} signal.
     *
     * @see #succeeded()
     * @see #failed()
     * @see Orientation#EMIT
     * @see Signal#CALL
     */

    default Service call () {

      emit (
        Signal.CALL
      );

      return this;

    }

    /**
     * Receipt of a {@link Signal#CALL} signal.
     *
     * @see #start()
     * @see #stop()
     * @see #fail()
     * @see #succeed()
     * @see Orientation#RECEIPT
     * @see Signal#CALL
     */

    default Service called () {

      receipt (
        Signal.CALL
      );

      return this;

    }


    /**
     * Record a {@link Signal#START} signal with a specified {@link Orientation}.
     *
     * @see Signal#START
     * @see #start()
     * @see #started()
     * @see #signal(Orientation, Signal)
     */

    default Service start (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.START
      );

      return this;

    }


    /**
     * Emit a {@link Signal#START} signal.
     *
     * @see #stop()
     * @see #fail()
     * @see #succeed()
     * @see Orientation#EMIT
     * @see Signal#START
     */

    default Service start () {

      emit (
        Signal.START
      );

      return this;

    }

    /**
     * Receipt of a {@link Signal#START} signal.
     *
     * @see #stopped()
     * @see #failed()
     * @see #succeeded()
     * @see Orientation#RECEIPT
     * @see Signal#START
     */

    default Service started () {

      receipt (
        Signal.START
      );

      return this;

    }


    /**
     * Record a {@link Signal#STOP} signal with a specified {@link Orientation}.
     *
     * @see Signal#STOP
     * @see #stop()
     * @see #stopped()
     * @see #signal(Orientation, Signal)
     */

    default Service stop (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.STOP
      );

      return this;

    }


    /**
     * Emit a @link {Signal#STOP} signal.
     *
     * @see #start()
     * @see #fail()
     * @see #succeed()
     * @see Orientation#EMIT
     * @see Signal#STOP
     */

    default Service stop () {

      emit (
        Signal.STOP
      );

      return this;

    }


    /**
     * Receipt of a @link {Signal#STOP} signal.
     *
     * @see #started()
     * @see #failed()
     * @see #succeeded()
     * @see Orientation#RECEIPT
     * @see Signal#STOP
     */

    default Service stopped () {

      receipt (
        Signal.STOP
      );

      return this;

    }


    /**
     * Record a {@link Signal#RECOURSE} signal with a specified {@link Orientation}.
     *
     * @see Signal#RECOURSE
     * @see Status#DEGRADED
     * @see #recourse()
     * @see #recoursed()
     * @see #signal(Orientation, Signal)
     */

    default Service recourse (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.RECOURSE
      );

      return this;

    }


    /**
     * Emit a {@link Signal#RECOURSE} signal.
     *
     * @see #call()
     * @see #recoursed()
     * @see Orientation#EMIT
     * @see Signal#RECOURSE
     * @see Status#DEGRADED
     */

    default Service recourse () {

      emit (
        Signal.RECOURSE
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#RECOURSE} signal.
     *
     * @see #call()
     * @see #recourse()
     * @see Orientation#RECEIPT
     * @see Signal#RECOURSE
     * @see Status#DEGRADED
     */

    default Service recoursed () {

      receipt (
        Signal.RECOURSE
      );

      return this;

    }


    /**
     * Record a {@link Signal#DELAY} signal with a specified {@link Orientation}.
     *
     * @see Signal#DELAY
     * @see Status#DEGRADED
     * @see #delay()
     * @see #delayed()
     * @see #signal(Orientation, Signal)
     */

    default Service delay (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.DELAY
      );

      return this;

    }


    /**
     * Emit a {@link Signal#DELAY} signal.
     *
     * @see #start()
     * @see Orientation#EMIT
     * @see Signal#DELAY
     * @see Status#DEGRADED
     */

    default Service delay () {

      emit (
        Signal.DELAY
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#DELAY} signal.
     *
     * @see #delay()
     * @see Orientation#RECEIPT
     * @see Signal#DELAY
     * @see Status#DEGRADED
     */

    default Service delayed () {

      receipt (
        Signal.DELAY
      );

      return this;

    }


    /**
     * Record a {@link Signal#SCHEDULE} signal with a specified {@link Orientation}.
     *
     * @see Signal#SCHEDULE
     * @see #schedule()
     * @see #scheduled()
     * @see #signal(Orientation, Signal)
     */

    default Service schedule (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.SCHEDULE
      );

      return this;

    }


    /**
     * Emit a {@link Signal#SCHEDULE} signal.
     *
     * @see #scheduled()
     * @see Orientation#EMIT
     * @see Signal#SCHEDULE
     */

    default Service schedule () {

      emit (
        Signal.SCHEDULE
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#SCHEDULE} signal.
     *
     * @see #call()
     * @see Orientation#RECEIPT
     * @see Signal#SCHEDULE
     */

    default Service scheduled () {

      receipt (
        Signal.SCHEDULE
      );

      return this;

    }


    /**
     * Record a {@link Signal#SUSPEND} signal with a specified {@link Orientation}.
     *
     * @see Signal#SUSPEND
     * @see Status#DEGRADED
     * @see #suspend()
     * @see #suspended()
     * @see #signal(Orientation, Signal)
     */

    default Service suspend (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.SUSPEND
      );

      return this;

    }


    /**
     * Emit a {@link Signal#SUSPEND} signal.
     *
     * @see #suspended()
     * @see Orientation#EMIT
     * @see Signal#SUSPEND
     * @see Status#DEGRADED
     */

    default Service suspend () {

      emit (
        Signal.SUSPEND
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#SUSPEND} signal.
     *
     * @see #suspend()
     * @see Orientation#RECEIPT
     * @see Signal#SUSPEND
     * @see Status#DEGRADED
     */

    default Service suspended () {

      receipt (
        Signal.SUSPEND
      );

      return this;

    }


    /**
     * Record a {@link Signal#RESUME} signal with a specified {@link Orientation}.
     *
     * @see Signal#RESUME
     * @see #resume()
     * @see #resumed()
     * @see #signal(Orientation, Signal)
     */

    default Service resume (
      final Orientation orientation
    ) {

      signal (
        orientation,
        Signal.RESUME
      );

      return this;

    }


    /**
     * Emit a {@link Signal#RESUME} signal.
     *
     * @see #resumed()
     * @see Orientation#EMIT
     * @see Signal#RESUME
     * @see Status#DEGRADED
     */

    default Service resume () {

      emit (
        Signal.RESUME
      );

      return this;

    }


    /**
     * Receipt of a {@link Signal#RESUME} signal.
     *
     * @see #suspend()
     * @see Orientation#RECEIPT
     * @see Signal#RESUME
     * @see Status#DEGRADED
     */

    default Service resumed () {

      receipt (
        Signal.RESUME
      );

      return this;

    }

  }

  /**
   * A (parent) marker interface for both {@link Signal} and {@link Status}.
   */

  public interface Phenomenon {

    /**
     * Returns the ordinal (position) of this phenomenon constant within its subclass (enum) set.
     *
     * @return The ordinal (position) of this phenomenon constant within its subclass (enum) set.
     * @see Enum#ordinal()
     */

    int ordinal ();

    /**
     * Returns the name of this phenomenon constant, as declared in its subclass (enum) set.
     *
     * @return The name of this phenomenon constant, as declared in its subclass (enum) set.
     * @see Enum#name()
     */

    String name ();

  }

  /**
   * A context represents some configured boundary within a process space.
   * <p>
   * Note: An SPI implementation of this interface is free to override
   * the default methods implementation included here.
   * <p>
   * There are two basic types of {@link Service} types that can be created within a context.
   * There is the {@link Service} which represents the self, or some sub-system of the self.
   * Then there are {@link Service} instances that represent the others as distinct from the self.
   * These others can depend on the self, as in they call on this self, or the self can
   * depend on them in its processing. Nothing in the creation of a {@link Service} defines these.
   * This is done via the {@link Environment} which defines a property representing the root of the
   * self and then there are the {@link Signal}s and the {@link Orientation} of the {@link Signal}s.
   *
   * @see Services#context(Environment)
   */

  public interface Context {

    /**
     * Returns the {@link Environment} associated with this context.
     *
     * @return A non-null reference to the {@link Environment} associated with context
     */

    Environment getEnvironment ();

    /**
     * Register a {@link Service} mapped to the {@link Name} within this context.
     *
     * @param name the name used for mapping to a {@link Service}
     * @return the registered {@link Service} mapped to a {@link Name}
     */

    Service service (
      Name name
    );

    /**
     * A shortcut method for {@code context.service(name(path)); }
     *
     * @param path the string to be parsed and transformed into a {@link Name}
     * @return The {@link Service} mapped to the created {@link Name}
     * @see Context#service(Name)
     */

    default Service service (
      final String path
    ) {

      return
        service (
          name (
            path
          )
        );

    }

    /**
     * Returns a {@link Stream} for iterating over the set of previously registered services.
     *
     * @return A non-null {@link Stream} of {@link Service} registrations
     */

    Stream< Service > services ();


    /**
     * Adds a {@link Subscriber} to receive signal and status updates.
     *
     * @param subscriber the subscriber to registered
     * @return The subscription used to control delivery of messages.
     */

    Subscription subscribe (
      Subscriber< ? super Phenomenon > subscriber
    );


    /**
     * Adds a {@link Subscriber} to receive {@link Signal} or {@link Status} updates.
     *
     * @param subscriber the subscriber to registered
     * @param type       the phenomenon class type of either {@link Signal} or {@link Status}
     * @return The subscription used to control delivery of messages.
     */

    < T extends Phenomenon > Subscription subscribe (
      Subscriber< T > subscriber,
      Class< T > type
    );

  }

  /**
   * Represents one or more name parts, much like a namespace,
   * used to identify a {@link Service} within a {@link Context}.
   * <p>
   * Note: An SPI implementation of this interface is free to override
   * the default methods implementation included here.
   *
   * @see Context#name(Class)
   * @see Context#name(String)
   * @see Context#name(Member)
   */

  public interface Name
    extends Iterable< Name >,
    Comparable< Name > {


    /**
     * The value for this name node.
     *
     * @return A non-null string value.
     */

    String getValue ();


    /**
     * The (parent) name prefixing this name.
     *
     * @return An {@link Optional} holding a reference to the name prefix or {@link Optional#empty()}.
     */

    Optional< Name > getPrefix ();


    /**
     * Returns a new name that has this name as a direct or indirect prefix.
     *
     * @param path the string to be parsed and appended to this name
     * @return A new name with the path appended as one or more name parts.
     */

    Name name ( String path );


    /**
     * Returns a new name that has this name as a direct prefix and a value of the enum name.
     *
     * @param value the enum to be appended to this name
     * @return A new name with the enum name appended a name part.
     */

    default Name name (
      final Enum< ? > value
    ) {

      return
        name (
          requireNonNull (
            value
          ).name ()
        );

    }


    /**
     * Returns a new name that has this name as a direct or indirect prefix.
     *
     * @param first  the first path to be appended to this name
     * @param second the second path to be appended to the result of the first path
     * @return A new name with both paths appended.
     */

    default Name name (
      final String first,
      final String second
    ) {

      return
        name ( first )
          .name ( second );

    }


    /**
     * Returns a new name that has this name as a direct or indirect prefix.
     *
     * @param path the name to be appended to this name
     * @return A new name with the path appended.
     */

    default Name name (
      final Name path
    ) {

      return
        path.foldTo (
          initial ->
            name (
              initial.getValue ()
            ),
          ( name, part ) ->
            name.name (
              part.getValue ()
            )
        );

    }


    /**
     * Returns a new extension of this name from iterating over a specified {@link Iterable} of {@link String} values.
     *
     * @param it the {@link Iterable} to be iterated over
     * @return A name as a result of the latest iteration of appendage
     * @throws NullPointerException if the {@link Iterable} is <tt>null</tt> or one of the values return is <tt>null</tt>null
     * @see Services#name(Iterable)
     */

    default Name name (
      final Iterable< String > it
    ) {

      return
        name (
          it,
          identity ()
        );

    }


    /**
     * Returns a new extension of this name from iterating over a specified {@link Iterable} and applying a transformation function.
     *
     * @param <T> the type of each value iterated over
     * @param it  the {@link Iterable} to be iterated over
     * @param fn  the function to be used to transform the type to a String type
     * @return A name as a result of the latest iteration of appendage
     * @throws NullPointerException if the {@link Iterable} is <tt>null</tt> or one of the values return is <tt>null</tt>
     * @see Services#name(Iterable, Function)
     */

    default < T > Name name (
      final Iterable< ? extends T > it,
      final Function< T, String > fn
    ) {

      return
        name (
          it.iterator (),
          fn
        );

    }

    /**
     * Returns a new extension of this name from iterating over a specified {@link Iterator} of {@link String} values.
     *
     * @param it the {@link Iterator} to be iterated over
     * @return A name as a result of the latest iteration of appendage
     * @throws NullPointerException if the {@link Iterable} is <tt>null</tt> or one of the values return is <tt>null</tt>null
     * @see Services#name(Iterable)
     */

    default Name name (
      final Iterator< String > it
    ) {

      return
        name (
          it,
          identity ()
        );

    }


    /**
     * Returns a new extension of this name from iterating over a specified {@link Iterator} and applying a transformation function.
     *
     * @param <T> the type of each value iterated over
     * @param it  the {@link Iterator} to be iterated over
     * @param fn  the function to be used to transform the type to a String type
     * @return A name as a result of the latest iteration of appendage
     * @throws NullPointerException if the {@link Iterator} is <tt>null</tt> or one of the values return is <tt>null</tt>
     * @see Services#name(Iterable, Function)
     */

    default < T > Name name (
      final Iterator< ? extends T > it,
      final Function< T, String > fn
    ) {

      Name name = this;

      while ( it.hasNext () ) {

        name =
          name.name (
            fn.apply (
              it.next ()
            )
          );

      }

      return
        name;

    }

    /**
     * Produces an accumulated value moving from left (root) to right (this) in the namespace.
     *
     * @param <T>         the return type of the accumulated value
     * @param initial     the function called to create the initial accumulated value
     * @param accumulator the function used to add a value to the accumulator
     * @return The accumulated result of performing the seed once and the accumulator.
     */

    < T > T foldTo (
      final Function< ? super Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Name, T > accumulator
    );


    /**
     * Produces an accumulated value moving from right (this) to left (root) in the namespace.
     *
     * @param <T>         the return type of the accumulated value
     * @param initial     the function called to create the initial accumulated value
     * @param accumulator the function used to add a value to the accumulator
     * @return The accumulated result of performing the seed once and the accumulator.
     */

    default < T > T foldFrom (
      final Function< ? super Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Name, T > accumulator
    ) {

      final Iterator< Name > it =
        iterator ();

      T result =
        initial.apply (
          it.next ()
        );

      while ( it.hasNext () ) {

        result =
          accumulator.apply (
            result,
            it.next ()
          );

      }

      return
        result;

    }


    /**
     * Returns an iterator over the name parts moving from right (this) to left (root).
     *
     * @return An iterator for moving over the name parts.
     */

    @Override
    default Iterator< Name > iterator () {

      //noinspection ReturnOfInnerClass
      return
        new Iterator< Name > () {

          private Name name =
            Name.this;

          @Override
          public boolean hasNext () {

            return
              name != null;

          }

          @Override
          public Name next () {

            final Name result = name;

            if ( result == null )
              throw new NoSuchElementException ();

            name =
              result
                .getPrefix ()
                .orElse ( null );

            return
              result;

          }
        };

    }

    /**
     * Creates a {@link Spliterator} over the name parts for this name.
     *
     * @return a {@code Spliterator} over the name parts.
     */

    @Override
    default Spliterator< Name > spliterator () {

      return
        Spliterators.spliterator (
          iterator (),
          foldFrom (
            self -> 1,
            ( sum, name ) -> sum + 1
          ).longValue (),
          DISTINCT | NONNULL | IMMUTABLE
        );

    }

    /**
     * Compares this {@code Name} instance with another.
     */

    @Override
    default int compareTo (
      final Name name
    ) {

      return
        this == name
        ? 0
        : toString ().compareTo ( name.toString () );

    }

    /**
     * Returns a {@link Stream} containing each name part starting with this name.
     *
     * @return A {@code Stream} in which the first element is this name, and the last is the root.
     */

    default Stream< Name > stream () {

      return
        StreamSupport.stream (
          spliterator (),
          false
        );

    }


    /**
     * Returns a {@link String} representation of the name,
     * with each name part separated by a <tt>"."</tt>.
     *
     * @return A non-null {@link String} representation.
     */

    @Override
    String toString ();

  }

  /**
   * An abstraction for interfacing with existing configuration libraries and stores.
   * <p>
   * Note: An SPI implementation of this interface is free to override the
   * default methods implementation included here.
   */

  @FunctionalInterface
  public interface Environment {

    /**
     * Returns an {@link Object} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    Optional< Object > getObject (
      Name name
    );


    /**
     * Returns the {@link Object} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Object getObject (
      final Name name,
      final Object defValue
    ) {

      return
        getObject ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a typed value mapped to a name.
     *
     * @param name the name to be matched
     * @param type the class type of the value returned
     * @param <T>  the class of the return type and default value
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default < T > Optional< T > getType (
      final Name name,
      final Class< ? extends T > type
    ) {

      return
        getObject ( name )
          .flatMap (
            object ->
              type.isInstance ( object )
              ? of ( type.cast ( object ) )
              : empty ()
          );

    }


    /**
     * Returns a typed value mapped to a name.
     *
     * @param name     the name to be matched
     * @param type     the class type of the value returned
     * @param <T>      the class of the return type and default value
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default < T > T getType (
      final Name name,
      final Class< T > type,
      final T defValue
    ) {

      return
        getType ( name, type )
          .orElse ( defValue );

    }


    /**
     * Returns a typed value mapped to a name.
     *
     * @param name   the name to be matched
     * @param type   the type of the value returned
     * @param alt    an alternative class type to be used as the base for a mapping
     * @param mapper the function used to transform the alternative type to the return type or <tt>null</tt>
     * @param <T>    the class of the return value type
     * @param <A>    the class of the alternative value type
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default < T, A > Optional< T > getType (
      final Name name,
      final Class< ? extends T > type,
      final Class< ? extends A > alt,
      final Function< ? super A, ? extends T > mapper
    ) {

      return
        getObject ( name )
          .flatMap (
            object -> {

              if ( type.isInstance ( object ) ) {

                return
                  of (
                    type.cast (
                      object
                    )
                  );

              } else if ( alt.isInstance ( object ) ) {

                return
                  ofNullable (
                    mapper.apply (
                      alt.cast (
                        object
                      )
                    )
                  );

              } else {

                return
                  empty ();

              }

            }
          );

    }


    /**
     * Returns a {@link String} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< String > getString (
      final Name name
    ) {

      return
        getType (
          name,
          String.class,
          CharSequence.class,
          CharSequence::toString
        );

    }


    /**
     * Returns a {@link String} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default String getString (
      final Name name,
      final String defValue
    ) {

      return
        getString ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link CharSequence} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< CharSequence > getCharSequence (
      final Name name
    ) {

      return
        getType (
          name,
          CharSequence.class
        );

    }


    /**
     * Returns a {@link CharSequence} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default CharSequence getCharSequence (
      final Name name,
      final CharSequence defValue
    ) {

      return
        getCharSequence ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Boolean} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< Boolean > getBoolean (
      final Name name
    ) {

      return
        getType (
          name,
          Boolean.class,
          String.class,
          Boolean::parseBoolean
        );

    }


    /**
     * Returns a {@link Boolean} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Boolean getBoolean (
      final Name name,
      final Boolean defValue
    ) {

      return
        getBoolean ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a boolean value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default boolean getBoolean (
      final Name name,
      final boolean defValue
    ) {

      return
        getBoolean ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Long} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched, or {@link Optional#empty()}.
     */

    default Optional< Long > getLong (
      final Name name
    ) {

      return
        getType (
          name,
          Long.class,
          String.class,
          Long::parseLong
        );

    }


    /**
     * Returns a long value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default long getLong (
      final Name name,
      final long defValue
    ) {

      return
        getLong ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Long} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Long getLong (
      final Name name,
      final Long defValue
    ) {

      return
        getLong ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Integer} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< Integer > getInteger (
      final Name name
    ) {

      return
        getType (
          name,
          Integer.class,
          String.class,
          Integer::parseInt
        );

    }


    /**
     * Returns an integer value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default int getInteger (
      final Name name,
      final int defValue
    ) {

      return
        getInteger ( name )
          .orElse ( defValue );

    }


    /**
     * Returns an integer value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Integer getInteger (
      final Name name,
      final Integer defValue
    ) {

      return
        getInteger ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Double} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< Double > getDouble (
      final Name name
    ) {

      return
        getType (
          name,
          Double.class,
          String.class,
          Double::parseDouble
        );

    }


    /**
     * Returns a {@link Double} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Double getDouble (
      final Name name,
      final Double defValue
    ) {

      return
        getDouble ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a double value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default double getDouble (
      final Name name,
      final double defValue
    ) {

      return
        getDouble ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@link Float} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the value matched or {@link Optional#empty()}.
     */

    default Optional< Float > getFloat (
      final Name name
    ) {

      return
        getType (
          name,
          Float.class,
          String.class,
          Float::parseFloat
        );

    }


    /**
     * Returns a {@link Float} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default float getFloat (
      final Name name,
      final Float defValue
    ) {

      return
        getFloat ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a float value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default float getFloat (
      final Name name,
      final float defValue
    ) {

      return
        getFloat ( name )
          .orElse ( defValue );

    }


    /**
     * Returns an {@link Enum} value mapped to a name.
     *
     * @param name the name to be matched
     * @param type the enum class type of the value returned
     * @param <T>  the enum class of the return type and default value
     * @return An {@link Optional} holding the enum value matched or {@link Optional#empty()}.
     */

    default < T extends Enum< T > > Optional< T > getEnum (
      final Name name,
      final Class< T > type
    ) {

      return
        getType (
          name,
          type,
          String.class,
          value ->
            Enum.valueOf (
              type,
              value
            )
        );

    }


    /**
     * Returns an {@link Enum} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param type     the enum class type of the value returned
     * @param <T>      the enum class of the return type and default value
     * @param defValue the default value to return if not matched
     * @return The {@link Enum} value matched, or the provided default value.
     */

    default < T extends Enum< T > > T getEnum (
      final Name name,
      final Class< T > type,
      final T defValue
    ) {

      return
        getEnum ( name, type )
          .orElse ( defValue );

    }


    /**
     * Returns an {@link Name} value mapped to a name.
     *
     * @param name the name to be matched
     * @return An {@link Optional} holding the enum value matched or {@link Optional#empty()}.
     */

    default Optional< Name > getName (
      final Name name
    ) {

      return
        getType (
          name,
          Name.class,
          String.class,
          Services::name
        );

    }


    /**
     * Returns an {@link Name} value mapped to a name.
     *
     * @param name     the name to be matched
     * @param defValue the default value to return if not matched
     * @return The value matched, or the provided default value.
     */

    default Name getName (
      final Name name,
      final Name defValue
    ) {

      return
        getName ( name )
          .orElse ( defValue );

    }


    /**
     * Returns a {@code Environment} that overrides (or supplies) the value for a particular path within an environment.
     *
     * @param <T>   the value type
     * @param name  the name to be matched
     * @param value the value returned on match
     * @return A {@code Environment} that overrides (or supplies) the value for a particular name within an environment.
     */

    default < T > Environment environment (
      final Name name,
      final T value
    ) {

      requireNonNull ( name );

      return
        lookup ->
          name == lookup
          ? ofNullable ( value )
          : getObject ( lookup );

    }

    /**
     * Returns a {@code Environment} that overrides (or supplies) the value for a particular path within an environment.
     *
     * @param <T>      the value type
     * @param name     the name to be matched
     * @param supplier the supplier used to provide a value on match
     * @return A {@code Environment} that overrides (or supplies) the value for a particular path within an environment.
     */

    default < T > Environment environment (
      final Name name,
      final Supplier< T > supplier
    ) {

      return
        lookup ->
          name == lookup
          ? ofNullable ( supplier.get () )
          : getObject ( lookup );

    }

    /**
     * Returns a {@code Environment} that first performs property look ups in a specified
     * {@code Environment} and then falls back to this {@code Environment}.
     *
     * @param environment the {@code Environment} used as the initial lookup target
     * @return A {@code Environment} that chains {@code this} environment, and the provided environment together.
     */

    default Environment environment (
      final Environment environment
    ) {

      return
        lookup -> {

          // java 9 introduces Optional.or(Supplier(Optional))

          final Optional< Object > result =
            environment.getObject ( lookup );

          return
            result.isPresent ()
            ? result
            : getObject ( lookup );

        };

    }

  }


  /**
   * A utility interface used for efficiently retrieving a value from an {@link Environment}.
   *
   * @param <T>
   */
  @FunctionalInterface
  public interface Variable< T > {

    /**
     * Returns a value from the environment of the {@link Context}.
     *
     * @param context the context used for sourcing a value
     * @return A value sourced from the context's environment, or the variable's default value.
     */

    default T of (
      final Context context
    ) {

      return
        of (
          context.getEnvironment ()
        );

    }

    /**
     * Returns a value from the environment.
     *
     * @param environment the environment used for sourcing a value
     * @return A value sourced from the environment, or the variable's default value.
     */

    T of (
      final Environment environment
    );

  }


  /**
   * An interface for registering interest in receiving {@link Signal} and {@link Status} changes.
   */

  @FunctionalInterface
  public interface Subscriber< T extends Phenomenon > {

    /**
     * A callback invoked only once for each particular {@link Service} since subscribing.
     *
     * @param name      the fully or partially qualified name of the service
     * @param registrar a {@link Consumer} that accepts a {@link Callback} for updates notifications
     */

    void accept (
      Name name,
      Consumer< Callback< ? super T > > registrar
    );

  }

  /**
   * An interface used for unregistering interest in receiving {@link Signal} and {@link Status} changes.
   */

  @FunctionalInterface
  public interface Subscription {

    /**
     * Cancels the subscription.
     */

    void cancel ();

  }


  /**
   * An interface used to receive {@link Signal} or {@link Status} changes for a particular {@link Service}.
   */

  @FunctionalInterface
  public interface Callback< T extends Phenomenon > {

    /**
     * Called when a {@link Service} has fired a signal or has had its status updated.
     *
     * @param orientation the orientation of the change
     * @param value       the value changing
     */

    void accept (
      final Orientation orientation,
      final T value
    );

    /**
     * Returns a new callback that first calls this callback and then afterwards calls on the other specified callback.
     *
     * @param after the callback to be called following processing by this callback
     * @return A new callback that first calls this callback and then afterwards calls on the other specified callback.
     */

    default Callback< T > andThen (
      final Callback< ? super T > after
    ) {

      requireNonNull ( after );

      return
        ( orientation, value ) -> {

          accept (
            orientation,
            value
          );

          after.accept (
            orientation,
            value
          );

        };

    }

  }

}
