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

package io.opensignals.services.samples;

final class Strings {

  private static final String DOT                             = ".";
  private static final String LOCAL                           = "local";
  private static final String REMOTE                          = "remote";
  private static final String SERVICES                        = "services";
  private static final String CONTEXT                         = "context";
  private static final String ID                              = "id";
  static final         String SERVICE                         = "service";
  static final         String IO                              = "io";
  static final         String OPENSIGNALS                     = "opensignals";
  static final         String ONE                             = "one";
  static final         String STRING                          = "string";
  static final         String INTEGER                         = "integer";
  static final         String PARENT                          = "parent";
  static final         String CHILD                           = "child";
  static final         String NAME                            = "name";
  static final         String TWO                             = "two";
  static final         String CHROMOSOMES                     = "chromosomes";
  static final         String SERVICE_LOCAL                   = SERVICE + DOT + LOCAL;
  static final         String SERVICE_REMOTE                  = SERVICE + DOT + REMOTE;
  static final         String SERVICE_ONE                     = SERVICE + DOT + ONE;
  static final         String SERVICE_TWO                     = SERVICE + DOT + TWO;
  static final         String SERVICE_1                       = SERVICE + DOT + "1";
  static final         String OPENSIGNALS_SERVICES_CONTEXT_ID = OPENSIGNALS + DOT + SERVICES + DOT + CONTEXT + DOT + ID;
  static final         String IO_OPENSIGNALS_ONE              = IO + DOT + OPENSIGNALS + DOT + ONE;
  static final         String IO_OPENSIGNALS                  = IO + DOT + OPENSIGNALS;

  private Strings () {}

  @SuppressWarnings ( "SameParameterValue" )
  static String times (
    final CharSequence chars,
    final int count
  ) {

    final StringBuilder sb =
      new StringBuilder (
        count * chars.length ()
      );

    for (
      int i = count;
      i > 0;
      i--
    ) {

      sb.append (
        chars
      );

    }

    return sb.toString ();

  }

}
