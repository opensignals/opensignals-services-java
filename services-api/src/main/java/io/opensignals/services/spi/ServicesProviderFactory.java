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

package io.opensignals.services.spi;

import io.opensignals.services.Services;

/**
 * The service provider factory interface for the services runtime.
 *
 * @author wlouth
 * @since 1.0
 */

public interface ServicesProviderFactory {

  /**
   * A method called by the {@link Services Services} to create the
   * provider that will be used as the delegate for all entry point calls.
   *
   * @return an instance of {@link ServicesProvider ServicesProvider}
   */

  ServicesProvider create ();

}

