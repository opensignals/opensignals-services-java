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

package io.opensignals.services.testkit;

import io.opensignals.services.Services;
import io.opensignals.services.Services.*;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.Spliterator.*;

/**
 * The TestKit utility class with recording capabilities.
 *
 * @author wlouth
 * @version 1.0
 */

public final class TestKit {

  private TestKit () {}

  /**
   * Returns a new recorder instance.
   *
   * @return A new recorder instance
   */

  public static < T extends Phenomenon > Recorder< T > recorder (
    final Context context,
    final Class< ? extends T > type
  ) {

    return
      new CaptureRecorder<> ( context, type );

  }

  /**
   * Returns a new "initial" captured change event with the specified fields set.
   *
   * @param name        the name of the service for the event
   * @param orientation the orientation of the change event
   * @param value       the value that was emitted or received
   * @return A new capture with the previous property set to <tt>null</tt>
   */

  public static < T extends Phenomenon > Capture< T > capture (
    final Name name,
    final Orientation orientation,
    final T value
  ) {

    return
      new CaptureRecord<> (
        name,
        orientation,
        value,
        null
      );

  }

  /**
   * Returns a new "initial" captured change event with the specified fields set.
   *
   * @param name        the name of the service for the event
   * @param orientation the orientation of the change event
   * @param value       the value that was emitted or received
   * @param previous    the capture prior to this or null
   * @return A new capture with the previous property set to previous parameter value
   */

  public static < T extends Phenomenon > Capture< T > capture (
    final Name name,
    final Orientation orientation,
    final T value,
    final Capture< T > previous
  ) {

    return
      previous == null ?
      capture (
        name,
        orientation,
        value
      ) :
      previous.to (
        name,
        orientation,
        value
      );

  }

  /**
   * An extension of the {@link Services.Subscriber} interface that records captured change events.
   *
   * @param <T> The phenomenon type
   */

  public interface Recorder< T extends Phenomenon > {

    /**
     * Starts capturing change events.
     */

    void start ();

    /**
     * Stops recording and returns a linked list of captured change events.
     *
     * @return The last recorded capture or {@code Optional.empty()}
     */

    Optional< Capture< T > > stop ();

  }


  /**
   * Represents a chained linked list of captured change events with
   * the most recent being the head of the linked list.
   */

  public interface Capture< T extends Phenomenon > extends Iterable< Capture< T > > {

    /**
     * Returns the name of the service for the change event.
     *
     * @return The name of the service
     */

    Name getName ();

    /**
     * Returns the orientation of the phenomenon.
     *
     * @return The orientation of the phenomenon
     */

    Orientation getOrientation ();

    /**
     * Returns the value for the phenomenon type.
     *
     * @return The value for the phenomenon type
     */

    T getValue ();

    /**
     * The capture that occurred immediately prior to this.
     *
     * @return The previous recorded capture or {@code Optional.empty()}
     */

    Optional< Capture< T > > getPrevious ();

    /**
     * Returns a new captured change event that follows on from this capture.
     *
     * @param name        the name of the service for the event
     * @param orientation the mode of the change event
     * @param value       the value that was emitted or received
     * @return A new capture with the previous property set to this capture
     */

    Capture< T > to (
      final Name name,
      final Orientation orientation,
      final T value
    );

    /**
     * Returns a new captured change event that follows on from this capture
     * with only the value property changed.
     *
     * @param value the value that was emitted or received
     * @return A new capture with the previous property set to this capture
     */

    Capture< T > to (
      final T value
    );


    /**
     * Returns a new captured change event that follows on from this capture
     * with the orientation and value property values changed.
     *
     * @param orientation the orientation of the change event
     * @param value       the value that was emitted or received
     * @return A new capture with the previous property set to this capture
     */

    Capture< T > to (
      final Orientation orientation,
      final T value
    );

    /**
     * Returns a stream of captured change events starting with this capture.
     *
     * @return A stream of captures
     */

    Stream< Capture< T > > stream ();

    /**
     * The total number of captures preceding this capture plus this capture.
     *
     * @return The number of captures in the linked list including this capture
     */

    int size ();


  }

  /**
   * A struct like object for holding a Capture reference
   */

  public static final class Closure< T extends Phenomenon > {

    @SuppressWarnings ( "squid:ClassVariableVisibilityCheck" )
    public Capture< T > capture;

  }

