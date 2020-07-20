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

import java.lang.reflect.Member;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;

/**
 * @author wlouth
 * @since 1.0
 */

@SuppressWarnings (
  {
    "ImplicitNumericConversion",
    "HardcodedFileSeparator"
  }
)
final class Names {

  private static final char                              DOT     = '.';
  private static final int                               INDEX   = DOT;
  private static final Pattern                           PATTERN = compile ( "\\" + DOT );
  private static final ConcurrentHashMap< String, Name > MAP     =
    new ConcurrentHashMap<> ( 1009 );

  private static IllegalArgumentException illegalArgument (
    final String path
  ) {

    return
      new IllegalArgumentException (
        "Invalid Path Specification: " + path
      );

  }

  private static boolean isNotEmpty (
    final CharSequence path
  ) {

    return path.length () > 0;

  }

  private static boolean isEmpty (
    final CharSequence path
  ) {

    return path.length () == 0;

  }

  private static Name lookup (
    final String path
  ) {

    final Name name =
      MAP.get (
        path
      );

    return
      name != null ?
      name :
      parse (
        path
      );

  }

  private static Name parse (
    final String path
  ) {

    final Name name =
      checkName (
        parseOrNull (
          path
        ),
        path
      );

    MAP.put (
      path,
      name
    );

    return
      name;

  }

  private static Name parseOrNull (
    final CharSequence path
  ) {

    final Closure closure =
      new Closure ();

    splitAsStream (
      path
    ).forEachOrdered (

      part ->
        addNameTo (
          part,
          closure
        )

    );

    return
      closure.value;

  }

  private static void addNameTo (
    final String part,
    final Closure closure
  ) {

    final Name prefix =
      closure.value;

    closure.value =
      prefix == null ?
      root ( part ) :
      prefix.node ( part );

  }


  private Names () {}

  static Name of (
    final Class< ? > cls
  ) {

    return
      lookup (
        cls.getName ()
      );

  }

  static Name of (
    final Member member
  ) {

    return
      lookup (
        member.getDeclaringClass ().getName ()
      ).node (
        member.getName ()
      );

  }

  @SuppressWarnings ( "WeakerAccess" )
  static Stream< String > splitAsStream (
    final CharSequence name
  ) {

    return
      PATTERN.splitAsStream (
        name
      ).filter (
        Names::isNotEmpty
      );

  }

  @SuppressWarnings ( "WeakerAccess" )
  static < T > T getOrAdd (
    final String path,
    final ConcurrentHashMap< String, T > names,
    final Function< ? super String, ? extends T > mapper
  ) {

    final T name =
      names.get (
        path
      );

    return
      name != null ?
      name :
      names.computeIfAbsent (
        path,
        mapper
      );

  }

  static Name of (
    final String path
  ) {

    checkPath (
      path
    );

    return
      isCompositePath (
        path
      ) ?
      lookup (
        path
      ) :
      root ( path );

  }

  static Name root (
    final String path
  ) {

    return
      getOrAdd (
        path,
        MAP,
        Name::new
      );

  }

  @SuppressWarnings ( "WeakerAccess" )
  static void checkPath (
    final String path
  ) {

    if ( isEmpty ( path ) ) {

      throw
        illegalArgument (
          path
        );

    }

  }

  @SuppressWarnings ( "WeakerAccess" )
  static boolean isCompositePath (
    final String path
  ) {

    return
      path.indexOf (
        INDEX
      ) != -1;

  }

  @SuppressWarnings ( "WeakerAccess" )
  static < T extends Services.Name > T checkName (
    final T name,
    final String path
  ) {

    if ( name == null ) {
      throw
        illegalArgument (
          path
        );
    }

    return name;

  }

  /**
   * The SPI implementation of {@link Services.Name}.
   *
   * @author wlouth
   * @since 1.0
   */

