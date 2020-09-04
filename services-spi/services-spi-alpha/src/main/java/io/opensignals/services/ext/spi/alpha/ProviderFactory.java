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

import io.opensignals.services.spi.ServicesProvider;
import io.opensignals.services.spi.ServicesProviderFactory;

/**
 * The SPI implementation of {@link ServicesProviderFactory}.
 *
 * @author wlouth
 * @since 1.0
 */

public final class ProviderFactory
  implements ServicesProviderFactory {

  @Override
  public ServicesProvider create () {

    return
      Provider.INSTANCE;

  }

}