  private static final class CaptureRecorder< T extends Phenomenon >
    implements Recorder< T > {

    private final Services.Context     context;
    private final Class< ? extends T > type;
    private final Object               lock = new Object ();
    private       State< T >           state;

    CaptureRecorder (
      final Context context,
      final Class< ? extends T > type
    ) {

      this.context =
        context;

      this.type =
        type;

    }


    @Override
    public void start () {

      synchronized ( lock ) {

        if ( isNull ( state ) ) {

          final State< T > initial
            = new State<> ();

          initial.subscription =
            context.subscribe (
              ( name, registrar ) ->
                registrar.accept (
                  ( orientation, value ) ->
                    initial.records =
                      new CaptureRecord<> (
                        name,
                        orientation,
                        value,
                        initial.records
                      )
                ),
              type
            );

          state =
            initial;

        }

      }

    }

    @Override
    public Optional< Capture< T > > stop () {

      final Capture< T > records;

      synchronized ( lock ) {

        final State< T > current =
          state;

        if ( nonNull ( current ) ) {

          current
            .subscription
            .cancel ();

          records =
            current.records;

          state =
            null;

        } else {

          records =
            null;

        }

      }

      return
        ofNullable (
          records
        );

    }

    private static final class State< T extends Phenomenon > {

      Subscription       subscription;
      CaptureRecord< T > records;

      State () {}
    }

  }


  private static final class CaptureRecord< P extends Phenomenon > implements Capture< P > {

    final         CaptureRecord< P > previous;
    private final Name               name;
    private final Orientation        orientation;
    private final P                  value;
    private final int                hashCode;
    private final int                index;

    @SuppressWarnings ( "AssignmentToMethodParameter" )
    private static boolean compare (
      CaptureRecord< ? > left,
      CaptureRecord< ? > right,
      final int count
    ) {

      for (
        int i = count;
        i >= 0;
        i--
      ) {

        if (
          left.name == right.name &&
            left.orientation == right.orientation &&
            left.value == right.value
        ) {

          left =
            left.previous;

          right =
            right.previous;

        } else {

          return false;

        }

      }

      return true;

    }


    CaptureRecord (
      final Name name,
      final Orientation orientation,
      final P value,
      final CaptureRecord< P > previous
    ) {

      this.name =
        name;

      this.orientation =
        orientation;

      this.value =
        value;

      this.previous =
        previous;

      hashCode =
        Objects.hash (
          name,
          orientation,
          value,
          previous
        );

      index =
        nonNull ( previous )
        ? previous.index + 1
        : 0;

    }

    @Override
    public Name getName () {

      return name;

    }

    @Override
    public Orientation getOrientation () {

      return orientation;

    }

    @Override
    public P getValue () {

      return value;

    }

    @Override
    public Optional< Capture< P > > getPrevious () {

      return
        ofNullable (
          previous
        );

    }

    @Override
    public Capture< P > to (
      final Name name,
      final Orientation orientation,
      final P value
    ) {

      return
        new CaptureRecord<> (
          name,
          orientation,
          value,
          this
        );

    }

    @Override
    public Capture< P > to (
      final P value
    ) {

      return
        new CaptureRecord<> (
          name,
          orientation,
          value,
          this
        );

    }

    @Override
    public Capture< P > to (
      final Orientation orientation,
      final P value
    ) {

      return
        new CaptureRecord<> (
          name,
          orientation,
          value,
          this
        );

    }

    @Override
    public Stream< Capture< P > > stream () {

      return
        StreamSupport.stream (
          spliterator (),
          false
        );

    }

    @Override
    public int size () {

      return index + 1;

    }

    @Override
    public int hashCode () {

      return hashCode;

    }

    @Override
    public boolean equals (
      final Object o
    ) {

      if ( this == o ) {
        return true;
      }

      if (
        o == null ||
          getClass () != o.getClass ()
      ) {
        return false;
      }

      final CaptureRecord< ? > capture =
        (CaptureRecord< ? >) o;

      return
        index == capture.index &&
          compare (
            this,
            capture,
            index
          );

    }

    @Override
    public String toString () {

      final StringBuilder result =
        new StringBuilder ( 200 );

      CaptureRecord< P > record =
        this;

      do {

        result
          .append ( "CaptureRecord: { " )
          .append ( "name: " ).append ( record.name ).append ( ", " )
          .append ( "orientation: " ).append ( record.orientation ).append ( ", " )
          .append ( "value: " ).append ( value ).append ( ", " )
          .append ( "capture: " );

      } while (
        ( record = record.previous ) != null
      );

      result.append ( "null }" );

      for (
        int i = index;
        i > 0;
        i--
      ) {

        result.append ( "}" );

      }

      return
        result.toString ();

    }

    @Override
    public Iterator< Capture< P > > iterator () {

      //noinspection ReturnOfInnerClass
      return
        new Iterator< Capture< P > > () {

          CaptureRecord< P > current =
            CaptureRecord.this;

          @Override
          public boolean hasNext () {

            return
              current != null;

          }

          @Override
          public CaptureRecord< P > next () {

            final CaptureRecord< P > result =
              current;

            if ( result != null ) {

              current =
                result.previous;

              return result;

            } else {

              throw
                new NoSuchElementException ();

            }

          }

        };

    }

    @Override
    public Spliterator< Capture< P > > spliterator () {

      //noinspection ImplicitNumericConversion
      return
        Spliterators.spliterator (
          iterator (),
          size (),
          DISTINCT |
            NONNULL |
            IMMUTABLE
        );

    }

  }

}
