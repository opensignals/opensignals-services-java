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

import io.opensignals.services.Services.Name;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static io.opensignals.services.Services.name;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test class for the {@link Services.Name} interface.
 *
 * @author wlouth
 * @since 1.0
 */

final class NameTest {

  private static final String ROOT_VALUE = "root";
  private static final String ROOT_PATH  = ROOT_VALUE;
  private static final Name   ROOT_NAME  = name ( ROOT_PATH );

  private final Function< Name, Integer > LENGTH =
    ( n ) -> n.getValue ().length ();

  private final ToIntFunction< Name > SUM =
    LENGTH::apply;

  @Test
  void root () {

    final Name name =
      name (
        ROOT_PATH
      );

    assertEquals (
      ROOT_VALUE,
      name.getValue ()
    );

    assertFalse (
      name
        .getPrefix ()
        .isPresent ()
    );

    assertSame (
      ROOT_NAME,
      name
    );

    assertEquals (
      ROOT_NAME,
      name
    );

    assertEquals (
      LENGTH.apply ( name ),
      name.foldFrom (
        LENGTH,
        ( v, n ) ->
          v + LENGTH.apply ( n )
      )
    );

    assertEquals (
      LENGTH.apply ( name ),
      (int) name.foldTo (
        n ->
          LENGTH.apply ( name ),
        ( v, n ) ->
          LENGTH.apply ( name ) + v
      )
    );

  }

  private static final String DOT        = ".";
  private static final String NODE_VALUE = "node";
  private static final String NODE_PATH  = ROOT_PATH + DOT + NODE_VALUE;
  private static final Name   NODE_NAME  = name ( NODE_PATH );

  @Test
  void node () {

    final Name name =
      name (
        NODE_PATH
      );

    assertEquals (
      NODE_VALUE,
      name.getValue ()
    );

    assertTrue (
      name
        .getPrefix ()
        .isPresent ()
    );

    assertEquals (
      Optional.of (
        ROOT_NAME
      ),
      name.getPrefix ()
    );

    assertSame (
      NODE_NAME,
      name
    );

    assertEquals (
      NODE_NAME,
      name
    );

    assertSame (
      name,
      ROOT_NAME.name (
        NODE_VALUE
      )
    );

    assertSame (
      name,
      name (
        ROOT_VALUE,
        NODE_VALUE
      )
    );

    assertSame (
      name.name (
        name
      ),
      name.name (
        name
      )
    );

    assertSame (
      name,
      name
        .name ( name )
        .getPrefix ()
        .flatMap ( Name::getPrefix )
        .orElseThrow ( AssertionError::new )
    );

    assertEquals (
      name
        .stream ()
        .mapToInt ( SUM )
        .sum (),
      name.foldFrom (
        LENGTH,
        ( v, n ) ->
          v + LENGTH.apply ( n )
      )
    );

    assertEquals (
      name
        .stream ()
        .mapToInt ( SUM )
        .sum (),
      (int) name.foldTo (
        n ->
          LENGTH.apply ( name ),
        ( v, n ) ->
          LENGTH.apply ( name ) + v
      )
    );

  }

}