  static final class Name
    implements Services.Name {

    @SuppressWarnings ( {"rawtypes", "java:S3740"} )
    private static final AtomicReferenceFieldUpdater< Name, ConcurrentHashMap > UPDATER =
      AtomicReferenceFieldUpdater.newUpdater (
        Name.class,
        ConcurrentHashMap.class,
        "cache"
      );

    private final String                            value;
    private final Name                              prefix;
    volatile      ConcurrentHashMap< String, Name > cache;
    private       String                            path;

    private static ConcurrentHashMap< String, Name > createCache () {

      return new ConcurrentHashMap<> ();

    }

    private static < T > T foldTo (
      final Name name,
      final Function< ? super Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Name, T > accumulator
    ) {

      final Name prefix =
        name.prefix;

      return
        prefix != null ?
        accumulator.apply (
          foldTo (
            prefix,
            initial,
            accumulator
          ),
          name
        ) :
        initial.apply (
          name
        );

    }

    private static < T > T foldFrom (
      final Name name,
      final BiFunction< ? super T, ? super Name, T > accumulator,
      final T value
    ) {

      return
        name != null
        ? name.foldFrom (
          accumulator,
          value
        )
        : value;

    }


    Name (
      final String value
    ) {

      prefix =
        null;

      this.value =
        value;

    }

    private Name (
      final Name prefix,
      final String value
    ) {

      this.prefix =
        prefix;

      this.value =
        value;

    }

    Name left () {

      return
        prefix;

    }

    private String path () {

      return
        foldTo (
          this,
          seed ->
            new StringBuilder (
              seed.value
            ),
          ( builder, node ) ->
            builder.append (
              DOT
            ).append (
              node.value
            )
        ).toString ();

    }

    @Override
    public String toString () {

      final String result =
        path;

      return
        result != null
        ? result
        : ( path = path () );

    }

    Name node (
      final String single
    ) {

      return
        getOrAdd (
          single,
          cache (),
          part ->
            new Name (
              this,
              part
            )
        );

    }

    Name node (
      final Enum< ? > value
    ) {

      return
        node (
          value.name ()
        );

    }

    private Name getOrParse (
      final String string
    ) {

      final ConcurrentHashMap< String, Name > nodes =
        cache ();

      final Name node =
        nodes.get (
          string
        );

      return
        node != null
        ? node
        : parseAndMaybeCacheNode ( string, nodes );

    }

    private Name parseAndMaybeCacheNode (
      final String string,
      final Map< ? super String, ? super Name > nodes
    ) {

      final Name node =
        checkName (
          parseNodeOrNull (
            string
          ),
          string
        );

      if ( isNotChild ( node ) ) {

        nodes.put (
          string,
          node
        );

      }

      return
        node;

    }

    private boolean isNotChild (
      final Name child
    ) {

      return
        child.prefix != this;

    }

    private Name parseNodeOrNull (
      final CharSequence sequence
    ) {

      return
        splitAsStream (
          sequence
        ).reduce (
          null,
          ( name, single ) ->
            name == null
            ? node ( single )
            : name.node ( single ),
          ( n1, n2 ) -> n2
        );

    }

    private ConcurrentHashMap< String, Name > cache () {

      final ConcurrentHashMap< String, Name > map =
        cache;

      return
        map != null ?
        map :
        updateAndGetCache ();

    }

    @SuppressWarnings ( "unchecked" )
    private ConcurrentHashMap< String, Name > updateAndGetCache () {

      return
        UPDATER
          .updateAndGet (
            this,
            prev ->
              prev == null
              ? createCache ()
              : prev
          );

    }

    @Override
    public String getValue () {

      return
        value;

    }

    @Override
    public Optional< Services.Name > getPrefix () {

      return
        ofNullable (
          prefix
        );

    }

    @Override
    public Name name (
      final String path
    ) {

      requireNonNull (
        path
      );

      checkPath (
        path
      );

      return
        isCompositePath ( path )
        ? getOrParse ( path )
        : node ( path );

    }

    @Override
    public Name name (
      final Enum< ? > value
    ) {

      return
        node (
          value.name ()
        );

    }

    @Override
    public Name name (
      final Services.Name path
    ) {

      //noinspection CastToConcreteClass
      return
        name (
          (Name) path
        );

    }

    @Override
    public < T > T foldTo (
      final Function< ? super Services.Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Services.Name, T > accumulator
    ) {

      return
        foldTo (
          this,
          initial,
          accumulator
        );

    }

    @Override
    public < T > T foldFrom (
      final Function< ? super Services.Name, ? extends T > initial,
      final BiFunction< ? super T, ? super Services.Name, T > accumulator
    ) {

      final Name name =
        prefix;

      final T tally =
        initial.apply (
          this
        );

      return
        name != null
        ? name.foldFrom (
          accumulator,
          tally
        )
        : tally;

    }

    @Override
    public java.util.Iterator< Services.Name > iterator () {

      return
        new Iterator (
          this
        );

    }

    Names.Name name (
      final Names.Name path
    ) {

      return
        path
          .foldTo (
            node (
              path.value
            ),
            ( t, p ) ->
              t.node (
                p.value
              )
          );

    }

    private < T > T foldFrom (
      final BiFunction< ? super T, ? super Name, T > accumulator,
      final T seed
    ) {

      return
        foldFrom (
          prefix,
          accumulator,
          accumulator.apply (
            seed,
            this
          )
        );

    }

    private < T > T foldTo (
      final T initial,
      final BiFunction< T, Name, T > accumulator
    ) {

      return
        prefix != null
        ? accumulator
          .apply (
            prefix.foldTo (
              initial,
              accumulator
            ),
            this
          )
        : initial;

    }

    @Override
    public void forEach (
      final Consumer< ? super Services.Name > action
    ) {

      Name name = this;

      do {

        action.accept (
          name
        );

      } while (
        ( name = name.prefix ) != null
      );

    }

    private static final class Iterator
      implements java.util.Iterator< Services.Name > {

      Name name;

      Iterator (
        final Name name
      ) {

        this.name =
          name;

      }

      @Override
      public boolean hasNext () {

        return
          name != null;

      }

      @Override
      public Name next () {

        final Name result =
          name;

        if ( result != null ) {

          name =
            result.left ();

          return
            result;

        }

        throw
          new NoSuchElementException ();

      }

    }


  }

  /*
   * A struct like class for reduce/collect like operations
   */

  private static final class Closure {

    Name value;

    Closure () {}

  }

}
